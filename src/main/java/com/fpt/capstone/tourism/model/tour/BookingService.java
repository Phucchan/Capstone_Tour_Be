package com.fpt.capstone.tourism.model.tour;


import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.enums.BookingServiceStatus;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "booking_customer")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BookingService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_service_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PartnerService service;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    private int quantity;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingServiceStatus status;

}
