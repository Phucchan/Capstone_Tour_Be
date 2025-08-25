package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "tour_schedules")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"tour", "tourPax", "coordinator"})
@ToString(callSuper = true, exclude = {"tour", "tourPax", "coordinator"})
public class TourSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id", nullable = false)
    private User coordinator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_pax_id", nullable = false)
    private TourPax tourPax;

    @Column(name = "departure_date")
    private LocalDateTime departureDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "available_seats", nullable = false)
    @Min(0)
    private Integer availableSeats;
}
