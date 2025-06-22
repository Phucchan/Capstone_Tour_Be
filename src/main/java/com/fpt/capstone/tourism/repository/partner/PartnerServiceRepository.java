package com.fpt.capstone.tourism.repository.partner;

import com.fpt.capstone.tourism.model.partner.PartnerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerServiceRepository extends JpaRepository<PartnerService, Long> {
}