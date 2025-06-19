package com.fpt.capstone.tourism.model.partner;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.tour.TourDay;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "tourDays")
@ToString(exclude = {"tourDays"})
public class ServiceType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_type_id")
    private Long id;

    @Column
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "serviceTypes")
    private List<TourDay> tourDays = new ArrayList<>();

}
