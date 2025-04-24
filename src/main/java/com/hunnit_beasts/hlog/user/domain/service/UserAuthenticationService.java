package com.hunnit_beasts.hlog.user.domain.service;

import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.Email;
import com.hunnit_beasts.hlog.user.domain.model.vo.Password;

import java.util.Optional;

public interface UserAuthenticationService {
    boolean authenticate(Email email, Password password);
    Optional<User> getCurrentUser();
}
