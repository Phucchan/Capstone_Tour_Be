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
import com.fpt.capstone.tourism.service.LocationService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    @Override
    public GeneralResponse<LocationDTO> saveLocation(LocationRequestDTO locationRequestDTO) {
        try {
            //Validate input data
            Validator.validateLocation(locationRequestDTO);

            //Check duplicate location
            if (locationRepository.findByName(locationRequestDTO.getName()) != null) {
                throw BusinessException.of(EXISTED_LOCATION);
            }

            //Save date to database
            Location location = locationMapper.toEntity(locationRequestDTO);
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
    public GeneralResponse<PagingDTO<LocationDTO>> getListLocation(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Location> locationPage = locationRepository.findAll(pageable);
            return buildPagedResponse(locationPage);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GET_LOCATIONS_FAIL, ex);
        }
    }

    private Specification<Location> buildSearchSpecification(String keyword, Boolean isDeleted) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("name"), "%" + keyword + "%");
                predicates.add(namePredicate);
            }

            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("deleted"), isDeleted));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    // SỬA LẠI TOÀN BỘ PHƯƠNG THỨC NÀY
    private GeneralResponse<PagingDTO<LocationDTO>> buildPagedResponse(Page<Location> locationPage) {
        // Chuyển đổi List<Location> sang List<LocationDTO>
        List<LocationDTO> locationDTOS = locationPage.getContent().stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());

        // Xây dựng PagingDTO với kiểu đúng là <LocationDTO>
        PagingDTO<LocationDTO> pagingDTO = PagingDTO.<LocationDTO>builder()
                .page(locationPage.getNumber())
                .size(locationPage.getSize())
                .total(locationPage.getTotalElements())
                .items(locationDTOS) // Bây giờ sẽ không còn lỗi
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "ok", pagingDTO);
    }
}
