package com.fpt.capstone.tourism.service.impl;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.repository.RoleRepository;
import com.fpt.capstone.tourism.repository.UserRepository;
import com.fpt.capstone.tourism.repository.UserRoleRepository;
import com.fpt.capstone.tourism.service.UserManagementService;
import com.fpt.capstone.tourism.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

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

    @Override
    public GeneralResponse<PagingDTO<UserManagementDTO>> getListUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<User> users = userRepository.findAll(pageable);
        List<UserManagementDTO> userManagementDTOs = users.getContent().stream()
                .map(user -> new UserManagementDTO(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getGender(),
                        user.getPhone(),
                        user.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getRoleName())
                                .toList(),
                        user.getDeleted()
                )).toList();

        PagingDTO<UserManagementDTO> pagingDTO = PagingDTO.<UserManagementDTO>builder()
                .page(users.getNumber())
                .size(users.getSize())
                .total(users.getTotalElements())
                .items(userManagementDTOs)
                .build();

        return GeneralResponse.of(pagingDTO);

    }

    @Override
    @Transactional
    public GeneralResponse<UserManagementDTO> createUser(UserManagementRequestDTO requestDTO) {
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


}
