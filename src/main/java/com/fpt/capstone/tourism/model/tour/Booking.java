package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"user", "tourSchedule", "seller"})
@ToString(exclude = {"user", "tourSchedule", "seller"})
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Column(name = "booking_code", nullable = false, unique = true)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_schedule_id", nullable = false)
    private TourSchedule tourSchedule;

    @Column(name = "booking_status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_url", columnDefinition = "text")
    private String paymentUrl;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "adults")
    private Integer adults;

    @Column(name = "children")
    private Integer children;

    @Column(name = "infants")
    private Integer infants;

    @Column(name = "toddlers")
    private Integer toddlers;

    @Column(name = "singleRooms")
    private Integer singleRooms;

    @Column(name = "needHelp")
    private Boolean needHelp;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "deposit_amount")
    private double depositAmount;

    @Column(name = "extra_hotel_cost")
    private Double extraHotelCost;

    @Column(name = "selling_price")
    private Double sellingPrice;

    private String reason;

    private String note;

}
