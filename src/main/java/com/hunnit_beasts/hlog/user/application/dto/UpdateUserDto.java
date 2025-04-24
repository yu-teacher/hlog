package com.hunnit_beasts.hlog.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UpdateUserDto {
    private final String email;
    private final String username;
    private final String password;
}
