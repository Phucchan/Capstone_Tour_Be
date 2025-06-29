package com.fpt.capstone.tourism.repository;


import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findUserById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    List<User> findAllByUserStatus(UserStatus userStatus);


    @Query("SELECT f.receiver FROM Friendship f WHERE f.sender.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findFriendsAsSender(@Param("userId") Long userId);

    @Query("SELECT f.sender FROM Friendship f WHERE f.receiver.id = :userId AND f.status = 'ACCEPTED'")
    List<User> findFriendsAsReceiver(@Param("userId") Long userId);



}
