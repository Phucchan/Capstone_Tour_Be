package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.common.UserDTO;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import com.fpt.capstone.tourism.repository.UserRepository;
import com.fpt.capstone.tourism.service.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public UserDTO connect(UserDTO userDTO) {
        //Find user in repository
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        //If present then set the status to online
        user.ifPresent(u -> {
            u.setUserStatus(UserStatus.ONLINE);
            userRepository.save(u);
        });
        return user.map(userMapper::toUserDTO).orElse(null);
    }
}
