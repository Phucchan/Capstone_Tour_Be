package com.fpt.capstone.tourism.service.impl.user;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangePasswordRequestDTO;
import com.fpt.capstone.tourism.dto.request.UpdateProfileRequestDTO;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.dto.response.UserProfileResponseDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.validator.Validator;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.repository.user.UserPointRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.fpt.capstone.tourism.constants.Constants.UserExceptionInformation.*;
import static com.fpt.capstone.tourism.constants.Constants.Message.PASSWORDS_DO_NOT_MATCH_MESSAGE;
import static com.fpt.capstone.tourism.constants.Constants.UserExceptionInformation.PASSWORD_INVALID;
import static com.fpt.capstone.tourism.constants.Constants.Message.INVALID_OLD_PASSWORD_MESSAGE;
import static com.fpt.capstone.tourism.constants.Constants.Message.EMPTY_PASSWORD;
import static com.fpt.capstone.tourism.constants.Constants.Regex.REGEX_PASSWORD;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final BookingRepository bookingRepository;
    private final UserPointRepository userPointRepository;


    @Override
    public User findById(Long id) {
        return userRepository.findUserById(id).orElseThrow();
    }

    @Override
    public User findUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.of(USER_NOT_FOUND_MESSAGE));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.of(USER_NOT_FOUND_MESSAGE));
    }

    @Override
    public User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw BusinessException.of(FAIL_TO_SAVE_USER_MESSAGE, e);
        }
    }

    @Override
    public Boolean existsByUsername(String userName) {
        return userRepository.existsByUsername(userName);
    }

    @Override
    public Boolean exitsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByPhoneNumber(String phone) {

        return userRepository.existsByPhone(phone);
    }

    @Override
    public GeneralResponse<List<UserBasicDTO>> findFriends(Long userId) {

        List<User> friends = new ArrayList<>();
        friends.addAll(userRepository.findFriendsAsSender(userId));
        friends.addAll(userRepository.findFriendsAsReceiver(userId));

        return GeneralResponse.of(friends
                .stream()
                .map(userMapper::toUserBasicDTO)
                .toList());
    }

    @Override
    public GeneralResponse<UserBasicDTO> getUserBasic(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.of(USER_NOT_FOUND_MESSAGE));

        return GeneralResponse.of(userMapper.toUserBasicDTO(user));

    }

    @Override
    public GeneralResponse<UserProfileResponseDTO> getUserProfile(Long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> BusinessException.of(USER_NOT_FOUND_MESSAGE));
        UserProfileResponseDTO dto = userMapper.toUserProfileResponseDTO(user);
        dto.setTotalToursBooked(Math.toIntExact(bookingRepository.countByUser_Id(userId)));
        Integer points = userPointRepository.sumPointsByUserId(userId);
        dto.setPoints(points != null ? points : 0);
        return GeneralResponse.of(dto);
    }

    @Override
    public GeneralResponse<UserProfileResponseDTO> updateUserProfile(Long userId, UpdateProfileRequestDTO requestDTO) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> BusinessException.of(USER_NOT_FOUND_MESSAGE));

        if (requestDTO.getFullName() != null) user.setFullName(requestDTO.getFullName());
        if (requestDTO.getEmail() != null) user.setEmail(requestDTO.getEmail());
        if (requestDTO.getGender() != null) user.setGender(requestDTO.getGender());
        if (requestDTO.getPhone() != null) user.setPhone(requestDTO.getPhone());
        if (requestDTO.getAddress() != null) user.setAddress(requestDTO.getAddress());
        if (requestDTO.getAvatarImg() != null) user.setAvatarImage(requestDTO.getAvatarImg());
        if (requestDTO.getDateOfBirth() != null) user.setDateOfBirth(requestDTO.getDateOfBirth());

        User saved = userRepository.save(user);
        UserProfileResponseDTO dto = userMapper.toUserProfileResponseDTO(saved);
        dto.setTotalToursBooked(Math.toIntExact(bookingRepository.countByUser_Id(userId)));
        Integer points = userPointRepository.sumPointsByUserId(userId);
        dto.setPoints(points != null ? points : 0);
        return GeneralResponse.of(dto);
    }

    @Override
    public GeneralResponse<String> changePassword(Long userId, ChangePasswordRequestDTO requestDTO) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> BusinessException.of(USER_NOT_FOUND_MESSAGE));

        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), user.getPassword())) {
            throw BusinessException.of(INVALID_OLD_PASSWORD_MESSAGE);
        }

        if (requestDTO.getNewPassword() == null || requestDTO.getRePassword() == null) {
            throw BusinessException.of(EMPTY_PASSWORD);
        }

        if (!requestDTO.getNewPassword().equals(requestDTO.getRePassword())) {
            throw BusinessException.of(PASSWORDS_DO_NOT_MATCH_MESSAGE);
        }

        // validate new password pattern
        Validator.validateRegex(requestDTO.getNewPassword(), REGEX_PASSWORD, PASSWORD_INVALID);

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);

        return GeneralResponse.of("Đổi mật khẩu thành công");
    }

    @Override
    public GeneralResponse<PagingDTO<BookingSummaryDTO>> getBookingHistory(Long userId, Pageable pageable) {
        userRepository.findUserById(userId)
                .orElseThrow(() -> BusinessException.of(USER_NOT_FOUND_MESSAGE));

        Page<Booking> bookings = bookingRepository.findByUser_Id(userId, pageable);

        List<BookingSummaryDTO> dtos = bookings.getContent().stream()
                .map(b -> BookingSummaryDTO.builder()
                        .id(b.getId())
                        .bookingCode(b.getBookingCode())
                        .tourName(b.getTourSchedule().getTour().getName())
                        .status(b.getBookingStatus() != null ? b.getBookingStatus().name() : null)
                        .totalAmount(b.getTotalAmount())
                        .createdAt(b.getCreatedAt())
                        .build())
                .toList();

        PagingDTO<BookingSummaryDTO> pagingDTO = PagingDTO.<BookingSummaryDTO>builder()
                .page(bookings.getNumber())
                .size(bookings.getSize())
                .total(bookings.getTotalElements())
                .items(dtos)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.GET_BOOKING_LIST_SUCCESS, pagingDTO);
    }
    @Override
    @Transactional
    public GeneralResponse<String> requestBookingCancellation(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(Constants.Message.BOOKING_NOT_FOUND));

        if (booking.getUser() == null || !booking.getUser().getId().equals(userId)) {
            throw BusinessException.of(Constants.Message.BOOKING_NOT_FOUND);
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING &&
                booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw BusinessException.of("Chỉ có thể yêu cầu hủy cho đặt tour đang chờ xác nhận hoặc đã xác nhận");
        }

        booking.setBookingStatus(BookingStatus.CANCEL_REQUESTED);
        bookingRepository.save(booking);

        return GeneralResponse.of("Yêu cầu hủy đặt tour thành công");
    }
}
