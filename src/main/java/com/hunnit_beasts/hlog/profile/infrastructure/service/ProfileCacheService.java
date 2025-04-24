package com.hunnit_beasts.hlog.profile.infrastructure.service;

import com.hunnit_beasts.hlog.profile.domain.model.entity.Profile;
import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ProfileCacheService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    // 프로필 ID 기반 캐시
    private final Map<UUID, CacheEntry<Profile>> profileCache = new ConcurrentHashMap<>();

    // 사용자 ID 기반 캐시
    private final Map<UUID, CacheEntry<Profile>> userIdToProfileCache = new ConcurrentHashMap<>();

    // 커스텀 URL 기반 캐시
    private final Map<String, CacheEntry<Profile>> customUrlToProfileCache = new ConcurrentHashMap<>();

    public void cacheProfile(Profile profile) {
        if (profile == null) {
            return;
        }

        CacheEntry<Profile> entry = new CacheEntry<>(profile);

        profileCache.put(profile.getId().getValue(), entry);
        userIdToProfileCache.put(profile.getUserId(), entry);

        if (profile.getSettings().getCustomUrl() != null) {
            customUrlToProfileCache.put(profile.getSettings().getCustomUrl(), entry);
        }
    }

    public Optional<Profile> getProfileById(ProfileId id) {
        if (id == null) {
            return Optional.empty();
        }

        return getCachedEntry(profileCache.get(id.getValue()));
    }

    public Optional<Profile> getProfileByUserId(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }

        return getCachedEntry(userIdToProfileCache.get(userId));
    }

    public Optional<Profile> getProfileByCustomUrl(String customUrl) {
        if (customUrl == null) {
            return Optional.empty();
        }

        return getCachedEntry(customUrlToProfileCache.get(customUrl));
    }

    public void removeFromCache(Profile profile) {
        if (profile == null) {
            return;
        }

        profileCache.remove(profile.getId().getValue());
        userIdToProfileCache.remove(profile.getUserId());

        if (profile.getSettings().getCustomUrl() != null) {
            customUrlToProfileCache.remove(profile.getSettings().getCustomUrl());
        }
    }

    public void clearCache() {
        profileCache.clear();
        userIdToProfileCache.clear();
        customUrlToProfileCache.clear();
        log.info("Profile cache cleared");
    }

    private <T> Optional<T> getCachedEntry(CacheEntry<T> entry) {
        if (entry == null) {
            return Optional.empty();
        }

        if (entry.isExpired()) {
            return Optional.empty();
        }

        return Optional.of(entry.getValue());
    }

    private static class CacheEntry<T> {
        @Getter
        private final T value;
        private final LocalDateTime expiresAt;

        public CacheEntry(T value) {
            this.value = value;
            this.expiresAt = LocalDateTime.now().plus(CACHE_TTL);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}