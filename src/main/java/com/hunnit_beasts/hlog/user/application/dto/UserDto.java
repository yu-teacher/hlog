package com.hunnit_beasts.hlog.user.application.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserDto {
    private final String id;
    private final String email;
    private final String username;
    private final String status;
    private final Set<String> roles;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
