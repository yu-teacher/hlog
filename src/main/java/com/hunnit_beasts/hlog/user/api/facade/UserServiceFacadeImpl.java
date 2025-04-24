package com.hunnit_beasts.hlog.user.api.facade;

import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceFacadeImpl implements UserServiceFacade {
    private final UserService userService;

    @Override
    public boolean existsById(String userId) {
        try {
            userService.getUserById(userId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public UserDto getUserById(String userId) {
        return userService.getUserById(userId);
    }

    @Override
    public String getUsernameById(String userId) {
        return userService.getUserById(userId).getUsername();
    }

    @Override
    public boolean hasPermission(String userId, String permission) {
        UserDto user = userService.getUserById(userId);
        return user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role) || permission.equals(role));
    }
}
