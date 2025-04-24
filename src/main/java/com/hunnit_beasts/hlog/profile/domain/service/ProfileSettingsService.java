package com.hunnit_beasts.hlog.profile.domain.service;

public interface ProfileSettingsService {
    void validateCustomUrl(String customUrl);
    boolean isCustomUrlAvailable(String customUrl);
    String generateCustomUrl(String base);
}