package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.PartnerServiceShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PartnerServiceService {
    GeneralResponse<List<PartnerServiceShortDTO>> getPartnerServices();
}
