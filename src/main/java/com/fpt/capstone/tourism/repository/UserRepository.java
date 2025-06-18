package com.fpt.capstone.tourism.repository;


import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    List<User> findAllByUserStatus(UserStatus userStatus);


    @Query("SELECT CASE WHEN f.sender.id = :userId THEN f.receiver " +
            "             ELSE f.sender END " +
            "FROM Friendship f " +
            "WHERE (f.sender.id = :userId OR f.receiver.id = :userId) " +
            "AND f.status = 'ACCEPTED' " +
            "AND (f.sender.id != :userId AND f.receiver.userStatus = 'ONLINE' " +
            "     OR f.receiver.id != :userId AND f.sender.userStatus = 'ONLINE')")
    List<User> findOnlineFriends(@Param("userId") Long userId);


}
