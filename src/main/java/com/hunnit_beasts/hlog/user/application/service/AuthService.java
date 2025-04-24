package com.hunnit_beasts.hlog.user.application.service;

public interface AuthService {
    String authenticate(String usernameOrEmail, String password);
}
