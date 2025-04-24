package com.hunnit_beasts.hlog.user.api.controller;

import com.hunnit_beasts.hlog.user.api.dto.UserResponse;
import com.hunnit_beasts.hlog.user.api.dto.UserUpdateRequest;
import com.hunnit_beasts.hlog.user.api.mapper.UserApiMapper;
import com.hunnit_beasts.hlog.user.application.dto.UpdateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserApiMapper userApiMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserById(userId);
        UserResponse response = userApiMapper.toResponse(userDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(userApiMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserUpdateRequest updateRequest) {

        UpdateUserDto updateDto = userApiMapper.toUpdateDto(updateRequest);
        UserDto updatedUser = userService.updateUser(userId, updateDto);
        UserResponse response = userApiMapper.toResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserResponse> changeUserStatus(
            @PathVariable("userId") String userId,
            @RequestParam("status") String status) {

        UserDto updatedUser = userService.changeUserStatus(userId, status);
        UserResponse response = userApiMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<UserResponse> addRoleToUser(
            @PathVariable("userId") String userId,
            @PathVariable("roleName") String roleName) {

        UserDto updatedUser = userService.addRoleToUser(userId, roleName);
        UserResponse response = userApiMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable("userId") String userId,
            @PathVariable("roleName") String roleName) {

        UserDto updatedUser = userService.removeRoleFromUser(userId, roleName);
        UserResponse response = userApiMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }
}
