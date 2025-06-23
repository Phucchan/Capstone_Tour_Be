package com.fpt.capstone.tourism.model.voucher;

import com.fpt.capstone.tourism.model.enums.VoucherStatus;
import jakarta.persistence.*;
import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "createdBy")
public class Voucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Lob
    private String description;

    @Column(name = "discount_amount", nullable = false)
    private double discountAmount;

    @Column(name = "points_required", nullable = false)
    private Integer pointsRequired;

    @Column(name = "min_order_value")
    private double minOrderValue;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "voucher_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherStatus voucherStatus;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
