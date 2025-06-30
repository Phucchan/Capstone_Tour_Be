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

    /**
     * Thống kê số lượng người dùng mới đăng ký theo từng tháng của một năm.
     *
     * @param year năm cần thống kê
     * @return Danh sách Object[] gồm: year, month, userCount
     */
    @Query(value = "SELECT EXTRACT(YEAR FROM u.created_at) AS yr, " +
            "EXTRACT(MONTH FROM u.created_at) AS mon, " +
            "COUNT(*) AS user_count " +
            "FROM users u " +
            "WHERE u.is_deleted = FALSE " +
            "AND EXTRACT(YEAR FROM u.created_at) = :year " +
            "GROUP BY yr, mon " +
            "ORDER BY mon",
            nativeQuery = true)
    List<Object[]> countNewUsersByMonth(@Param("year") int year);


}
