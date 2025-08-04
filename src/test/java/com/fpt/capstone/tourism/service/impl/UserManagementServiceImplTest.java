package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserFullInformationResponseDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import com.fpt.capstone.tourism.repository.RoleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.user.UserRoleRepository;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Captor
    private ArgumentCaptor<User> userCaptor;
    
    @Captor
    private ArgumentCaptor<Specification<User>> specCaptor;


    private User user;
    private UserManagementRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .phone("1234567890")
                .userStatus(UserStatus.ONLINE)
                .deleted(Boolean.FALSE)
                .build();
        
        Role customerRole = Role.builder().id(1L).roleName("CUSTOMER").build();
        UserRole userRole = UserRole.builder().id(1L).user(user).role(customerRole).build();
        user.setUserRoles(new HashSet<>(List.of(userRole)));


        requestDTO = UserManagementRequestDTO.builder()
                .username("newuser")
                .fullName("New User")
                .email("new@example.com")
                .password("newPassword123!")
                .phone("0987654321")
                .roleNames(List.of("STAFF"))
                .build();
    }

    // region createUser Tests

    @Test
    void createUser_Normal_Success() {
        // Arrange
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L); // Simulate saving and getting an ID
            return savedUser;
        });
        
        Role staffRole = Role.builder().id(2L).roleName("STAFF").build();
        when(roleRepository.findByRoleName("STAFF")).thenReturn(Optional.of(staffRole));

        User savedUserWithRole = User.builder().id(2L).deleted(false).userStatus(UserStatus.ONLINE).build();
        UserRole savedUserRole = UserRole.builder().user(savedUserWithRole).role(staffRole).build();
        savedUserWithRole.setUserRoles(new HashSet<>(List.of(savedUserRole)));
        when(userRepository.findUserById(2L)).thenReturn(Optional.of(savedUserWithRole));
        

        // Act
        GeneralResponse<UserManagementDTO> response = userManagementService.createUser(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("User created successfully", response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData().getRoleNames().contains("STAFF"));

        verify(userRepository, times(1)).save(userCaptor.capture());
        assertEquals("newuser", userCaptor.getValue().getUsername());
        assertEquals("encodedNewPassword", userCaptor.getValue().getPassword());
        
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }
    
    @Test
    void createUser_Boundary_NewRoleIsCreated() {
         // Arrange
        requestDTO.setRoleNames(List.of("NEW_ROLE"));
        
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L); 
            return savedUser;
        });
        
        when(roleRepository.findByRoleName("NEW_ROLE")).thenReturn(Optional.empty());
        
        Role newRole = Role.builder().id(3L).roleName("NEW_ROLE").build();
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);
        
        User savedUserWithRole = User.builder().id(2L).deleted(false).userStatus(UserStatus.ONLINE).build();
        UserRole savedUserRole = UserRole.builder().user(savedUserWithRole).role(newRole).build();
        savedUserWithRole.setUserRoles(new HashSet<>(List.of(savedUserRole)));
        when(userRepository.findUserById(2L)).thenReturn(Optional.of(savedUserWithRole));
        
        // Act
        userManagementService.createUser(requestDTO);
        
        // Assert
        verify(roleRepository, times(1)).findByRoleName("NEW_ROLE");
        verify(roleRepository, times(1)).save(any(Role.class)); 
    }

    // endregion

    // region updateUser Tests

    @Test
    void updateUser_Normal_Success() {
        // Arrange
        Long userId = 1L;
        when(userService.findById(userId)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        Role staffRole = Role.builder().id(2L).roleName("STAFF").build();
        when(roleRepository.findByRoleName("STAFF")).thenReturn(Optional.of(staffRole));
        
        // Act
        GeneralResponse<UserManagementDTO> response = userManagementService.updateUser(userId, requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("User updated successfully", response.getMessage());
        
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        
        assertEquals("newuser", updatedUser.getUsername());
        assertEquals("New User", updatedUser.getFullName());
        
        verify(userRoleRepository).deleteByUserId(userId);
        verify(userRoleRepository).save(any(UserRole.class));
    }

    @Test
    void updateUser_Boundary_PartialUpdateWithNulls() {
        // Arrange
        Long userId = 1L;
        UserManagementRequestDTO partialRequest = UserManagementRequestDTO.builder()
                .fullName("Only Update FullName")
                .username(null)
                .email(null)
                .password(null)
                .phone(null)
                .roleNames(null) 
                .build();
        
        String originalUsername = user.getUsername();

        when(userService.findById(userId)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userManagementService.updateUser(userId, partialRequest);
        
        // Assert
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals("Only Update FullName", updatedUser.getFullName());
        assertEquals(originalUsername, updatedUser.getUsername()); 
        assertFalse(updatedUser.getUserRoles().isEmpty()); 
        verify(passwordEncoder, never()).encode(any()); 
        verify(userRoleRepository, never()).deleteByUserId(anyLong()); 
    }
    
    @Test
    void updateUser_Abnormal_UserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userService.findById(userId)).thenThrow(BusinessException.of(HttpStatus.NOT_FOUND, "User not found"));
        
        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userManagementService.updateUser(userId, requestDTO));
        
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getHttpCode());
        assertEquals("User not found", ex.getMessage());
    }

    // endregion

    // region changeStatus Tests
    @Test
    void changeStatus_Normal_Success() {
        // Arrange
        Long userId = 1L;
        ChangeStatusDTO statusDTO = new ChangeStatusDTO();
        statusDTO.setNewStatus("OFFLINE");
        
        when(userService.findById(userId)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        GeneralResponse<UserManagementDTO> response = userManagementService.changeStatus(userId, statusDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Status updated successfully", response.getMessage());
        
        verify(userRepository).save(userCaptor.capture());
        assertEquals(UserStatus.OFFLINE, userCaptor.getValue().getUserStatus());
    }
    
    @Test
    void changeStatus_Abnormal_NullStatus() {
        // Arrange
        Long userId = 1L;
        ChangeStatusDTO statusDTO = new ChangeStatusDTO();
        statusDTO.setNewStatus(null);
        
        when(userService.findById(userId)).thenReturn(user);
        
        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userManagementService.changeStatus(userId, statusDTO));
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getHttpCode());
        assertEquals("User status must not be null", ex.getMessage());
    }
    
    @Test
    void changeStatus_Abnormal_InvalidStatusString() {
        // Arrange
        Long userId = 1L;
        String invalidStatus = "INVALID_STATUS";
        ChangeStatusDTO statusDTO = new ChangeStatusDTO();
        statusDTO.setNewStatus(invalidStatus);
        
        when(userService.findById(userId)).thenReturn(user);
        
        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userManagementService.changeStatus(userId, statusDTO));
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getHttpCode());
        assertTrue(ex.getMessage().contains("Invalid user status: " + invalidStatus));
    }
    
    // endregion
    
    // region deleteUser Tests
    @Test
    void deleteUser_Normal_Success() {
        // Arrange
        Long userId = 1L;
        user.setDeleted(false);
        when(userService.findById(userId)).thenReturn(user);
        
        // Act
        GeneralResponse<String> response = userManagementService.deleteUser(userId);
        
        // Assert
        assertNotNull(response);
        assertEquals("User deleted successfully", response.getData());
        
        verify(userRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().getDeleted());
    }

    @Test
    void deleteUser_Abnormal_UserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userService.findById(userId)).thenThrow(BusinessException.of(HttpStatus.NOT_FOUND, "User not found"));
        
        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userManagementService.deleteUser(userId));
        
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getHttpCode());
    }
    // endregion
}