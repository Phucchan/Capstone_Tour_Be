package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.UserDTO;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;

public interface ChatService {
    UserBasicDTO connect(UserDTO userDTO);

    UserBasicDTO disconnect(UserBasicDTO userDTO);
}
