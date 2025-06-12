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
            try{
                //Validate input data
                Validator.validateLocation(locationRequestDTO);

                //Check duplicate location
                if(locationRepository.findByName(locationRequestDTO.getName()) != null){
                    throw BusinessException.of(EXISTED_LOCATION);
                }

                //Save date to database
                Location location = locationMapper.toEntity(locationRequestDTO);
                location.setCreatedAt(LocalDateTime.now());
                location.setDeleted(false);
                locationRepository.save(location);

                LocationDTO locationDTO = locationMapper.toDTO(location);

                return new GeneralResponse<>(HttpStatus.OK.value(), CREATE_LOCATION_SUCCESS, locationDTO);
            }catch (BusinessException be){
                throw be;
            } catch (Exception ex){
                throw BusinessException.of(CREATE_LOCATION_FAIL, ex);
            }
        }

        @Override
        public GeneralResponse<LocationDTO> getLocationById(Long id) {
            try{
                Location location = locationRepository.findById(id).orElseThrow();

                LocationDTO locationDTO = locationMapper.toDTO(location);
                return new GeneralResponse<>(HttpStatus.OK.value(), GENERAL_SUCCESS_MESSAGE, locationDTO);
            }catch (BusinessException be){
                throw be;
            } catch (Exception ex){
                throw BusinessException.of(GENERAL_FAIL_MESSAGE, ex);
            }
        }

        @Override
        public GeneralResponse<PagingDTO<List<LocationDTO>>> getAllLocation(int page, int size, String keyword, Boolean isDeleted, String orderDate) {
            try {
                // Determine sorting order dynamically
                Sort sort = "asc".equalsIgnoreCase(orderDate) ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
                Pageable pageable = PageRequest.of(page, size, sort);

                Specification<Location> spec = buildSearchSpecification(keyword, isDeleted);

                Page<Location> serviceProviderPage = locationRepository.findAll(spec, pageable);
                List<LocationDTO> serviceProviderDTOS = serviceProviderPage.getContent().stream()
                        .map(locationMapper::toDTO)
                        .collect(Collectors.toList());

                return buildPagedResponse(serviceProviderPage, serviceProviderDTOS);
            } catch (Exception ex) {
                throw BusinessException.of("Tải danh sách địa điểm thất bại", ex);
            }
        }

        @Override
        public GeneralResponse<LocationDTO> deleteLocation(Long id, boolean isDeleted) {
            try{
                Location location = locationRepository.findById(id).orElseThrow();

                location.setDeleted(isDeleted);
                location.setUpdatedAt(LocalDateTime.now());
                locationRepository.save(location);

                LocationDTO locationDTO = locationMapper.toDTO(location);
                return new GeneralResponse<>(HttpStatus.OK.value(), GENERAL_SUCCESS_MESSAGE, locationDTO);
            }catch (BusinessException be){
                throw be;
            } catch (Exception ex){
                throw BusinessException.of(GENERAL_FAIL_MESSAGE, ex);
            }
        }

        @Override
        public GeneralResponse<?> getListLocation() {
            return null;
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



        private GeneralResponse<PagingDTO<List<LocationDTO>>> buildPagedResponse(Page<Location> locationPage, List<LocationDTO> locationDTOS) {
            PagingDTO<List<LocationDTO>> pagingDTO = PagingDTO.<List<LocationDTO>>builder()
                    .page(locationPage.getNumber())
                    .size(locationPage.getSize())
                    .total(locationPage.getTotalElements())
                    .items(locationDTOS)
                    .build();

            return new GeneralResponse<>(HttpStatus.OK.value(), "ok", pagingDTO);
        }
    }
