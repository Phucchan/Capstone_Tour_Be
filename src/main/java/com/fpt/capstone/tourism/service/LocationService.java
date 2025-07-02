package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.request.LocationRequestDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;

import java.util.List;

public interface LocationService {
    GeneralResponse<LocationDTO> saveLocation(LocationRequestDTO locationRequestDTO);
    GeneralResponse<LocationDTO> getLocationById(Long id);
    GeneralResponse<LocationDTO> deleteLocation(Long id, boolean isDeleted);
    GeneralResponse<PagingDTO<LocationDTO>> getListLocation(int page, int size, String keyword);

    List<LocationDTO> getAllDepartures();
    List<LocationDTO> getAllDestinations();
}
