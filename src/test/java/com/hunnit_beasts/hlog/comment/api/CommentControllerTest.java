package com.hunnit_beasts.hlog.comment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.comment.api.dto.CommentResponse;
import com.hunnit_beasts.hlog.comment.api.dto.CreateCommentRequest;
import com.hunnit_beasts.hlog.comment.api.dto.UpdateCommentRequest;
import com.hunnit_beasts.hlog.comment.api.mapper.CommentApiMapper;
import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.CreateCommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.UpdateCommentDto;
import com.hunnit_beasts.hlog.comment.application.service.CommentService;
import com.hunnit_beasts.hlog.user.api.facade.UserServiceFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CommentApiMapper commentApiMapper;

    @MockitoBean
    private UserServiceFacade userServiceFacade;

    private static final UUID TEST_USER_ID = UUID.randomUUID(); // 테스트용 고정 UUID 생성

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("댓글 생성 API 테스트")
    void testCreateComment() throws Exception {
        // given
        UUID targetId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"); // 유효한 UUID 형식 사용

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("New comment")
                .targetId(targetId)
                .build();

        CreateCommentDto createDto = CreateCommentDto.builder()
                .content("New comment")
                .targetId(targetId)
                .authorId(authorId)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .content("New comment")
                .targetId(targetId)
                .authorId(authorId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .content("New comment")
                .targetId(targetId)
                .authorId(authorId)
                .authorName("Test User")
                .status("ACTIVE")
                .createdAt(commentDto.getCreatedAt())
                .updatedAt(commentDto.getUpdatedAt())
                .build();

        when(commentApiMapper.toCreateDto(any(CreateCommentRequest.class), eq(authorId))).thenReturn(createDto);
        when(commentService.createComment(any(CreateCommentDto.class))).thenReturn(commentDto);
        when(userServiceFacade.getUsernameById(any(String.class))).thenReturn("Test User");
        when(commentApiMapper.toResponse(any(CommentDto.class), eq("Test User"))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("New comment"))
                .andExpect(jsonPath("$.authorName").value("Test User"));
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("댓글 수정 API 테스트")
    void testUpdateComment() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"); // 유효한 UUID 형식 사용
        UUID targetId = UUID.randomUUID();

        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .id(commentId)
                .content("Updated comment")
                .build();

        UpdateCommentDto updateDto = UpdateCommentDto.builder()
                .id(commentId)
                .content("Updated comment")
                .build();

        CommentDto existingDto = CommentDto.builder()
                .id(commentId)
                .content("Original comment")
                .targetId(targetId)
                .authorId(authorId) // 본인 댓글이므로 권한 있음
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();

        CommentDto updatedDto = CommentDto.builder()
                .id(commentId)
                .content("Updated comment")
                .targetId(targetId)
                .authorId(authorId)
                .status("ACTIVE")
                .createdAt(existingDto.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .content("Updated comment")
                .targetId(targetId)
                .authorId(authorId)
                .authorName("Test User")
                .status("ACTIVE")
                .createdAt(updatedDto.getCreatedAt())
                .updatedAt(updatedDto.getUpdatedAt())
                .build();

        when(commentService.getComment(any(UUID.class))).thenReturn(existingDto);
        when(commentApiMapper.toUpdateDto(any(UpdateCommentRequest.class))).thenReturn(updateDto);
        when(commentService.updateComment(any(UpdateCommentDto.class))).thenReturn(updatedDto);
        when(userServiceFacade.getUsernameById(any(String.class))).thenReturn("Test User");
        when(commentApiMapper.toResponse(any(CommentDto.class), eq("Test User"))).thenReturn(response);

        // when & then
        mockMvc.perform(put("/api/comments/" + commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Updated comment"))
                .andExpect(jsonPath("$.authorName").value("Test User"));
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("다른 사용자의 댓글 수정 시도 시 권한 오류 테스트")
    void testUpdateCommentForbidden() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID(); // 다른 사용자
        UUID currentUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"); // 현재 로그인한 사용자

        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .id(commentId)
                .content("Updated comment")
                .build();

        CommentDto existingDto = CommentDto.builder()
                .id(commentId)
                .authorId(authorId) // 다른 사람의 댓글
                .build();

        when(commentService.getComment(any(UUID.class))).thenReturn(existingDto);

        // when & then
        mockMvc.perform(put("/api/comments/" + commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("댓글 삭제 API 테스트")
    void testDeleteComment() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"); // 유효한 UUID 형식 사용

        CommentDto existingDto = CommentDto.builder()
                .id(commentId)
                .authorId(authorId) // 본인 댓글이므로 권한 있음
                .build();

        when(commentService.getComment(any(UUID.class))).thenReturn(existingDto);

        // when & then
        mockMvc.perform(delete("/api/comments/" + commentId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("특정 게시물의 댓글 목록 조회 API 테스트")
    void testGetCommentsByTarget() throws Exception {
        // given
        UUID targetId = UUID.randomUUID();
        UUID commentId1 = UUID.randomUUID();
        UUID commentId2 = UUID.randomUUID();
        UUID authorId1 = UUID.randomUUID();
        UUID authorId2 = UUID.randomUUID();

        CommentDto dto1 = CommentDto.builder()
                .id(commentId1)
                .content("Comment 1")
                .targetId(targetId)
                .authorId(authorId1)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        CommentDto dto2 = CommentDto.builder()
                .id(commentId2)
                .content("Comment 2")
                .targetId(targetId)
                .authorId(authorId2)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();

        List<CommentDto> commentDtos = Arrays.asList(dto1, dto2);

        CommentResponse response1 = CommentResponse.builder()
                .id(commentId1)
                .content("Comment 1")
                .targetId(targetId)
                .authorId(authorId1)
                .authorName("User 1")
                .status("ACTIVE")
                .createdAt(dto1.getCreatedAt())
                .updatedAt(dto1.getUpdatedAt())
                .build();

        CommentResponse response2 = CommentResponse.builder()
                .id(commentId2)
                .content("Comment 2")
                .targetId(targetId)
                .authorId(authorId2)
                .authorName("User 2")
                .status("ACTIVE")
                .createdAt(dto2.getCreatedAt())
                .updatedAt(dto2.getUpdatedAt())
                .build();

        when(commentService.getCommentsByTarget(any(UUID.class))).thenReturn(commentDtos);
        when(userServiceFacade.getUsernameById(any(String.class))).thenReturn("User 1", "User 2");
        when(commentApiMapper.toResponse(eq(dto1), eq("User 1"))).thenReturn(response1);
        when(commentApiMapper.toResponse(eq(dto2), eq("User 2"))).thenReturn(response2);

        // when & then
        mockMvc.perform(get("/api/comments/target/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments", hasSize(2)))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.comments[0].id").value(commentId1.toString()))
                .andExpect(jsonPath("$.comments[0].content").value("Comment 1"))
                .andExpect(jsonPath("$.comments[0].authorName").value("User 1"))
                .andExpect(jsonPath("$.comments[1].id").value(commentId2.toString()))
                .andExpect(jsonPath("$.comments[1].content").value("Comment 2"))
                .andExpect(jsonPath("$.comments[1].authorName").value("User 2"));
    }
}