package com.fpt.capstone.tourism.repository;


import com.fpt.capstone.tourism.model.User;
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
    User findUserByEmailContainsIgnoreCase(String email);
    User findUserByEmailAndPassword(String username,String password);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Page<User> findAll(Specification<User> spec, Pageable pageable);


    @Query("SELECT u FROM User u JOIN u.userRoles ur " +
            "WHERE ur.role.roleName = :roleName " +
            "AND (u.deleted IS NULL OR u.deleted = false) " +
            "AND (ur.deleted IS NULL OR ur.deleted = false) " +
            "AND (ur.role.deleted IS NULL OR ur.role.deleted = false) " +
            "AND (:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')))")
    List<User> findUsersByRoleNameAndFullNameLike(
            @Param("roleName") String userRole,
            @Param("fullName") String fullName
    );

    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.role.id = 10")
    Page<User> findAllTourGuides(Specification<User> spec,Pageable pageable);



    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.userRoles ur " +
            "WHERE ur.role.id = :roleId " +
            "AND ur.deleted = false")
    List<User> findUsersByRoleAndActive(
            @Param("roleId") Long roleId,
            @Param("active") boolean active);


    @Query(value = """
SELECT u.id FROM User u
WHERE u.username = :name
""")
    Long findIdByUsername(String name);
}
