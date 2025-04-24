package com.hunnit_beasts.hlog.post.application;

import com.hunnit_beasts.hlog.post.application.dto.CreateSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.SeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdateSeriesDto;
import com.hunnit_beasts.hlog.post.application.mapper.SeriesDtoMapper;
import com.hunnit_beasts.hlog.post.application.service.SeriesService;
import com.hunnit_beasts.hlog.post.application.service.SeriesServiceImpl;
import com.hunnit_beasts.hlog.post.domain.event.SeriesCreatedEvent;
import com.hunnit_beasts.hlog.post.domain.event.SeriesEvent;
import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.entity.SeriesPost;
import com.hunnit_beasts.hlog.post.domain.model.vo.*;
import com.hunnit_beasts.hlog.post.domain.repository.PostRepository;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesPostRepository;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesRepository;
import com.hunnit_beasts.hlog.post.infrastructure.messaging.PostEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("시리즈 서비스 테스트")
class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SeriesPostRepository seriesPostRepository;

    @Mock
    private SeriesDtoMapper seriesDtoMapper;

    @Mock
    private PostEventPublisher eventPublisher;

    private SeriesService seriesService;

    @BeforeEach
    void setUp() {
        seriesService = new SeriesServiceImpl(
                seriesRepository,
                postRepository,
                seriesPostRepository,
                seriesDtoMapper,
                eventPublisher
        );
    }

    @Test
    @DisplayName("시리즈 생성 시 시리즈가 저장되고 이벤트가 발행되어야 한다")
    void createSeries_ShouldSaveSeriesAndPublishEvent() {
        // Given
        CreateSeriesDto createSeriesDto = CreateSeriesDto.builder()
                .name("Test Series")
                .description("Test Description")
                .authorId(UUID.randomUUID())
                .build();

        Series series = Series.create(
                SeriesName.of(createSeriesDto.getName()),
                Description.of(createSeriesDto.getDescription()),
                createSeriesDto.getAuthorId()
        );

        SeriesDto expectedSeriesDto = SeriesDto.builder()
                .id(series.getId().getValue())
                .name(series.getName().getValue())
                .description(series.getDescription().getValue())
                .authorId(series.getAuthorId())
                .status(series.getStatus().toString())
                .createdAt(series.getCreatedAt())
                .updatedAt(series.getUpdatedAt())
                .build();

        when(seriesDtoMapper.toEntity(createSeriesDto)).thenReturn(series);
        when(seriesRepository.save(series)).thenReturn(series);
        when(seriesDtoMapper.toDto(eq(series), anyList(), anyMap())).thenReturn(expectedSeriesDto);

        // When
        SeriesDto result = seriesService.createSeries(createSeriesDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedSeriesDto.getId(), result.getId());
        assertEquals(expectedSeriesDto.getName(), result.getName());

        verify(seriesRepository).save(series);

        ArgumentCaptor<SeriesCreatedEvent> eventCaptor = ArgumentCaptor.forClass(SeriesCreatedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        SeriesCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(series.getId(), capturedEvent.getSeriesId());
    }

    @Test
    @DisplayName("ID로 시리즈 조회 시 시리즈가 반환되어야 한다")
    void getSeriesById_ShouldReturnSeries() {
        // Given
        UUID seriesId = UUID.randomUUID();
        Series series = Series.create(
                SeriesName.of("Test Series"),
                Description.of("Test Description"),
                UUID.randomUUID()
        );

        List<SeriesPost> seriesPosts = new ArrayList<>();

        SeriesDto expectedSeriesDto = SeriesDto.builder()
                .id(series.getId().getValue())
                .name(series.getName().getValue())
                .description(series.getDescription().getValue())
                .build();

        when(seriesRepository.findById(any(SeriesId.class))).thenReturn(Optional.of(series));
        when(seriesPostRepository.findBySeriesId(any(SeriesId.class))).thenReturn(seriesPosts);
        when(seriesDtoMapper.toDto(eq(series), eq(seriesPosts), anyMap())).thenReturn(expectedSeriesDto);

        // When
        SeriesDto result = seriesService.getSeriesById(seriesId);

        // Then
        assertNotNull(result);
        assertEquals(expectedSeriesDto.getId(), result.getId());
        assertEquals(expectedSeriesDto.getName(), result.getName());

        verify(seriesRepository).findById(any(SeriesId.class));
        verify(seriesPostRepository).findBySeriesId(any(SeriesId.class));
    }

    @Test
    @DisplayName("시리즈 업데이트 시 시리즈가 수정되고 저장되어야 한다")
    void updateSeries_ShouldUpdateAndSaveSeries() {
        // Given
        UUID seriesId = UUID.randomUUID();
        UpdateSeriesDto updateSeriesDto = UpdateSeriesDto.builder()
                .name("Updated Series")
                .description("Updated Description")
                .build();

        Series series = Series.create(
                SeriesName.of("Old Series"),
                Description.of("Old Description"),
                UUID.randomUUID()
        );

        List<SeriesPost> seriesPosts = new ArrayList<>();

        SeriesDto expectedSeriesDto = SeriesDto.builder()
                .id(series.getId().getValue())
                .name("Updated Series")
                .description("Updated Description")
                .build();

        when(seriesRepository.findById(any(SeriesId.class))).thenReturn(Optional.of(series));
        when(seriesRepository.save(series)).thenReturn(series);
        when(seriesPostRepository.findBySeriesId(any(SeriesId.class))).thenReturn(seriesPosts);
        when(seriesDtoMapper.toDto(eq(series), eq(seriesPosts), anyMap())).thenReturn(expectedSeriesDto);

        // When
        SeriesDto result = seriesService.updateSeries(seriesId, updateSeriesDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedSeriesDto.getId(), result.getId());
        assertEquals(expectedSeriesDto.getName(), result.getName());
        assertEquals(expectedSeriesDto.getDescription(), result.getDescription());

        verify(seriesDtoMapper).updateEntityFromDto(series, updateSeriesDto);
        verify(seriesRepository).save(series);
        verify(eventPublisher).publish((SeriesEvent) any());
    }

    @Test
    @DisplayName("시리즈에 포스트 추가 시 관계가 생성되어야 한다")
    void addPostToSeries_ShouldCreateSeriesPostRelation() {
        // Given
        UUID seriesId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        Integer order = 1;

        Series series = Series.create(
                SeriesName.of("Test Series"),
                Description.of("Test Description"),
                UUID.randomUUID()
        );

        Post post = Post.create(
                Title.of("Test Post"),
                Content.ofMarkdown("Test Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        SeriesPost seriesPost = SeriesPost.create(series.getId(), post.getId(), order);

        when(seriesRepository.findById(any(SeriesId.class))).thenReturn(Optional.of(series));
        when(postRepository.findById(any(PostId.class))).thenReturn(Optional.of(post));
        when(seriesPostRepository.existsBySeriesIdAndPostId(any(SeriesId.class), any(PostId.class))).thenReturn(false);
        when(seriesPostRepository.save(any(SeriesPost.class))).thenReturn(seriesPost);

        // Mock for getSeriesById call
        SeriesDto expectedSeriesDto = SeriesDto.builder()
                .id(series.getId().getValue())
                .name(series.getName().getValue())
                .build();
        when(seriesPostRepository.findBySeriesId(any(SeriesId.class))).thenReturn(Collections.singletonList(seriesPost));
        when(seriesDtoMapper.toDto(eq(series), anyList(), anyMap())).thenReturn(expectedSeriesDto);

        // When
        SeriesDto result = seriesService.addPostToSeries(seriesId, postId, order);

        // Then
        assertNotNull(result);
        assertEquals(expectedSeriesDto.getId(), result.getId());

        verify(seriesPostRepository).save(any(SeriesPost.class));
        verify(eventPublisher).publish((SeriesEvent) any());
    }
}
