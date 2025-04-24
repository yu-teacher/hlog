package com.hunnit_beasts.hlog.profile.domain.repository;

import com.hunnit_beasts.hlog.profile.domain.model.entity.Profile;
import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {
    Profile save(Profile profile);
    Optional<Profile> findById(ProfileId id);
    Optional<Profile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    void delete(Profile profile);
    Optional<Profile> findByCustomUrl(String customUrl);
    boolean existsByCustomUrl(String customUrl);
}