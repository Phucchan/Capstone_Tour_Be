package com.fpt.capstone.tourism.repository.blog;

import com.fpt.capstone.tourism.model.blog.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}