package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "theme_id") // Đã sửa từ tour_id thành theme_id cho đúng ngữ nghĩa
    private Long id;
    private String name; // Name of the tour theme, e.g., "Adventure", "Cultural", etc.
    private String description; // Description of the tour theme
    //thêm trường mới để lưu ảnh đại diện cho chủ đề
    @Column(name = "image_url")
    private String imageUrl;
}
