package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.Region;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import com.fpt.capstone.tourism.model.enums.TourType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tours")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"departLocation", "createdBy", "tourTheme"})
@ToString(exclude = {"departLocation", "createdBy", "tourTheme"})
public class Tour extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // Mã tour, có thể là duy nhất

    @Column(nullable = false)
    private String name;

    @Column(name = "tour_transport")
    @Enumerated(EnumType.STRING)
    private TourTransport tourTransport;

    // Thêm trường ảnh đại diện cho tour
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "tour_type", length = 50)
    private TourType tourType; // e.g., FIXED, CUSTOM

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_theme_id")
    private TourTheme tourTheme; // Consider using Enum if validation needed

    @Lob
    private String description;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "tour_status", length = 50)
    private TourStatus tourStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location departLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}

