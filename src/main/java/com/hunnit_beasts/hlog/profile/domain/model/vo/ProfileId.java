package com.hunnit_beasts.hlog.profile.domain.model.vo;

import lombok.Getter;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ProfileId {
    private final UUID value;

    private ProfileId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static ProfileId create() {
        return new ProfileId(UUID.randomUUID());
    }

    public static ProfileId of(UUID value) {
        return new ProfileId(value);
    }

    public static ProfileId of(String value) {
        return new ProfileId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileId profileId = (ProfileId) o;
        return value.equals(profileId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}