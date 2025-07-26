package com.fpt.capstone.tourism.repository.partner;

import com.fpt.capstone.tourism.dto.common.partner.PartnerShortDTO;
import com.fpt.capstone.tourism.model.partner.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer>, JpaSpecificationExecutor<Partner> {

    @Query("""
    SELECT new com.fpt.capstone.tourism.dto.common.partner.PartnerShortDTO(
        p.id,
        p.name,
        p.description,
        p.logoUrl,
        p.websiteUrl,
        p.contactEmail,
        p.contactPhone,
        st.name,
        p.location.id
    )
    FROM Partner p
    JOIN p.serviceType st
    JOIN p.location l
    WHERE p.location.id IN :locationIds AND st.id IN (1, 3)
""")
    List<PartnerShortDTO> findAllShortByLocationIds(List<Integer> locationIds);

}
