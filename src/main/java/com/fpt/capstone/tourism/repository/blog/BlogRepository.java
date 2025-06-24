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
}