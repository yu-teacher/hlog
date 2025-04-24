package com.hunnit_beasts.hlog.user.application.service;

import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UpdateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(CreateUserDto createUserDto);
    UserDto getUserById(String userId);
    UserDto getUserByEmail(String email);
    UserDto getUserByUsername(String username);
    UserDto updateUser(String userId, UpdateUserDto updateUserDto);
    void deleteUser(String userId);
    List<UserDto> getAllUsers();
    UserDto changeUserStatus(String userId, String status);
    UserDto addRoleToUser(String userId, String roleName);
    UserDto removeRoleFromUser(String userId, String roleName);
}
