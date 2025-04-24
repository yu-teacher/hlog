package com.hunnit_beasts.hlog.user.application.mapper;

import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.Email;
import com.hunnit_beasts.hlog.user.domain.model.vo.Password;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserDtoMapper {
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId().getValue().toString())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(CreateUserDto dto) {
        return User.create(
                Email.of(dto.getEmail()),
                Username.of(dto.getUsername()),
                Password.of(dto.getPassword())
        );
    }
}
