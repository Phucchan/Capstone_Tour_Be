package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.common.TokenDTO;
import com.fpt.capstone.tourism.dto.common.UserDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.RegisterRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserInfoResponseDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.JwtHelper;
import com.fpt.capstone.tourism.helper.PasswordGenerateImpl;
import com.fpt.capstone.tourism.helper.validator.Validator;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.Token;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.repository.RoleRepository;
import com.fpt.capstone.tourism.repository.user.UserRoleRepository;
import com.fpt.capstone.tourism.service.AuthService;
import com.fpt.capstone.tourism.service.EmailConfirmationService;
import com.fpt.capstone.tourism.service.EmailService;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock private UserService userService;
    @Mock private JwtHelper jwtHelper;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailConfirmationService emailConfirmationService;
    @Mock private RoleRepository roleRepository;
    @Mock private EmailService emailService;
    @Mock private PasswordGenerateImpl passwordGenerate;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void login_success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("Motconvit!");

        User user = User.builder().username("testuser").deleted(false).emailConfirmed(true).build();

        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(jwtHelper.generateToken(user)).thenReturn("token");
        when(userMapper.toUserBasicDTO(user)).thenReturn(null);

        GeneralResponse<TokenDTO> response = authService.login(userDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Đăng nhập thành công", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("token", response.getData().getToken());
    }

    @Test
    @Order(2)
    void testLogin_Passwordnull() {
        // Arrange
        UserDTO userDTO = new UserDTO("testuser", "");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Mật khẩu không được để trống", exception.getMessage());
    }
    @Test
    @Order(3)
    void testLogin_InvalidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("testuser", "Phuc2002");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Mật khẩu phải từ 8 ký tự trở lên, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 ký tự đặc biệt", exception.getMessage());
    }
    @Test
    @Order(4)
    void testLogin_InvalidUsernameAndValidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("test", "Motconvit!");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }
    @Test
    @Order(5)
    void testLogin_InvalidUsernameAndInvalidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("test", "Phuc2002");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }
    @Test
    @Order(6)
    void testLogin_InvalidUsernameAndPasswordNull() {
        // Arrange
        UserDTO userDTO = new UserDTO("test", "Phuc2002");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }
    @Test
    @Order(7)
    void testLogin_UsernameNullAndPasswordNull() {
        // Arrange
        UserDTO userDTO = new UserDTO("", "");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập không được để trống", exception.getMessage());
    }
    @Test
    @Order(8)
    void testLogin_UsernameNullAndValidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("", "Motconvit!");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập không được để trống", exception.getMessage());
    }
    @Test
    @Order(9)
    void testLogin_UsernameNullAndInvalidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("", "Motconvit");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập không được để trống", exception.getMessage());
    }
    @Test
    @Order(10)
    void testLogin_InvalidLongUsernameAndValidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("abcdefghijklmnopqrstuvwxyz0123456789", "Motconvit!");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }
    @Test
    @Order(11)
    void testLogin_InvalidLongUsernameAndInValidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("abcdefghijklmnopqrstuvwxyz0123456789", "Phuc2002");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }
    @Test
    @Order(12)
    void testLogin_InvalidLongUsernameAndPasswordNull() {
        // Arrange
        UserDTO userDTO = new UserDTO("abcdefghijklmnopqrstuvwxyz0123456789", "");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }

    @Test
    @Order(13)
    void testLogin_ValidLongUsernameAndPasswordNull() {
        // Arrange
        UserDTO userDTO = new UserDTO("abcdefghijklmnopqrstuvwxyz0123", "");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Mật khẩu không được để trống", exception.getMessage());
    }
    @Test
    @Order(14)
    void testLogin_ValidLongUsernameAndValidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("abcdefghijklmnopqrstuvwxyz0123", "Motconvit!");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Đăng nhập thất bại! Tên đăng nhập hoặc mật khẩu không đúng", exception.getMessage());
    }
    @Test
    @Order(15)
    void testLogin_ValidLongUsernameAndInvalidPassword() {
        // Arrange
        UserDTO userDTO = new UserDTO("abcdefghijklmnopqrstuvwxyz0123", "Phuc2002");

        // Act & Assert
        Exception exception = assertThrows(BusinessException.class, () -> authService.login(userDTO));
        assertEquals("Mật khẩu phải từ 8 ký tự trở lên, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 ký tự đặc biệt", exception.getMessage());
    }




  
}