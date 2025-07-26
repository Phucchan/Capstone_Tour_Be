package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.PartnerDetailDTO;
import com.fpt.capstone.tourism.dto.response.PartnerSummaryDTO;
import org.springframework.stereotype.Service;

@Service
public interface PartnerManagementService {
    GeneralResponse<PagingDTO<PartnerSummaryDTO>> getPartners(int page,
                                                              int size,
                                                              String keyword,
                                                              Boolean isDeleted,
                                                              String sortField,
                                                              String sortDirection);
    GeneralResponse<PartnerDetailDTO> getPartnerDetail(Long id);
}