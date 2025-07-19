package com.fpt.capstone.tourism.service.impl.plan;

import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.service.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {


    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    public List<PublicLocationDTO> getLocations() {
        try {
            List<Location> locations = locationRepository.findAllLocations();
            return locations.stream().map(locationMapper::toPublicLocationDTO).toList();
        } catch (Exception ex) {
            throw BusinessException.of("Không thể lấy địa điểm cho kế hoạch", ex);
        }
    }
}
