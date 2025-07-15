package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
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

    @Mock
    private UserService userService;
    @Mock
    private JwtHelper jwtHelper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailConfirmationService emailConfirmationService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordGenerateImpl passwordGenerate;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private UserMapper userMapper;

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


    @Test
    void login_emailNotConfirmed_throwsException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = User.builder().username("testuser").deleted(false).emailConfirmed(false).build();

        when(userService.findUserByUsername("testuser")).thenReturn(user);

        assertThrows(BusinessException.class, () -> authService.login(userDTO));
    }

    @Test
    void register_success() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.exitsByEmail(anyString())).thenReturn(false);
        when(userService.existsByPhoneNumber(anyString())).thenReturn(false);
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(Role.builder().roleName("CUSTOMER").build()));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(null);
        when(emailConfirmationService.createEmailConfirmationToken(any(User.class))).thenReturn(Token.builder().build());

        GeneralResponse<UserInfoResponseDTO> response = authService.register(req);

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertNotNull(response.getData());
        assertEquals("testuser2", response.getData().getUsername());
        assertEquals("Cảm ơn bạn đã đăng ký. Vui lòng kiểm tra email để hoàn tất xác minh", response.getMessage());
    }

    @Test
    void register_phoneExists_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.exitsByEmail(anyString())).thenReturn(false);
        when(userService.existsByPhoneNumber(anyString())).thenReturn(true);

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Số điện thoại đã được sử dụng", exception.getMessage());
    }

    @Test
    void register_invalidPhoneFormat_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("035"); // Invalid phone number
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Số điện thoại phải gồm đúng 10-15 chữ số", exception.getMessage());
    }

    @Test
    void register_phoneWithSpecialChar_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("035@567890"); // Invalid phone number with special character
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Số điện thoại phải gồm đúng 10-15 chữ số", exception.getMessage());
    }

    @Test
    void register_phonenull_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone(""); // null phone number
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Số điện thoại không được để trống", exception.getMessage());
    }

    @Test
    void register_invalidPasswordFormat_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motcon"); // Invalid password
        req.setRePassword("motconvit");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Mật khẩu phải từ 8 ký tự trở lên, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 ký tự đặc biệt", exception.getMessage());
    }

    @Test
    void register_rePasswordMismatch_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit123!"); // Mismatched rePassword
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Mật khẩu và xác nhận mật khẩu không trùng khớp", exception.getMessage());
    }

    @Test
    void register_passwordNull_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword(""); // Null or empty password
        req.setRePassword("");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Mật khẩu không được để trống", exception.getMessage());
    }

    @Test
    void register_rePasswordNull_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword(""); // Null or empty rePassword
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Xác nhận mật khẩu không được để trống", exception.getMessage());
    }

    @Test
    void register_invalidFullName_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName(" test ! user"); // Invalid full name
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Họ tên phải bắt đầu bằng chữ cái, chỉ chứa chữ cái và khoảng trắng", exception.getMessage());
    }

    @Test
    void register_fullNameNull_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName(""); // Null or empty full name
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Họ và tên không được để trống", exception.getMessage());
    }

    @Test
    void register_emailExists_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("testuser@gmail.com");

        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.exitsByEmail("testuser@gmail.com")).thenReturn(true);

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Email đã được sử dụng", exception.getMessage());
    }

    @Test
    void register_invalidEmailFormat_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("testuser @gmail.com"); // Invalid email

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Email không hợp lệ", exception.getMessage());
    }

    @Test
    void register_emailNull_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail(""); // Null or empty email

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Email không được để trống", exception.getMessage());
    }

    @Test
    void register_usernameExists_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        when(userService.existsByUsername("testuser")).thenReturn(true);

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Tên đăng nhập đã tồn tại", exception.getMessage());
    }

    @Test
    void register_fullNameTooLong_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("This is a very long full name that exceeds fifty characters in total length!");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Họ tên phải bắt đầu bằng chữ cái, chỉ chứa chữ cái và khoảng trắng", exception.getMessage());
    }

    @Test
    void register_phoneNotStartWithZero_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("testuser2");
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("1234567890"); // Does not start with 0
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Đăng ký tài khoản thất bại do lỗi hệ thống!", exception.getMessage());
    }

    @Test
    void register_invalidUsernameTooShort_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("test"); // Too short
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }

    @Test
    void register_usernameTooLong_throwsException() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername("abcdefghijklmnopqrstuvwxyz01234567890123456789"); // >36 chars
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        Exception exception = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals("Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự", exception.getMessage());
    }

    @Test
    void register_usernameExactly30Chars_success() {
        String username = "testuser"; // 30 chars
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername(username);
        req.setPassword("Motconvit!");
        req.setRePassword("Motconvit!");
        req.setFullName("User Name");
        req.setPhone("0123456789");
        req.setAddress("Address");
        req.setEmail("user@email.com");

        when(userService.existsByUsername(username)).thenReturn(false);
        when(userService.exitsByEmail(anyString())).thenReturn(false);
        when(userService.existsByPhoneNumber(anyString())).thenReturn(false);
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(Role.builder().roleName("CUSTOMER").build()));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(null);
        when(emailConfirmationService.createEmailConfirmationToken(any(User.class))).thenReturn(Token.builder().build());

        GeneralResponse<UserInfoResponseDTO> response = authService.register(req);

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertNotNull(response.getData());
        assertEquals(username, response.getData().getUsername());
        assertEquals("Cảm ơn bạn đã đăng ký. Vui lòng kiểm tra email để hoàn tất xác minh", response.getMessage());
    }

    @Test
    void confirmEmail_success() {
        // Arrange
        String token = "testToken";
        User user = User.builder().emailConfirmed(false).build();
        Token emailToken = Token.builder().user(user).build();

        when(emailConfirmationService.validateConfirmationToken(token)).thenReturn(emailToken);
        when(userService.saveUser(user)).thenReturn(user);

        // Act
        GeneralResponse<String> response = authService.confirmEmail(token);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.EMAIL_CONFIRMED_SUCCESS_MESSAGE, response.getMessage());
        assertEquals("Đăng ký thành công! Vui lòng đăng nhập để tiếp tục", response.getMessage());
        assertNull(response.getData());
        assertTrue(user.isEmailConfirmed());
        verify(userService).saveUser(user);
    }

    @Test
    void confirmEmail_emailNotFound() {
        // Arrange
        String token = "invalidToken";
        when(emailConfirmationService.validateConfirmationToken(token))
            .thenThrow(new BusinessException(400, "Email xác nhận không tồn tại hoặc đã hết hạn!", null));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.confirmEmail(token));
        assertEquals("Xác nhận email thất bại", exception.getMessage());
    }

    @Test
    void confirmEmail_emailAlreadyConfirmed_throwsException() {
        // Arrange
        String token = "alreadyConfirmedToken";
        User user = User.builder().emailConfirmed(true).build(); // email đã xác nhận
        Token emailToken = Token.builder().user(user).build();

        when(emailConfirmationService.validateConfirmationToken(token)).thenReturn(emailToken);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.confirmEmail(token));
        assertEquals("Email đã được xác nhận trước đó", exception.getMessage());
    }


}