package com.fpt.capstone.tourism.model.payment;

import com.fpt.capstone.tourism.model.partner.PartnerService;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_bill_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class PaymentBillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PaymentBill paymentBill;

    @Column(name = "content")
    private String content; // Ví dụ: "Trả lại vé máy bay"

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private int unitPrice;

    @Column(name = "discount")
    private double discount;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount; // Thành tiền = (quantity * unitPrice - discount)

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_bill_item_status")
    private PaymentBillItemStatus paymentBillItemStatus;
}
