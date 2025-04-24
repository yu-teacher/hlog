package com.hunnit_beasts.hlog.user.api.dto;

import lombok.Getter;

@Getter
public class JwtAuthenticationResponse {
    private final String accessToken;
    private final String tokenType;

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
