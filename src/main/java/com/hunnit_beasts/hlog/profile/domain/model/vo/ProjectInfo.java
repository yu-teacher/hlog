package com.hunnit_beasts.hlog.profile.domain.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectInfo {
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    private final String title;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public static ProjectInfo create(String title, String description,
                                     LocalDate startDate, LocalDate endDate) {
        validateTitle(title);
        validateDescription(description);
        validateDates(startDate, endDate);
        return new ProjectInfo(title, description, startDate, endDate);
    }

    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Project title cannot be empty");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Project title cannot exceed " + MAX_TITLE_LENGTH + " characters");
        }
    }

    private static void validateDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Project description cannot be null");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Project description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }

    private static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectInfo that = (ProjectInfo) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, startDate, endDate);
    }
}