package com.hunnit_beasts.hlog.post.domain;

import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.vo.Description;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesName;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Series 엔티티 테스트")
class SeriesTest {

    @Test
    @DisplayName("시리즈 생성 시 유효한 시리즈가 반환되어야 한다")
    void createSeries_ShouldReturnValidSeries() {
        // Given
        String name = "Test Series";
        String description = "Test Description";
        UUID authorId = UUID.randomUUID();

        // When
        Series series = Series.create(
                SeriesName.of(name),
                Description.of(description),
                authorId
        );

        // Then
        assertNotNull(series);
        assertNotNull(series.getId());
        assertEquals(name, series.getName().getValue());
        assertEquals(description, series.getDescription().getValue());
        assertEquals(authorId, series.getAuthorId());
        assertEquals(SeriesStatus.ACTIVE, series.getStatus());
        assertNotNull(series.getCreatedAt());
        assertEquals(series.getCreatedAt(), series.getUpdatedAt());
    }

    @Test
    @DisplayName("이름 업데이트 시 이름이 변경되어야 한다")
    void updateName_ShouldChangeName() {
        // Given
        Series series = Series.create(
                SeriesName.of("Old Name"),
                Description.of("Test Description"),
                UUID.randomUUID()
        );

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        series.updateName(SeriesName.of("New Name"));

        // Then
        assertEquals("New Name", series.getName().getValue());
        assertNotEquals(series.getCreatedAt(), series.getUpdatedAt());
    }

    @Test
    @DisplayName("설명 업데이트 시 설명이 변경되어야 한다")
    void updateDescription_ShouldChangeDescription() {
        // Given
        Series series = Series.create(
                SeriesName.of("Test Series"),
                Description.of("Old Description"),
                UUID.randomUUID()
        );

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        series.updateDescription(Description.of("New Description"));

        // Then
        assertEquals("New Description", series.getDescription().getValue());
        assertNotEquals(series.getCreatedAt(), series.getUpdatedAt());
    }

    @Test
    @DisplayName("비활성화 및 활성화 시 상태가 변경되어야 한다")
    void deactivateAndActivate_ShouldChangeStatus() {
        // Given
        Series series = Series.create(
                SeriesName.of("Test Series"),
                Description.of("Test Description"),
                UUID.randomUUID()
        );

        // When
        series.deactivate();

        // Then
        assertEquals(SeriesStatus.INACTIVE, series.getStatus());

        // When
        series.activate();

        // Then
        assertEquals(SeriesStatus.ACTIVE, series.getStatus());
    }
}
