package com.fpt.capstone.tourism.repository.partner;

import com.fpt.capstone.tourism.model.partner.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
}