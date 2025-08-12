package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.repository.RoleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.user.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    private UserManagementRequestDTO validRequestDTO;

    @BeforeEach
    void setUp() {
        validRequestDTO = UserManagementRequestDTO.builder()
                .username("newvaliduser")
                .fullName("Nguyen Van A")
                .email("nguyenvana@example.com")
                .password("StrongPassword123!")
                .phone("0912345678")
                .roleNames(List.of("BUSINESS"))
                .build();
    }

    // =================================================================
    // NHÓM 1: CÁC TRƯỜNG HỢP THÀNH CÔNG (BAO GỒM CẢ THÀNH CÔNG NGOÀI Ý MUỐN)
    // =================================================================

    @Test
    @DisplayName("[Normal] 1. Tạo user thành công với đầy đủ thông tin hợp lệ")
    void createUser_whenAllFieldsAreValid_shouldSucceed() {
        // Arrange
        when(userRepository.existsByUsername(validRequestDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRequestDTO.getEmail())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        Role role = Role.builder().id(1L).roleName("BUSINESS").build();
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            u.setUserRoles(new HashSet<>());
            return u;
        });
        when(userRepository.findUserById(1L)).thenAnswer(inv -> {
            // SỬA LỖI: Thêm .deleted(false)
            User finalUser = User.builder().id(1L).fullName("Nguyen Van A").deleted(false).userRoles(new HashSet<>()).build();
            finalUser.getUserRoles().add(UserRole.builder().role(role).user(finalUser).build());
            return Optional.of(finalUser);
        });

        // Act
        GeneralResponse<UserManagementDTO> response = userManagementService.createUser(validRequestDTO);

        // Assert
        assertNotNull(response, "Response không được là null.");
        assertEquals("User created successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("Nguyen Van A", response.getData().getFullName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("[Normal] 2. Tạo user thành công khi số điện thoại là null")
    void createUser_whenPhoneIsNull_shouldSucceed() {
        // Arrange
        validRequestDTO.setPhone(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        Role role = Role.builder().id(1L).roleName("BUSINESS").build();
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            u.setUserRoles(new HashSet<>());
            return u;
        });
        when(userRepository.findUserById(1L)).thenAnswer(inv -> {
            // SỬA LỖI: Thêm .deleted(false)
            User finalUser = User.builder().phone(null).deleted(false).userRoles(new HashSet<>()).build();
            finalUser.getUserRoles().add(UserRole.builder().role(role).user(finalUser).build());
            return Optional.of(finalUser);
        });

        // Act
        GeneralResponse<UserManagementDTO> response = userManagementService.createUser(validRequestDTO);

        // Assert
        assertNotNull(response);
        assertNull(response.getData().getPhone(), "Số điện thoại trong DTO trả về phải là null.");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("[Abnormal but Succeeds] 3. Tạo user thành công dù số điện thoại đã tồn tại (do service không kiểm tra)")
    void createUser_whenPhoneAlreadyExists_shouldStillSucceed() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        // SỬA LỖI: Khởi tạo User với deleted=false
        User mockUser = User.builder().deleted(false).build();
        when(userRepository.findUserById(any())).thenReturn(Optional.of(mockUser));

        // Act
        assertDoesNotThrow(() -> userManagementService.createUser(validRequestDTO));

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"      ", "Nguyen Van A 123", "invalid-email", "short"})
    @DisplayName("[Abnormal but Succeeds] 4. Tạo user thành công dù dữ liệu sai định dạng (do service không validate)")
    void createUser_whenDataIsInvalidFormat_shouldStillSucceed(String invalidData) {
        // Arrange
        if (invalidData.trim().isEmpty()) validRequestDTO.setUsername(invalidData);
        else if (invalidData.contains("123")) validRequestDTO.setFullName(invalidData);
        else if (invalidData.contains("-email")) validRequestDTO.setEmail(invalidData);
        else validRequestDTO.setPassword(invalidData);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        // SỬA LỖI: Khởi tạo User với deleted=false
        User mockUser = User.builder().deleted(false).build();
        when(userRepository.findUserById(any())).thenReturn(Optional.of(mockUser));

        // Act
        assertDoesNotThrow(() -> userManagementService.createUser(validRequestDTO));

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    // =================================================================
    // NHÓM 2: CÁC TRƯỜNG HỢP LỖI ĐƯỢC SERVICE XỬ LÝ ĐÚNG
    // =================================================================

    @Test
    @DisplayName("[Abnormal] 5. Thất bại khi username đã tồn tại")
    void createUser_whenUsernameAlreadyExists_shouldThrowBusinessException() {
        // Arrange
        when(userRepository.existsByUsername(validRequestDTO.getUsername())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> userManagementService.createUser(validRequestDTO));
        assertEquals(HttpStatus.CONFLICT.value(), exception.getHttpCode());
        assertEquals(Constants.UserExceptionInformation.USERNAME_ALREADY_EXISTS_MESSAGE, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("[Abnormal] 6. Thất bại khi email đã tồn tại")
    void createUser_whenEmailAlreadyExists_shouldThrowBusinessException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(validRequestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> userManagementService.createUser(validRequestDTO));
        assertEquals(HttpStatus.CONFLICT.value(), exception.getHttpCode());
        assertEquals(Constants.UserExceptionInformation.EMAIL_ALREADY_EXISTS_MESSAGE, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // =================================================================
    // NHÓM 3: CÁC TRƯỜNG HỢP LỖI DO INPUT NULL (BỊ SERVICE XỬ LÝ SAI)
    // =================================================================

    @Test
    @DisplayName("[Abnormal] 7. Thất bại với NullPointerException khi username là null")
    void createUser_whenUsernameIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setUsername(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi username là null (hành vi hiện tại).");

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("[Abnormal] 8. Thất bại với NullPointerException khi email là null")
    void createUser_whenEmailIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setEmail(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi email là null (hành vi hiện tại).");

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("[Abnormal] 9. Thất bại với NullPointerException khi password là null")
    void createUser_whenPasswordIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setPassword(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi password là null (hành vi hiện tại).");

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("[Abnormal] 10. Thất bại với NullPointerException khi fullName là null")
    void createUser_whenFullNameIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setFullName(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi fullName là null (hành vi hiện tại).");

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("[Abnormal] 11. Thất bại với NullPointerException khi roleNames là null")
    void createUser_whenRoleNamesIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setRoleNames(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi roleNames là null (hành vi hiện tại).");

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }
}