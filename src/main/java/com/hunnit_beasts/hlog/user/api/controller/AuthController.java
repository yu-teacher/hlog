package com.hunnit_beasts.hlog.user.api.controller;

import com.hunnit_beasts.hlog.user.api.dto.JwtAuthenticationResponse;
import com.hunnit_beasts.hlog.user.api.dto.UserLoginRequest;
import com.hunnit_beasts.hlog.user.api.dto.UserRegistrationRequest;
import com.hunnit_beasts.hlog.user.api.dto.UserResponse;
import com.hunnit_beasts.hlog.user.api.mapper.UserApiMapper;
import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.service.AuthService;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final UserApiMapper userApiMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest registrationRequest) {
        CreateUserDto createDto = userApiMapper.toCreateDto(registrationRequest);
        UserDto createdUser = userService.registerUser(createDto);
        UserResponse response = userApiMapper.toResponse(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(
            @Valid @RequestBody UserLoginRequest loginRequest) {

        String jwt = authService.authenticate(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
        );

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
