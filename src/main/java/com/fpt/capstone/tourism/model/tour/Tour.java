package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.Region;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import com.fpt.capstone.tourism.model.enums.TourType;
import jakarta.persistence.*;
import com.fpt.capstone.tourism.model.tour.TourDiscount;

import lombok.*;

import java.util.List;

@Entity
@Table(name = "tours")
@org.hibernate.annotations.Check(constraints = "((tour_type = 'CUSTOM' AND request_id IS NOT NULL) OR (tour_type = 'FIXED' AND request_id IS NULL))")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"departLocation", "createdBy", "themes", "requestBooking"})
@ToString(exclude = {"departLocation", "createdBy", "themes", "requestBooking"})
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

    @ManyToMany
    @JoinTable(
            name = "tour_theme_mapping",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "tour_theme_id")
    )
    private List<TourTheme> themes;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "tour_status", length = 50)
    private TourStatus tourStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", unique = true)
    private RequestBooking requestBooking;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location departLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourDay> tourDays;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourSchedule> schedules;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourPax> tourPaxes;

    
}

