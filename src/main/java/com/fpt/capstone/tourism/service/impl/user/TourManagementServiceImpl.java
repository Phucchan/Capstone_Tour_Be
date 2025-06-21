package com.fpt.capstone.tourism.service.impl.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.response.TourResponseDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourManagementMapper;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourManagementServiceImpl implements com.fpt.capstone.tourism.service.TourManagementService {


    @Autowired
    private TourManagementRepository tourRepository;

    @Autowired
    private TourManagementMapper tourMapper;


    @Override
    public GeneralResponse<List<TourResponseDTO>> getListTours() {
        List<Tour> tours = tourRepository.findAll();
        List<TourResponseDTO> tourResponseDTOs = tours.stream()
                .map(tourMapper::toTourResponseDTO)
                .collect(Collectors.toList());
        return GeneralResponse.of(tourResponseDTOs);
    }




    @Override
    public GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        try {
            TourStatus newStatus = TourStatus.valueOf(changeStatusDTO.getNewStatus());
            tour.setTourStatus(newStatus);
            tourRepository.save(tour);
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Invalid tour status: " + changeStatusDTO.getNewStatus(), e);
        }

        return GeneralResponse.of((Object) null, "Status updated successfully");

    }




}
