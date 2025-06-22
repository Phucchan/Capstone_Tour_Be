package com.fpt.capstone.tourism.model.partner;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.enums.BedType;
import com.fpt.capstone.tourism.model.enums.RoomCategory;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"service"})
@ToString(callSuper = true, exclude = {"service"})
public class HotelRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private PartnerService service;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_category", nullable = false)
    private RoomCategory roomCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", nullable = false)
    private BedType bedType;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities;



    // Constructors, Getters, Setters, etc.
}