package com.hunnit_beasts.hlog.user.api.mapper;

import com.hunnit_beasts.hlog.user.api.dto.UserRegistrationRequest;
import com.hunnit_beasts.hlog.user.api.dto.UserResponse;
import com.hunnit_beasts.hlog.user.api.dto.UserUpdateRequest;
import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UpdateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserApiMapper {
    public UserResponse toResponse(UserDto dto) {
        UserResponse response = new UserResponse();
        response.setId(dto.getId());
        response.setEmail(dto.getEmail());
        response.setUsername(dto.getUsername());
        response.setStatus(dto.getStatus());
        response.setRoles(dto.getRoles());
        response.setCreatedAt(dto.getCreatedAt());
        response.setUpdatedAt(dto.getUpdatedAt());
        return response;
    }

    public CreateUserDto toCreateDto(UserRegistrationRequest request) {
        return new CreateUserDto(
                request.getEmail(),
                request.getUsername(),
                request.getPassword()
        );
    }

    public UpdateUserDto toUpdateDto(UserUpdateRequest request) {
        return new UpdateUserDto(
                request.getEmail(),
                request.getUsername(),
                request.getPassword()
        );
    }
}
