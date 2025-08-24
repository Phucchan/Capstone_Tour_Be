package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.partner.PartnerShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeDeleteStatusDTO;
import com.fpt.capstone.tourism.dto.request.PartnerUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.PartnerDetailDTO;
import com.fpt.capstone.tourism.dto.response.PartnerSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PartnerManagementService {
    GeneralResponse<PagingDTO<PartnerSummaryDTO>> getPartners(int page,
                                                              int size,
                                                              String keyword,
                                                              Boolean isDeleted,
                                                              String sortField,
                                                              String sortDirection);
    GeneralResponse<PartnerDetailDTO> getPartnerDetail(Long id);

    GeneralResponse<PartnerDetailDTO> updatePartner(Long id, PartnerUpdateRequestDTO requestDTO);

    GeneralResponse<PartnerDetailDTO> addPartner(PartnerUpdateRequestDTO requestDTO);

    GeneralResponse<PartnerSummaryDTO> changePartnerStatus(Long id, ChangeDeleteStatusDTO changeStatusDTO);

    GeneralResponse<List<PartnerShortDTO>> getPartners(String planId, String categoryName, List<Integer> locationIds);
}