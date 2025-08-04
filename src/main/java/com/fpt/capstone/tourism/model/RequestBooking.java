package com.fpt.capstone.tourism.model;

import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.model.enums.RoomCategory;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "request_booking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RequestBooking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_booking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_location_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Location departureLocation;

    @Column(name = "price_min")
    private Double priceMin;

    @Column(name = "price_max")
    private Double priceMax;

    @ManyToMany
    @JoinTable(
            name = "request_booking_destination_locations",
            joinColumns = @JoinColumn(name = "request_booking_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Location> destinationLocations;

    @Column(name = "location_detail", columnDefinition = "text")
    private String destinationDetail;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TourTransport transport;

    private int adults;

    private int children;

    private int infants;

    @Column(name = "hotel_rooms")
    private int hotelRooms;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_category")
    private RoomCategory roomCategory;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestBookingStatus status;
}