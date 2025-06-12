package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    public void deleteByUserId(Long userId);
}
