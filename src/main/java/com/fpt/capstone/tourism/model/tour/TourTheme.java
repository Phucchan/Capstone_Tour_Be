package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tour_themes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TourTheme  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_theme_id")
    private Long id;
    private String name; // Name of the tour theme, e.g., "Adventure", "Cultural", etc.
    private String description; // Description of the tour theme

    @ManyToMany(mappedBy = "themes")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Tour> tours;

}
