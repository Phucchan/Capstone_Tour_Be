package com.fpt.capstone.tourism.model.partner;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.enums.TicketClass;
import com.fpt.capstone.tourism.model.enums.VehicleType;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "transport_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"service"})
@ToString(callSuper = true, exclude = {"service"})
public class TransportServiceDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transport_service_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private PartnerService service;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_class", nullable = false)
    private TicketClass ticketClass;

    @Column(name = "seat_count")
    private Integer seatCount;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;



    // Getters, Setters, Constructors, etc.
}
