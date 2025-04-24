package com.hunnit_beasts.hlog.user.application.service;

import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UpdateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.mapper.UserDtoMapper;
import com.hunnit_beasts.hlog.user.domain.event.UserCreatedEvent;
import com.hunnit_beasts.hlog.user.domain.event.UserDeletedEvent;
import com.hunnit_beasts.hlog.user.domain.event.UserUpdatedEvent;
import com.hunnit_beasts.hlog.user.domain.model.entity.Role;
import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.*;
import com.hunnit_beasts.hlog.user.domain.repository.RoleRepository;
import com.hunnit_beasts.hlog.user.domain.repository.UserRepository;
import com.hunnit_beasts.hlog.user.domain.service.PasswordEncryptionService;
import com.hunnit_beasts.hlog.user.infrastructure.messaging.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final UserDtoMapper userDtoMapper;
    private final UserEventPublisher eventPublisher;

    @Override
    public UserDto registerUser(CreateUserDto createUserDto) {
        // 이메일, 사용자명 중복 검사
        if (userRepository.existsByEmail(Email.of(createUserDto.getEmail()))) {
            throw new IllegalArgumentException("Email already in use: " + createUserDto.getEmail());
        }

        if (userRepository.existsByUsername(Username.of(createUserDto.getUsername()))) {
            throw new IllegalArgumentException("Username already in use: " + createUserDto.getUsername());
        }

        // 엔티티 생성
        User user = userDtoMapper.toEntity(createUserDto);

        // 비밀번호 암호화
        user.changePassword(passwordEncryptionService.encrypt(user.getPassword()));

        // 저장
        User savedUser = userRepository.save(user);

        // 이벤트 발행
        eventPublisher.publish(new UserCreatedEvent(savedUser));

        // DTO 변환 후 반환
        return userDtoMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return userDtoMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        return userDtoMapper.toDto(user);
    }

    @Override
    @Transactional()
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(Username.of(username))
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        return userDtoMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(String userId, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 이메일 변경 시 중복 검사
        if (updateUserDto.getEmail() != null &&
                !user.getEmail().getValue().equals(updateUserDto.getEmail())) {

            if (userRepository.existsByEmail(Email.of(updateUserDto.getEmail()))) {
                throw new IllegalArgumentException("Email already in use: " + updateUserDto.getEmail());
            }
            user.changeEmail(Email.of(updateUserDto.getEmail()));
        }

        // 사용자명 변경 시 중복 검사
        if (updateUserDto.getUsername() != null &&
                !user.getUsername().getValue().equals(updateUserDto.getUsername())) {

            if (userRepository.existsByUsername(Username.of(updateUserDto.getUsername()))) {
                throw new IllegalArgumentException("Username already in use: " + updateUserDto.getUsername());
            }
            user.changeUsername(Username.of(updateUserDto.getUsername()));
        }

        // 비밀번호 변경
        if (updateUserDto.getPassword() != null) {
            Password newPassword = Password.of(updateUserDto.getPassword());
            user.changePassword(passwordEncryptionService.encrypt(newPassword));
        }

        // 저장
        User updatedUser = userRepository.save(user);

        // 이벤트 발행
        eventPublisher.publish(new UserUpdatedEvent(updatedUser));

        // DTO 변환 후 반환
        return userDtoMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        userRepository.delete(user);

        // 이벤트 발행
        eventPublisher.publish(new UserDeletedEvent(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto changeUserStatus(String userId, String status) {
        User user = userRepository.findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserStatus newStatus = UserStatus.valueOf(status.toUpperCase());
        user.changeStatus(newStatus);

        User updatedUser = userRepository.save(user);

        // 이벤트 발행
        eventPublisher.publish(new UserUpdatedEvent(updatedUser));

        return userDtoMapper.toDto(updatedUser);
    }

    @Override
    public UserDto addRoleToUser(String userId, String roleName) {
        User user = userRepository.findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        RoleName name = RoleName.valueOf(roleName.toUpperCase());
        Role role = roleRepository.findByName(name)
                .orElseGet(() -> {
                    if (name == RoleName.ADMIN) {
                        return Role.admin();
                    } else {
                        return Role.user();
                    }
                });

        user.addRole(role);

        User updatedUser = userRepository.save(user);

        // 이벤트 발행
        eventPublisher.publish(new UserUpdatedEvent(updatedUser));

        return userDtoMapper.toDto(updatedUser);
    }

    @Override
    public UserDto removeRoleFromUser(String userId, String roleName) {
        User user = userRepository.findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        RoleName name = RoleName.valueOf(roleName.toUpperCase());

        // 해당 역할을 찾아 제거
        user.getRoles().stream()
                .filter(r -> r.getName() == name)
                .findFirst()
                .ifPresent(user::removeRole);

        User updatedUser = userRepository.save(user);

        // 이벤트 발행
        eventPublisher.publish(new UserUpdatedEvent(updatedUser));

        return userDtoMapper.toDto(updatedUser);
    }
}