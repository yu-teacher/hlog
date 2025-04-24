package com.hunnit_beasts.hlog.user.api.facade;

import com.hunnit_beasts.hlog.user.application.dto.UserDto;

public interface UserServiceFacade {
    boolean existsById(String userId);
    UserDto getUserById(String userId);
    String getUsernameById(String userId);
    boolean hasPermission(String userId, String permission);
}
