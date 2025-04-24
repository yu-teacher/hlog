package com.hunnit_beasts.hlog.user.domain.model.entity;

import com.hunnit_beasts.hlog.user.domain.model.vo.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class User {
    private final UserId id;
    private Email email;
    private Username username;
    private Password password;
    private UserStatus status;
    private final Set<Role> roles;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(UserId id, Email email, Username username, Password password,
                 UserStatus status, Set<Role> roles, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.status = Objects.requireNonNull(status);
        this.roles = new HashSet<>(roles);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = createdAt;
    }

    public static User create(Email email, Username username, Password password) {
        return new User(
                UserId.create(),
                email,
                username,
                password,
                UserStatus.ACTIVE,
                Collections.singleton(Role.user()),
                LocalDateTime.now()
        );
    }

    // 변화 목적
    public static User reconstitute(
            UserId id, Email email, Username username, Password password,
            UserStatus status, Set<Role> roles,
            LocalDateTime createdAt, LocalDateTime updatedAt) {

        User user = new User(id, email, username, password, status, roles, createdAt);

        user.updatedAt = updatedAt;

        return user;
    }

    public void changeEmail(Email email) {
        this.email = Objects.requireNonNull(email);
        this.updatedAt = LocalDateTime.now();
    }

    public void changeUsername(Username username) {
        this.username = Objects.requireNonNull(username);
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(Password password) {
        this.password = Objects.requireNonNull(password);
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(UserStatus status) {
        this.status = Objects.requireNonNull(status);
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(Role role) {
        this.roles.add(Objects.requireNonNull(role));
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRole(Role role) {
        this.roles.remove(Objects.requireNonNull(role));
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }


    public Set<Role> getRoles() { return Collections.unmodifiableSet(roles); }

}
