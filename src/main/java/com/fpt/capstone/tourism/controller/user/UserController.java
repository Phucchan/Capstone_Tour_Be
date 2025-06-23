package com.fpt.capstone.tourism.controller.user;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/friends/{userId}")
    public ResponseEntity<GeneralResponse<List<UserBasicDTO>>> findFriends(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(userService.findFriends(userId));
    }


}
