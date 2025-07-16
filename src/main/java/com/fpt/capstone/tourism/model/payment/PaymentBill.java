package com.fpt.capstone.tourism.model.payment;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "payment_bills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class PaymentBill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_number", nullable = false, unique = true)
    private String billNumber; // Số chứng từ, ví dụ: "PB-2023-001"
    // Ví dụ: TOUR, BOOKING, KHÁC
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_for_type", nullable = false)
    private PaymentForType paymentForType;

    @Column(name = "booking_code")
    private String bookingCode;

    @Column(name = "paid_by", nullable = false)
    private String paidBy; // Người trả tiền

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User creator;

    @Column(name = "receiver_address", columnDefinition = "text")
    private String receiverAddress;

    @Column(name = "pay_to", nullable = false)
    private String payTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @OneToMany(mappedBy = "paymentBill", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PaymentBillItem> items;
}
