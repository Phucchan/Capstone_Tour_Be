package com.fpt.capstone.tourism.repository.user;


import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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


    // SỬA LỖI: Thêm CAST(... AS timestamp)
    @Query(value = "SELECT EXTRACT(YEAR FROM u.created_at) AS yr, " +
            "EXTRACT(MONTH FROM u.created_at) AS mon, " +
            "COUNT(*) AS user_count " +
            "FROM users u " +
            "WHERE u.is_deleted = FALSE " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR u.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR u.created_at <= :endDate) " +
            "GROUP BY yr, mon " +
            "ORDER BY mon",
            nativeQuery = true)
    List<Object[]> countNewUsersByMonth(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    // SỬA LỖI: Thêm CAST(... AS timestamp)
    @Query(value = "SELECT COUNT(*) FROM users u " +
            "WHERE u.is_deleted = FALSE " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR u.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR u.created_at <= :endDate)",
            nativeQuery = true)
    Long countNewUsers(@Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r " +
            "WHERE r.roleName = :roleName AND u.deleted = FALSE")
    List<User> findByRoleName(@Param("roleName") String roleName);
}
