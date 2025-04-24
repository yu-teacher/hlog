package com.hunnit_beasts.hlog.profile.domain.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Contact {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final int MAX_LOCATION_LENGTH = 100;

    private final String email;
    private final String location;

    public static Contact create(String email, String location) {
        validateEmail(email);
        validateLocation(location);
        return new Contact(email, location);
    }

    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private static void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        if (location.length() > MAX_LOCATION_LENGTH) {
            throw new IllegalArgumentException("Location cannot exceed " + MAX_LOCATION_LENGTH + " characters");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(email, contact.email) &&
                Objects.equals(location, contact.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, location);
    }
}