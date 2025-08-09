package com.fpt.capstone.tourism.model.partner;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.enums.CostType;
import com.fpt.capstone.tourism.model.tour.TourDay;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"serviceType", "partner", "tourDays"})
@ToString(exclude = {"serviceType", "partner", "tourDays"})
public class PartnerService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long id;

    @Column(name = "name", nullable = false) // <-- THÊM TRƯỜNG NÀY
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "nett_price")
    private double nettPrice;

    @Column(name = "selling_price")
    private double sellingPrice;
    @Enumerated(EnumType.STRING)

    @Column(name = "cost_type")
    private CostType costType;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    private List<TourDay> tourDays = new ArrayList<>();


}
