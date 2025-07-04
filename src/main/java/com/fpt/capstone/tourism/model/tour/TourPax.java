package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.enums.PaxType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_pax")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = "tour")
@ToString(callSuper = true, exclude = "tour")
public class TourPax extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_pax_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "min_quantity")
    private int minQuantity;

    @Column(name = "max_quantity")
    private int maxQuantity;

    @Column(name = "selling_price")
    private double sellingPrice;

    @Column(name = "fixed_price")
    private double fixedPrice;

    @Column(name = "extra_hotel_cost")
    private double extraHotelCost;

}
