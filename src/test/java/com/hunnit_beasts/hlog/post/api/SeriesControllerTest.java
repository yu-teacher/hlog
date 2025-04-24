package com.hunnit_beasts.hlog.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.post.api.dto.CreateSeriesRequest;
import com.hunnit_beasts.hlog.post.api.dto.SeriesResponse;
import com.hunnit_beasts.hlog.post.api.dto.UpdateSeriesRequest;
import com.hunnit_beasts.hlog.post.api.mapper.SeriesApiMapper;
import com.hunnit_beasts.hlog.post.application.dto.CreateSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.SeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdateSeriesDto;
import com.hunnit_beasts.hlog.post.application.service.SeriesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("시리즈 컨트롤러 테스트")
class SeriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SeriesService seriesService;

    @MockitoBean
    private SeriesApiMapper seriesApiMapper;

    @Test
    @DisplayName("시리즈 생성 요청 시 생성된 시리즈가 반환되어야 한다")
    void createSeries_ShouldReturnCreatedSeries() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        CreateSeriesRequest request = CreateSeriesRequest.builder()
                .name("Test Series")
                .description("Test Description")
                .authorId(authorId)
                .build();

        CreateSeriesDto createSeriesDto = CreateSeriesDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .authorId(request.getAuthorId())
                .build();

        UUID seriesId = UUID.randomUUID();
        SeriesDto seriesDto = SeriesDto.builder()
                .id(seriesId)
                .name(request.getName())
                .description(request.getDescription())
                .authorId(authorId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .posts(new ArrayList<>())
                .build();

        SeriesResponse seriesResponse = SeriesResponse.builder()
                .id(seriesId)
                .name(seriesDto.getName())
                .description(seriesDto.getDescription())
                .authorId(seriesDto.getAuthorId())
                .authorName("Test Author")
                .status(seriesDto.getStatus())
                .createdAt(seriesDto.getCreatedAt())
                .updatedAt(seriesDto.getUpdatedAt())
                .posts(new ArrayList<>())
                .build();

        when(seriesApiMapper.toCreateSeriesDto(any(CreateSeriesRequest.class))).thenReturn(createSeriesDto);
        when(seriesService.createSeries(any(CreateSeriesDto.class))).thenReturn(seriesDto);
        when(seriesApiMapper.toSeriesResponse(any(SeriesDto.class))).thenReturn(seriesResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/series")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(seriesId.toString()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));

        verify(seriesApiMapper).toCreateSeriesDto(any(CreateSeriesRequest.class));
        verify(seriesService).createSeries(any(CreateSeriesDto.class));
        verify(seriesApiMapper).toSeriesResponse(any(SeriesDto.class));
    }

    @Test
    @DisplayName("ID로 시리즈 조회 요청 시 시리즈가 반환되어야 한다")
    void getSeriesById_ShouldReturnSeries() throws Exception {
        // Given
        UUID seriesId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        SeriesDto seriesDto = SeriesDto.builder()
                .id(seriesId)
                .name("Test Series")
                .description("Test Description")
                .authorId(authorId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .posts(new ArrayList<>())
                .build();

        SeriesResponse seriesResponse = SeriesResponse.builder()
                .id(seriesId)
                .name(seriesDto.getName())
                .description(seriesDto.getDescription())
                .authorId(seriesDto.getAuthorId())
                .authorName("Test Author")
                .status(seriesDto.getStatus())
                .createdAt(seriesDto.getCreatedAt())
                .updatedAt(seriesDto.getUpdatedAt())
                .posts(new ArrayList<>())
                .build();

        when(seriesService.getSeriesById(seriesId)).thenReturn(seriesDto);
        when(seriesApiMapper.toSeriesResponse(seriesDto)).thenReturn(seriesResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/series/{seriesId}", seriesId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seriesId.toString()))
                .andExpect(jsonPath("$.name").value(seriesDto.getName()))
                .andExpect(jsonPath("$.status").value(seriesDto.getStatus()));

        verify(seriesService).getSeriesById(seriesId);
        verify(seriesApiMapper).toSeriesResponse(seriesDto);
    }

    @Test
    @DisplayName("시리즈 업데이트 요청 시 업데이트된 시리즈가 반환되어야 한다")
    void updateSeries_ShouldReturnUpdatedSeries() throws Exception {
        // Given
        UUID seriesId = UUID.randomUUID();
        UpdateSeriesRequest request = UpdateSeriesRequest.builder()
                .name("Updated Series")
                .description("Updated Description")
                .build();

        UpdateSeriesDto updateSeriesDto = UpdateSeriesDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        UUID authorId = UUID.randomUUID();
        SeriesDto updatedSeriesDto = SeriesDto.builder()
                .id(seriesId)
                .name(request.getName())
                .description(request.getDescription())
                .authorId(authorId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .posts(new ArrayList<>())
                .build();

        SeriesResponse seriesResponse = SeriesResponse.builder()
                .id(seriesId)
                .name(updatedSeriesDto.getName())
                .description(updatedSeriesDto.getDescription())
                .authorId(updatedSeriesDto.getAuthorId())
                .authorName("Test Author")
                .status(updatedSeriesDto.getStatus())
                .createdAt(updatedSeriesDto.getCreatedAt())
                .updatedAt(updatedSeriesDto.getUpdatedAt())
                .posts(new ArrayList<>())
                .build();

        when(seriesApiMapper.toUpdateSeriesDto(any(UpdateSeriesRequest.class))).thenReturn(updateSeriesDto);
        when(seriesService.updateSeries(eq(seriesId), any(UpdateSeriesDto.class))).thenReturn(updatedSeriesDto);
        when(seriesApiMapper.toSeriesResponse(any(SeriesDto.class))).thenReturn(seriesResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/series/{seriesId}", seriesId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seriesId.toString()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));

        verify(seriesApiMapper).toUpdateSeriesDto(any(UpdateSeriesRequest.class));
        verify(seriesService).updateSeries(eq(seriesId), any(UpdateSeriesDto.class));
        verify(seriesApiMapper).toSeriesResponse(any(SeriesDto.class));
    }

    @Test
    @DisplayName("시리즈 삭제 요청 시 204 상태코드가 반환되어야 한다")
    void deleteSeries_ShouldReturnNoContent() throws Exception {
        // Given
        UUID seriesId = UUID.randomUUID();
        doNothing().when(seriesService).deleteSeries(seriesId);

        // When & Then
        mockMvc.perform(delete("/api/v1/series/{seriesId}", seriesId))
                .andExpect(status().isNoContent());

        verify(seriesService).deleteSeries(seriesId);
    }

    @Test
    @DisplayName("시리즈에 포스트 추가 요청 시 업데이트된 시리즈가 반환되어야 한다")
    void addPostToSeries_ShouldReturnUpdatedSeries() throws Exception {
        // Given
        UUID seriesId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        Integer order = 1;

        UUID authorId = UUID.randomUUID();
        SeriesDto updatedSeriesDto = SeriesDto.builder()
                .id(seriesId)
                .name("Test Series")
                .description("Test Description")
                .authorId(authorId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SeriesResponse seriesResponse = SeriesResponse.builder()
                .id(seriesId)
                .name(updatedSeriesDto.getName())
                .description(updatedSeriesDto.getDescription())
                .authorId(updatedSeriesDto.getAuthorId())
                .authorName("Test Author")
                .status(updatedSeriesDto.getStatus())
                .createdAt(updatedSeriesDto.getCreatedAt())
                .updatedAt(updatedSeriesDto.getUpdatedAt())
                .build();

        when(seriesService.addPostToSeries(seriesId, postId, order)).thenReturn(updatedSeriesDto);
        when(seriesApiMapper.toSeriesResponse(updatedSeriesDto)).thenReturn(seriesResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/series/{seriesId}/posts/{postId}", seriesId, postId)
                        .param("order", order.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seriesId.toString()));

        verify(seriesService).addPostToSeries(seriesId, postId, order);
        verify(seriesApiMapper).toSeriesResponse(updatedSeriesDto);
    }
}
