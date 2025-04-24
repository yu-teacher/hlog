package com.hunnit_beasts.hlog.user.infrastructure.security;

import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.Email;
import com.hunnit_beasts.hlog.user.domain.model.vo.UserId;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import com.hunnit_beasts.hlog.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // username이 이메일 형식인지 확인
            if (username.contains("@")) {
                return loadUserByEmail(username);
            }

            // username이 UUID 형식인지 확인
            try {
                UUID uuid = UUID.fromString(username);
                return loadUserById(UserId.of(uuid));
            } catch (IllegalArgumentException e) {
                // UUID가 아니면 일반 사용자명으로 처리
                User user = userRepository.findByUsername(Username.of(username))
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
                return createCustomUserDetails(user);
            }
        } catch (Exception e) {
            log.error("Error loading user by username: {}", username, e);
            throw new UsernameNotFoundException("Failed to load user: " + e.getMessage());
        }
    }

    /**
     * 이메일로 사용자 정보 로드
     */
    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return createCustomUserDetails(user);
    }

    /**
     * ID로 사용자 정보 로드
     */
    public CustomUserDetails loadUserById(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id.getValue()));
        return createCustomUserDetails(user);
    }

    /**
     * ID로 사용자 정보 로드
     */
    public CustomUserDetails loadUserByUsername(Username username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username.getValue()));
        return createCustomUserDetails(user);
    }

    /**
     * 사용자 엔티티를 CustomUserDetails로 변환
     */
    private CustomUserDetails createCustomUserDetails(User user) {
        CustomUserInfoDTO userInfoDTO = mapUserToDto(user);
        return new CustomUserDetails(userInfoDTO);
    }

    /**
     * 사용자 엔티티를 DTO로 변환
     */
    private CustomUserInfoDTO mapUserToDto(User user) {
        // ModelMapper만으로 복잡한 변환이 어려울 수 있으므로 수동 매핑 추가
        CustomUserInfoDTO dto = mapper.map(user, CustomUserInfoDTO.class);

        // User 엔티티에서 필요한 추가 정보 매핑
        dto.setUserId(user.getId().getValue().toString());
        dto.setEmail(user.getEmail().getValue());

        // 역할 매핑 (User 엔티티의 역할 구조에 따라 조정 필요)
        CustomUserInfoDTO.Role role = CustomUserInfoDTO.Role.USER;
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        if (roleNames.contains("ADMIN")) {
            role = CustomUserInfoDTO.Role.ADMIN;
        }

        dto.setRole(role);
        dto.setPermissions(CustomUserInfoDTO.getDefaultPermissions(role));
        dto.setLastLoginAt(LocalDateTime.now());
        dto.setStatus(user.getStatus());

        return dto;
    }
}
