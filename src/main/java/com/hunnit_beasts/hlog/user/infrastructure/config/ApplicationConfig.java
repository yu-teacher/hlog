package com.hunnit_beasts.hlog.user.infrastructure.config;

import com.hunnit_beasts.hlog.user.domain.repository.UserRepository;
import com.hunnit_beasts.hlog.user.domain.service.PasswordEncryptionService;
import com.hunnit_beasts.hlog.user.infrastructure.security.PasswordEncoderImpl;
import com.hunnit_beasts.hlog.user.infrastructure.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncryptionService passwordEncryptionService() {
        return new PasswordEncoderImpl(passwordEncoder());
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
