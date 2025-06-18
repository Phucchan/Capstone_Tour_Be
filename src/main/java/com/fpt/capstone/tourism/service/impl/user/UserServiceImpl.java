package com.fpt.capstone.tourism.service.impl.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import com.fpt.capstone.tourism.repository.UserRepository;
import com.fpt.capstone.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fpt.capstone.tourism.constants.Constants.UserExceptionInformation.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;



    @Override
    public User findById(Long id) {
        return userRepository.findUserById(id).orElseThrow();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }




    @Override
    public User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw BusinessException.of(FAIL_TO_SAVE_USER_MESSAGE,e);
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
    public GeneralResponse<List<UserBasicDTO>> findOnlineFriends(Long userId) {
        return GeneralResponse.of(userRepository.findOnlineFriends(userId)
                .stream()
                .map(user -> UserBasicDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .avatarImage(user.getAvatarImage())
                        .build())
                .toList());
    }
}
