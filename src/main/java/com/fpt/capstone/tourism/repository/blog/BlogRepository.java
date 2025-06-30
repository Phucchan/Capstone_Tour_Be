package com.fpt.capstone.tourism.repository.blog;

import com.fpt.capstone.tourism.model.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    // Lấy 5 bài blog mới nhất không bị xóa
    List<Blog> findFirst5ByDeletedFalseOrderByCreatedAtDesc();
    List<Blog> findByDeletedFalseOrderByCreatedAtDesc();
    Page<Blog> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * PHƯƠNG THỨC MỚI
     * Tìm tất cả các bài blog chưa bị xóa và trả về dưới dạng phân trang.
     * Spring Data JPA sẽ tự động hiểu và tạo câu lệnh query phù hợp.
     * @param pageable đối tượng chứa thông tin về trang hiện tại, kích thước trang và sắp xếp.
     * @return một Page chứa danh sách blog và thông tin phân trang.
     */
    Page<Blog> findByDeletedFalse(Pageable pageable);
}