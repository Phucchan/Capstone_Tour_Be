package com.fpt.capstone.tourism.model.payment;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.tour.Booking;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "refunds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Booking booking;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_account_holder", length = 100)
    private String bankAccountHolder;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount;
}