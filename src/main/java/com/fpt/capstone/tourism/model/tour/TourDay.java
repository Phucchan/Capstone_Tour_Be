package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tour_day")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"tour", "location", "services", "serviceTypes"})
@ToString(exclude = {"tour", "location", "services", "serviceTypes"})
public class TourDay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tour_day_services",
            joinColumns = @JoinColumn(name = "tour_day_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<PartnerService> services = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "tour_day_service_types",
            joinColumns = @JoinColumn(name = "tour_day_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id")
    )
    private List<ServiceType> serviceTypes = new ArrayList<>();


}
