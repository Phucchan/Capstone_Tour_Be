package com.fpt.capstone.tourism.service.impl;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.response.UserFullInformationResponseDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import com.fpt.capstone.tourism.repository.RoleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.user.UserRoleRepository;
import com.fpt.capstone.tourism.service.UserManagementService;
import com.fpt.capstone.tourism.service.UserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    public GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> getAllUsers(int page,
                                                                                  int size,
                                                                                  String keyword,
                                                                                  Boolean isDeleted,
                                                                                  String roleName,
                                                                                  String sortField,
                                                                                  String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<User> specification = buildSpecification(keyword, isDeleted, roleName, null);
        Page<User> users = userRepository.findAll(specification, pageable);
        List<UserFullInformationResponseDTO> dtos = users.getContent().stream()
                .map(userMapper::toDTO)
                .toList();

        PagingDTO<UserFullInformationResponseDTO> pagingDTO = PagingDTO.<UserFullInformationResponseDTO>builder()
                .page(users.getNumber())
                .size(users.getSize())
                .total(users.getTotalElements())
                .items(dtos)
                .build();

        return GeneralResponse.of(pagingDTO);

    }
    @Override
    public GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> getAllCustomers(int page,
                                                                                      int size,
                                                                                      String keyword,
                                                                                      Boolean isDeleted,
                                                                                      String sortField,
                                                                                      String sortDirection) {
        return getAllUsers(page, size, keyword, isDeleted, "CUSTOMER", sortField, sortDirection);
    }
    @Transactional
    public GeneralResponse<UserManagementDTO> changeStatus(Long id, ChangeStatusDTO changeStatusDTO) {
        User user = userService.findById(id);
        String statusValue = changeStatusDTO.getNewStatus();
        if (statusValue == null) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "User status must not be null");
        }
        try {
            UserStatus newStatus = UserStatus.valueOf(statusValue.toUpperCase());
            user.setUserStatus(newStatus);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST,
                    "Invalid user status: " + statusValue,
                    e);
        }
        return GeneralResponse.of(toDTO(user), "Status updated successfully");
    }


    @Override
    public GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> getAllStaff(int page,
                                                                                  int size,
                                                                                  String keyword,
                                                                                  Boolean isDeleted,
                                                                                  String sortField,
                                                                                  String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<User> specification = buildSpecification(keyword, isDeleted, null, "CUSTOMER");
        Page<User> users = userRepository.findAll(specification, pageable);
        List<UserFullInformationResponseDTO> dtos = users.getContent().stream()
                .map(userMapper::toDTO)
                .toList();

        PagingDTO<UserFullInformationResponseDTO> pagingDTO = PagingDTO.<UserFullInformationResponseDTO>builder()
                .page(users.getNumber())
                .size(users.getSize())
                .total(users.getTotalElements())
                .items(dtos)
                .build();

        return GeneralResponse.of(pagingDTO);
    }

    @Override
    @Transactional
    public GeneralResponse<UserManagementDTO> createUser(UserManagementRequestDTO requestDTO) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw BusinessException.of(HttpStatus.CONFLICT, "Username already exists");
        }

        // 2. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw BusinessException.of(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = User.builder()
                .username(requestDTO.getUsername())
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .phone(requestDTO.getPhone())
                .deleted(false)
                .emailConfirmed(true)
                .build();

        User savedUser = userRepository.save(user);

        if (requestDTO.getRoleNames() != null) {
            for (String roleName : requestDTO.getRoleNames()) {
                Role role = roleRepository.findByRoleName(roleName)
                        .orElseGet(() -> roleRepository.save(Role.builder()
                                .roleName(roleName)
                                .deleted(false)
                                .build()));
                UserRole userRole = userRoleRepository.save(UserRole.builder()
                        .user(savedUser)
                        .role(role)
                        .deleted(false)
                        .build());
                savedUser.getUserRoles().add(userRole);
            }
        }
        savedUser = userRepository.findUserById(savedUser.getId()).orElse(savedUser);
        UserManagementDTO dto = toDTO(savedUser);
        return GeneralResponse.of(dto, "User created successfully");
    }

    @Override
    @Transactional
    public GeneralResponse<UserManagementDTO> updateUser(Long id, UserManagementRequestDTO requestDTO) {
        User user = userService.findById(id);

        if (requestDTO.getUsername() != null) user.setUsername(requestDTO.getUsername());
        if (requestDTO.getFullName() != null) user.setFullName(requestDTO.getFullName());
        if (requestDTO.getEmail() != null) user.setEmail(requestDTO.getEmail());
        if (requestDTO.getPassword() != null) user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        if (requestDTO.getPhone() != null) user.setPhone(requestDTO.getPhone());

        User updatedUser = userRepository.save(user);

        if (requestDTO.getRoleNames() != null) {
            userRoleRepository.deleteByUserId(id);
            for (String roleName : requestDTO.getRoleNames()) {
                Role role = roleRepository.findByRoleName(roleName)
                        .orElseGet(() -> roleRepository.save(Role.builder()
                                .roleName(roleName)
                                .deleted(false)
                                .build()));
                userRoleRepository.save(UserRole.builder()
                        .user(updatedUser)
                        .role(role)
                        .deleted(false)
                        .build());
            }
        }

        UserManagementDTO dto = toDTO(updatedUser);
        return GeneralResponse.of(dto, "User updated successfully");
    }

    @Override
    @Transactional
    public GeneralResponse<String> deleteUser(Long id) {
        User user = userService.findById(id);
        user.softDelete();
        userRepository.save(user);
        return GeneralResponse.of("User deleted successfully");
    }

    private UserManagementDTO toDTO(User user) {
        List<String> roleNames = user.getUserRoles() == null
                ? List.of()
                : user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList();
        return new UserManagementDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getGender(),
                user.getPhone(),
                roleNames,
                user.getDeleted()
        );

    }
    private Specification<User> buildSpecification(String keyword,
                                                   Boolean isDeleted,
                                                   String roleName,
                                                   String excludeRole) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate fullName = cb.like(cb.lower(root.get("fullName")), likeKeyword);
                Predicate email = cb.like(cb.lower(root.get("email")), likeKeyword);
                Predicate username = cb.like(cb.lower(root.get("username")), likeKeyword);
                predicates.add(cb.or(fullName, email, username));
            }

            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("deleted"), isDeleted));
            }

            if ((roleName != null && !roleName.isBlank()) || excludeRole != null) {
                Join<User, UserRole> urJoin = root.join("userRoles", JoinType.LEFT);
                Join<UserRole, Role> roleJoin = urJoin.join("role", JoinType.LEFT);
                if (roleName != null && !roleName.isBlank()) {
                    predicates.add(cb.equal(roleJoin.get("roleName"), roleName));
                }
                if (excludeRole != null) {
                    predicates.add(cb.notEqual(roleJoin.get("roleName"), excludeRole));
                }
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Kiểm tra xem một giá trị (username hoặc email) đã được sử dụng hay chưa.
     * @param type loại cần kiểm tra ('username' hoặc 'email')
     * @param value giá trị cần kiểm tra
     * @return Map chứa key "isTaken" với giá trị true/false
     */
    @Override
    public Map<String, Boolean> checkUniqueness(String type, String value) {
        boolean isTaken;
        if ("username".equalsIgnoreCase(type)) {
            isTaken = userRepository.existsByUsername(value);
        } else if ("email".equalsIgnoreCase(type)) {
            isTaken = userRepository.existsByEmail(value);
        } else {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Invalid uniqueness check type");
        }
        return Map.of("isTaken", isTaken);
    }
}
