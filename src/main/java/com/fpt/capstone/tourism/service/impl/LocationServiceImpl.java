package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.request.LocationRequestDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.validator.Validator;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import com.fpt.capstone.tourism.service.LocationService;
import com.fpt.capstone.tourism.service.S3Service;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fpt.capstone.tourism.constants.Constants.Message.*;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final TourRepository tourRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket-url}")
    private String bucketUrl;

    @Override
    public List<LocationDTO> getAllDepartures() {
        return locationRepository.findByDeletedFalseOrderByNameAsc()
                .stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getAllDestinations() {
        return locationRepository.findByDeletedFalseOrderByNameAsc()
                .stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private GeneralResponse<PagingDTO<LocationDTO>> buildPagedResponse(Page<Location> locationPage) {
        List<LocationDTO> locationDTOS = locationPage.getContent().stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());

        PagingDTO<LocationDTO> pagingDTO = PagingDTO.<LocationDTO>builder()
                .page(locationPage.getNumber())
                .size(locationPage.getSize())
                .total(locationPage.getTotalElements())
                .items(locationDTOS)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), GET_LOCATIONS_SUCCESS, pagingDTO);
    }

    @Override
    public GeneralResponse<LocationDTO> saveLocation(LocationRequestDTO locationRequestDTO, MultipartFile file) {
        try {
            //Validate input data
            Validator.validateLocation(locationRequestDTO);
            if (file == null || file.isEmpty()) {
                throw BusinessException.of(EMPTY_LOCATION_IMAGE);
            }
            //Check duplicate location
            if (locationRepository.findByName(locationRequestDTO.getName()) != null) {
                throw BusinessException.of(EXISTED_LOCATION);
            }

            //Save date to database
            Location location = locationMapper.toEntity(locationRequestDTO);
            String key = s3Service.uploadFile(file, "locations");
            location.setImage(bucketUrl + "/" + key);
            location.setCreatedAt(LocalDateTime.now());
            location.setDeleted(false);
            locationRepository.save(location);

            LocationDTO locationDTO = locationMapper.toDTO(location);

            return new GeneralResponse<>(HttpStatus.OK.value(), CREATE_LOCATION_SUCCESS, locationDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(CREATE_LOCATION_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<LocationDTO> getLocationById(Long id) {
        try {
            Location location = locationRepository.findById(id).orElseThrow();

            LocationDTO locationDTO = locationMapper.toDTO(location);
            return new GeneralResponse<>(HttpStatus.OK.value(), GENERAL_SUCCESS_MESSAGE, locationDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GENERAL_FAIL_MESSAGE, ex);
        }
    }


    @Override
    public GeneralResponse<LocationDTO> deleteLocation(Long id, boolean isDeleted) {
        try {
            Location location = locationRepository.findById(id).orElseThrow();

            location.setDeleted(isDeleted);
            location.setUpdatedAt(LocalDateTime.now());
            locationRepository.save(location);

            LocationDTO locationDTO = locationMapper.toDTO(location);
            return new GeneralResponse<>(HttpStatus.OK.value(), GENERAL_SUCCESS_MESSAGE, locationDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GENERAL_FAIL_MESSAGE, ex);
        }
    }

    @Override
    public GeneralResponse<PagingDTO<LocationDTO>> getListLocation(int page, int size, String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Location> locationPage;
            if (keyword != null && !keyword.isEmpty()) {
                locationPage = locationRepository.findByNameContainingIgnoreCase(keyword, pageable);
            } else {
                locationPage = locationRepository.findAll(pageable);
            }
            return buildPagedResponse(locationPage);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GET_LOCATIONS_FAIL, ex);
        }
    }



    @Override
    public GeneralResponse<LocationDTO> updateLocation(Long id, LocationRequestDTO locationRequestDTO, MultipartFile file) {
        try {
            Validator.validateLocation(locationRequestDTO);
            Location location = locationRepository.findById(id).orElseThrow();
            location.setName(locationRequestDTO.getName());
            location.setDescription(locationRequestDTO.getDescription());
            if (file != null && !file.isEmpty()) {
                String key = s3Service.uploadFile(file, "locations");
                location.setImage(bucketUrl + "/" + key);
            }
            location.setUpdatedAt(LocalDateTime.now());
            locationRepository.save(location);
            LocationDTO locationDTO = locationMapper.toDTO(location);
            return new GeneralResponse<>(HttpStatus.OK.value(), GENERAL_SUCCESS_MESSAGE, locationDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GENERAL_FAIL_MESSAGE, ex);
        }
    }




}
