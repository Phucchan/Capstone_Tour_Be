package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserFullInformationResponseDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.repository.RoleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.user.UserRoleRepository;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
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
    // ... các @Mock khác
    @Mock
    private UserMapper userMapper;

    // SỬA LỖI: Thêm mock cho UserService vì nó là một dependency của UserManagementServiceImpl
    @Mock
    private UserService userService;

    private UserManagementRequestDTO validRequestDTO;
    //...
    // SỬA LỖI: Thêm mock cho UserMapper vì nó được sử dụng trong service



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
    // NHÓM 1: CÁC TRƯỜNG HỢP TEST CHO createUser
    // =================================================================

    @Test
    @DisplayName("[createUser] Normal: Tạo user thành công với đầy đủ thông tin hợp lệ")
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
    @DisplayName("[createUser] Normal: Tạo user thành công khi số điện thoại là null")
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
    @DisplayName("[createUser] Abnormal but Succeeds: Tạo user thành công dù số điện thoại đã tồn tại (do service không kiểm tra)")
    void createUser_whenPhoneAlreadyExists_shouldStillSucceed() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User mockUser = User.builder().deleted(false).build();
        when(userRepository.findUserById(any())).thenReturn(Optional.of(mockUser));

        // Act
        assertDoesNotThrow(() -> userManagementService.createUser(validRequestDTO));

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"      ", "Nguyen Van A 123", "invalid-email", "short"})
    @DisplayName("[createUser] Abnormal but Succeeds: Tạo user thành công dù dữ liệu sai định dạng (do service không validate)")
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
        User mockUser = User.builder().deleted(false).build();
        when(userRepository.findUserById(any())).thenReturn(Optional.of(mockUser));

        // Act
        assertDoesNotThrow(() -> userManagementService.createUser(validRequestDTO));

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("[createUser] Abnormal: Thất bại khi username đã tồn tại")
    void createUser_whenUsernameAlreadyExists_shouldThrowBusinessException() {
        // Arrange
        when(userRepository.existsByUsername(validRequestDTO.getUsername())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> userManagementService.createUser(validRequestDTO));
        assertEquals(HttpStatus.CONFLICT.value(), exception.getHttpCode());
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("[createUser] Abnormal: Thất bại khi email đã tồn tại")
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

    @Test
    @DisplayName("[createUser] Abnormal: Thất bại với NullPointerException khi username là null")
    void createUser_whenUsernameIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setUsername(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi username là null (hành vi hiện tại).");
    }

    @Test
    @DisplayName("[createUser] Abnormal: Thất bại với NullPointerException khi email là null")
    void createUser_whenEmailIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setEmail(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi email là null (hành vi hiện tại).");
    }

    @Test
    @DisplayName("[createUser] Abnormal: Thất bại với NullPointerException khi password là null")
    void createUser_whenPasswordIsNull_shouldThrowNullPointerException() {
        // Arrange
        validRequestDTO.setPassword(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service nên ném NullPointerException khi password là null (hành vi hiện tại).");
    }

    @Test
    @DisplayName("[createUser] Abnormal: Thất bại với NullPointerException khi fullName là null")
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
    }

    @Test
    @DisplayName("[createUser] Normal: Tạo user thành công khi roleNames là null (do service có check null)")
    void createUser_whenRoleNamesIsNull_shouldSucceedWithoutRoles() {
        // Arrange
        validRequestDTO.setRoleNames(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User mockUser = User.builder().deleted(false).userRoles(new HashSet<>()).build();
        when(userRepository.findUserById(any())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertDoesNotThrow(() -> {
            userManagementService.createUser(validRequestDTO);
        }, "Service không nên ném exception khi roleNames là null.");

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }

    // =================================================================
    // NHÓM 2: CÁC TRƯỜNG HỢP TEST CHO getAllUsers
    // =================================================================

    @Test
    @DisplayName("[getAllUsers] Valid Input: Lấy danh sách thành công với các tham số mặc định")
    void getAllUsers_withDefaultParameters_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy danh sách thành công với các tham số mặc định.");
        // Arrange
        int page = 0;
        int size = 10;
        String sortField = "id";
        String sortDirection = "DESC";
        // SỬA LỖI: Tạo đối tượng Pageable để sử dụng trong mock PageImpl
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortField));

        User user1 = User.builder().id(1L).fullName("User One").build();
        User user2 = User.builder().id(2L).fullName("User Two").build();
        // SỬA LỖI: Sử dụng constructor PageImpl(content, pageable, total) để mô phỏng đúng
        Page<User> mockUserPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockUserPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(new UserFullInformationResponseDTO());

        // Act
        GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> response =
                userManagementService.getAllUsers(page, size, null, null, null, sortField, sortDirection);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getTotal());
        assertEquals(2, response.getData().getItems().size());
        assertEquals(page, response.getData().getPage());
        // Bây giờ assertion này sẽ pass
        assertEquals(size, response.getData().getSize());

        // Verify
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userMapper, times(2)).toDTO(any(User.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getAllUsers] Valid Input: Lấy danh sách thành công với đầy đủ các bộ lọc")
    void getAllUsers_withAllFilters_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy danh sách thành công với đầy đủ các bộ lọc.");
        // Arrange
        int page = 0;
        int size = 5;
        String keyword = "test";
        Boolean isDeleted = false;
        String roleName = "CUSTOMER";
        String sortField = "fullName";
        String sortDirection = "ASC";

        Page<User> mockUserPage = new PageImpl<>(List.of(User.builder().id(1L).build()));
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockUserPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(new UserFullInformationResponseDTO());

        // Act
        GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> response =
                userManagementService.getAllUsers(page, size, keyword, isDeleted, roleName, sortField, sortDirection);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotal());

        // Verify
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getAllUsers] Valid Input: Trả về trang rỗng khi không có người dùng nào")
    void getAllUsers_whenNoUsersFound_shouldReturnEmptyPage() {
        System.out.println("Test Case: Valid Input - Trả về trang rỗng khi không có người dùng nào.");
        // Arrange
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        // Act
        GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> response =
                userManagementService.getAllUsers(0, 10, null, null, null, "id", "DESC");

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getTotal());
        assertTrue(response.getData().getItems().isEmpty());

        // Verify
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userMapper, never()).toDTO(any());
        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + Constants.Message.NO_SERVICES_AVAILABLE);
    }

    @Test
    @DisplayName("[getAllUsers] Invalid Input: Thất bại khi page là số âm")
    void getAllUsers_whenPageIsNegative_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi page là số âm.");
        // Arrange
        int invalidPage = -1;

        // Act & Assert
        // PageRequest.of() sẽ ném ra lỗi này trước khi service được thực thi sâu.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagementService.getAllUsers(invalidPage, 10, null, null, null, "id", "DESC");
        });

        assertTrue(exception.getMessage().contains("Page index must not be less than zero"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Page không được là số âm.");
    }

    @Test
    @DisplayName("[getAllUsers] Invalid Input: Thất bại khi size nhỏ hơn 1")
    void getAllUsers_whenSizeIsLessThanOne_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi size nhỏ hơn 1.");
        // Arrange
        int invalidSize = 0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagementService.getAllUsers(0, invalidSize, null, null, null, "id", "DESC");
        });

        assertTrue(exception.getMessage().contains("Page size must not be less than one"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Size phải lớn hơn 0.");
    }
    @Test
    @DisplayName("[getAllUsers] Invalid Input: Thất bại khi sortDirection không hợp lệ")
    void getAllUsers_whenSortDirectionIsInvalid_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi sortDirection không hợp lệ.");
        // Arrange
        String invalidDirection = "INVALID_DIRECTION";

        // Act & Assert
        // Sort.Direction.fromString() sẽ ném ra lỗi này.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagementService.getAllUsers(0, 10, null, null, null, "id", invalidDirection);
        });

        // SỬA LỖI: Thêm dấu nháy đơn ' ' xung quanh giá trị không hợp lệ để khớp với thông báo lỗi thực tế
        assertTrue(exception.getMessage().contains("Invalid value '" + invalidDirection + "'"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Hướng sắp xếp không hợp lệ.");
    }

    @Test
    @DisplayName("[getAllUsers] Invalid Input: Thất bại khi repository ném ra lỗi")
    void getAllUsers_whenRepositoryFails_shouldPropagateException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        // Giả lập repository ném ra một lỗi runtime
        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        // Kỳ vọng một RuntimeException sẽ được ném ra từ service vì không có khối try-catch
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userManagementService.getAllUsers(0, 10, null, null, null, "id", "DESC");
        });

        assertEquals("Database connection error", exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }
    // =================================================================
    // NHÓM 3: CÁC TRƯỜNG HỢP TEST CHO getAllCustomers
    // =================================================================

    @Test
    @DisplayName("[getAllCustomers] Valid Input: Lấy danh sách khách hàng thành công")
    void getAllCustomers_withDefaultParameters_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy danh sách khách hàng thành công.");
        // Arrange
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Giả lập có 2 user là CUSTOMER được tìm thấy
        User customer1 = User.builder().id(101L).fullName("Customer One").build();
        User customer2 = User.builder().id(102L).fullName("Customer Two").build();
        Page<User> mockCustomerPage = new PageImpl<>(List.of(customer1, customer2), pageable, 2);

        // Giả lập repository trả về trang khách hàng
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockCustomerPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(new UserFullInformationResponseDTO());

        // Act
        GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> response =
                userManagementService.getAllCustomers(page, size, null, null, "id", "DESC");

        // Assert
        assertNotNull(response, "Response không được là null.");
        assertNotNull(response.getData(), "Dữ liệu trả về không được là null.");
        assertEquals(2, response.getData().getTotal(), "Tổng số lượng khách hàng phải là 2.");
        assertEquals(size, response.getData().getSize(), "Kích thước trang phải khớp.");

        // Verify
        // Quan trọng nhất là xác minh rằng phương thức cốt lõi đã được gọi
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userMapper, times(2)).toDTO(any(User.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getAllCustomers] Valid Input: Lấy danh sách khách hàng thành công với keyword")
    void getAllCustomers_withKeyword_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy danh sách khách hàng thành công với keyword.");
        // Arrange
        String keyword = "customer";
        Page<User> mockCustomerPage = new PageImpl<>(List.of(User.builder().id(101L).build()));

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockCustomerPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(new UserFullInformationResponseDTO());

        // Act
        GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> response =
                userManagementService.getAllCustomers(0, 10, keyword, false, "fullName", "ASC");

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotal());

        // Verify
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getAllCustomers] Invalid Input: Thất bại khi page là số âm")
    void getAllCustomers_whenPageIsNegative_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi page là số âm.");
        // Arrange
        int invalidPage = -1;

        // Act & Assert
        // Lỗi này được ném ra bởi Spring Data trước khi logic của service được thực thi
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagementService.getAllCustomers(invalidPage, 10, null, null, "id", "DESC");
        });

        assertTrue(exception.getMessage().contains("Page index must not be less than zero"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Page không được là số âm.");
    }

    @Test
    @DisplayName("[getAllCustomers] Invalid Input: Thất bại khi repository ném ra lỗi")
    void getAllCustomers_whenRepositoryFails_shouldPropagateException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        // Giả lập repository ném ra một lỗi runtime
        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        // Vì hàm getAllCustomers không có khối try-catch, exception sẽ được ném ra ngoài
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userManagementService.getAllCustomers(0, 10, null, null, "id", "DESC");
        });

        assertEquals("Database connection error", exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }
    // =================================================================
    // NHÓM 4: CÁC TRƯỜNG HỢP TEST CHO updateUser
    // =================================================================

    @Test
    @DisplayName("[updateUser] Valid Input: Cập nhật thành công khi tất cả các trường đều hợp lệ")
    void updateUser_whenAllFieldsAreValid_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Cập nhật thành công khi tất cả các trường đều hợp lệ.");
        // Arrange
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .username("olduser")
                .fullName("Old Name")
                .email("old@example.com")
                .phone("0123456789")
                .deleted(false) // SỬA LỖI: Khởi tạo giá trị cho trường 'deleted'
                .build();

        UserManagementRequestDTO updateRequest = UserManagementRequestDTO.builder()
                .username("newuser")
                .fullName("New Name")
                .email("new@example.com")
                .password("NewPassword123!")
                .phone("0987654321")
                .roleNames(List.of("ADMIN"))
                .build();

        // Giả lập tìm thấy user
        when(userService.findById(userId)).thenReturn(existingUser);
        // Giả lập mã hóa mật khẩu
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("encodedNewPassword");
        // Giả lập tìm thấy role
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.of(new Role(1L, "ADMIN", false)));
        // Giả lập hàm save trả về user đã cập nhật
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        GeneralResponse<UserManagementDTO> response = userManagementService.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("User updated successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("New Name", response.getData().getFullName());

        // Verify
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encodedNewPassword", savedUser.getPassword());
        assertEquals("0987654321", savedUser.getPhone());

        verify(userRoleRepository, times(1)).deleteByUserId(userId);
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[updateUser] Valid Input: Cập nhật thành công khi chỉ một vài trường được cung cấp")
    void updateUser_whenOnlySomeFieldsAreProvided_shouldUpdateOnlyThoseFields() {
        System.out.println("Test Case: Valid Input - Cập nhật thành công khi chỉ một vài trường được cung cấp.");
        // Arrange
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .username("originaluser")
                .fullName("Original Name")
                .email("original@example.com")
                .deleted(false) // SỬA LỖI: Khởi tạo giá trị cho trường 'deleted'
                .build();

        // Chỉ cập nhật fullName và phone
        UserManagementRequestDTO partialUpdateRequest = UserManagementRequestDTO.builder()
                .fullName("Updated Name")
                .phone("1122334455")
                .build();

        when(userService.findById(userId)).thenReturn(existingUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userManagementService.updateUser(userId, partialUpdateRequest);

        // Assert & Verify
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Updated Name", savedUser.getFullName(), "Họ tên phải được cập nhật.");
        assertEquals("1122334455", savedUser.getPhone(), "Số điện thoại phải được cập nhật.");
        assertEquals("originaluser", savedUser.getUsername(), "Username không được thay đổi.");
        assertEquals("original@example.com", savedUser.getEmail(), "Email không được thay đổi.");

        // Vì roleNames là null trong DTO, không có hoạt động nào với role repository
        verify(userRoleRepository, never()).deleteByUserId(anyLong());
        verify(userRoleRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[updateUser] Invalid Input: Thất bại khi không tìm thấy người dùng")
    void updateUser_whenUserNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi không tìm thấy người dùng.");
        // Arrange
        Long nonExistentId = 99L;
        // Giả lập userService.findById ném ra lỗi
        when(userService.findById(nonExistentId))
                .thenThrow(BusinessException.of(HttpStatus.NOT_FOUND, Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.updateUser(nonExistentId, new UserManagementRequestDTO());
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, exception.getMessage());

        // Verify
        verify(userRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE);
    }

    @Test
    @DisplayName("[updateUser] Invalid Input: Thất bại khi request DTO là null")
    void updateUser_whenRequestDTOIsNull_shouldThrowNullPointerException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi request DTO là null.");
        // Arrange
        Long userId = 1L;
        // Giả lập tìm thấy user để vượt qua bước đầu tiên
        when(userService.findById(userId)).thenReturn(User.builder().id(userId).build());

        // Act & Assert
        // Kỳ vọng một NullPointerException vì service sẽ cố gắng truy cập các trường của DTO null
        assertThrows(NullPointerException.class, () -> {
            userManagementService.updateUser(userId, null);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: DTO không được là null.");
    }

    @Test
    @DisplayName("[updateUser] Invalid Input: Thất bại khi lưu vào database")
    void updateUser_whenDatabaseSaveFails_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi lưu vào database.");
        // Arrange
        Long userId = 1L;
        User existingUser = User.builder().id(userId).fullName("Old Name").build();
        UserManagementRequestDTO updateRequest = UserManagementRequestDTO.builder().fullName("New Name").build();

        when(userService.findById(userId)).thenReturn(existingUser);
        // Giả lập hàm save ném ra lỗi
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        // Kỳ vọng exception từ repository sẽ được ném ra ngoài vì không có try-catch
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userManagementService.updateUser(userId, updateRequest);
        });

        assertEquals("Database connection error", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }
    @Test
    @DisplayName("[updateUser] Normal: Cập nhật thành công khi mật khẩu là null (không thay đổi mật khẩu)")
    void updateUser_whenPasswordIsNull_shouldNotChangePassword() {
        System.out.println("Test Case: Normal Input - Cập nhật thành công khi mật khẩu là null.");
        // Arrange
        Long userId = 1L;
        String oldEncodedPassword = "encodedOldPassword";
        User existingUser = User.builder()
                .id(userId)
                .fullName("Original Name")
                .password(oldEncodedPassword) // Mật khẩu cũ đã được mã hóa
                .deleted(false)
                .build();

        // Yêu cầu cập nhật không chứa mật khẩu
        UserManagementRequestDTO updateRequest = UserManagementRequestDTO.builder()
                .fullName("Updated Name")
                .build();

        when(userService.findById(userId)).thenReturn(existingUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userManagementService.updateUser(userId, updateRequest);

        // Assert & Verify
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        // Đảm bảo mật khẩu không bị thay đổi
        assertEquals(oldEncodedPassword, savedUser.getPassword(), "Mật khẩu không được thay đổi khi DTO không cung cấp mật khẩu mới.");
        // Đảm bảo các trường khác vẫn được cập nhật
        assertEquals("Updated Name", savedUser.getFullName());

        // Vì password là null, không có tương tác nào với passwordEncoder
        verify(passwordEncoder, never()).encode(anyString());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[updateUser] Normal but logically invalid: Cập nhật thành công dù mật khẩu mới không hợp lệ (do service không validate)")
    void updateUser_whenPasswordIsInvalidFormat_shouldStillSucceed() {
        System.out.println("Test Case: Normal Input - Cập nhật thành công dù mật khẩu mới không hợp lệ.");
        // Arrange
        Long userId = 1L;
        User existingUser = User.builder().id(userId).deleted(false).build();
        String invalidPassword = "123"; // Mật khẩu quá ngắn, không hợp lệ

        UserManagementRequestDTO updateRequest = UserManagementRequestDTO.builder()
                .password(invalidPassword)
                .build();

        when(userService.findById(userId)).thenReturn(existingUser);
        when(passwordEncoder.encode(invalidPassword)).thenReturn("encoded_123"); // Giả lập mã hóa mật khẩu không hợp lệ
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        // Hàm vẫn chạy thành công vì service không có logic validate định dạng mật khẩu
        assertDoesNotThrow(() -> {
            userManagementService.updateUser(userId, updateRequest);
        });

        // Assert & Verify
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        // Kiểm tra xem mật khẩu "không hợp lệ" đã được mã hóa và lưu lại
        assertEquals("encoded_123", savedUser.getPassword());

        // Xác minh rằng passwordEncoder đã được gọi
        verify(passwordEncoder, times(1)).encode(invalidPassword);
        System.out.println("Log: " + Constants.Message.SUCCESS + " (Lưu ý: Service hiện không validate định dạng mật khẩu khi cập nhật).");
    }
}