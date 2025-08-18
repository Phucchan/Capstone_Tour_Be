package com.fpt.capstone.tourism.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Policy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_policy", columnDefinition = "TEXT")
    private String tourPolicy;

    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicy;

    @Column(name = "booking_policy", columnDefinition = "TEXT")
    private String bookingPolicy;

    @Column(name = "tour_price", columnDefinition = "TEXT")
    private String tourPrice;
}