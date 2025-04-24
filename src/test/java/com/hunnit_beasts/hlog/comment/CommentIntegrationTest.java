package com.hunnit_beasts.hlog.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.comment.api.dto.CommentResponse;
import com.hunnit_beasts.hlog.comment.api.dto.CreateCommentRequest;
import com.hunnit_beasts.hlog.comment.api.dto.UpdateCommentRequest;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentStatus;
import com.hunnit_beasts.hlog.comment.infrastructure.persistence.entity.CommentJpaEntity;
import com.hunnit_beasts.hlog.comment.infrastructure.persistence.repository.CommentJpaRepository;
import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.service.PostService;
import com.hunnit_beasts.hlog.user.api.facade.UserServiceFacade;
import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @MockitoBean
    private UserServiceFacade userServiceFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    private UUID testTargetId;
    private UUID testAuthorId;
    private UUID testPost2Id;

    @BeforeEach
    void setUp() {
        // 테스트 유저 생성
        CreateUserDto createUserDto = new CreateUserDto("test@gmail.com", "testUser", "testPass");
        testAuthorId = UUID.fromString(userService.registerUser(createUserDto).getId());

        // 테스트 포스트 생성
        CreatePostDto createPostDto = CreatePostDto.builder()
                .title("Test Title")
                .content("Test Content")
                .contentFormat("MARKDOWN")
                .authorId(testAuthorId)
                .tagNames(new HashSet<>(Arrays.asList("java", "spring")))
                .publish(false)
                .build();
        testTargetId = postService.createPost(createPostDto).getId();

        // 두 번째 테스트 포스트 생성
        CreatePostDto createPostDto2 = CreatePostDto.builder()
                .title("Test Title 2")
                .content("Test Content 2")
                .contentFormat("MARKDOWN")
                .authorId(testAuthorId)
                .tagNames(new HashSet<>(Arrays.asList("test", "integration")))
                .publish(false)
                .build();
        testPost2Id = postService.createPost(createPostDto2).getId();

        // User 서비스 페이사드 모킹
        when(userServiceFacade.getUsernameById(any(String.class))).thenReturn("Test User");

        // 모든 테스트 댓글 제거
        commentJpaRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("댓글 생성, 조회, 수정, 삭제의 전체 흐름 테스트")
    void testCommentCRUDFlow() throws Exception {
        // 1. 댓글 생성
        CreateCommentRequest createRequest = CreateCommentRequest.builder()
                .content("통합 테스트용 댓글")
                .targetId(testTargetId)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("통합 테스트용 댓글"))
                .andExpect(jsonPath("$.targetId").value(testTargetId.toString()))
                .andExpect(jsonPath("$.authorName").value("Test User"))
                .andReturn();

        CommentResponse createdComment = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                CommentResponse.class
        );

        // 2. 댓글 조회
        mockMvc.perform(get("/api/comments/" + createdComment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdComment.getId().toString()))
                .andExpect(jsonPath("$.content").value("통합 테스트용 댓글"));

        // 3. 댓글 수정
        UpdateCommentRequest updateRequest = UpdateCommentRequest.builder()
                .id(createdComment.getId())
                .content("수정된 댓글 내용")
                .build();

        mockMvc.perform(put("/api/comments/" + createdComment.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 댓글 내용"));

        // 4. 댓글 삭제
        mockMvc.perform(delete("/api/comments/" + createdComment.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // 5. 삭제 확인 (API로 조회 시 404 반환)
        mockMvc.perform(get("/api/comments/" + createdComment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"))
                .andExpect(jsonPath("$.content").value("수정된 댓글 내용"))
                .andExpect(jsonPath("$.id").value(createdComment.getId().toString()));
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("특정 타겟의 모든 댓글 조회 테스트")
    void testGetCommentsByTarget() throws Exception {
        // 1. 첫 번째 타겟용 댓글 3개 생성
        for (int i = 0; i < 3; i++) {
            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("Target 1 - Comment " + i)
                    .targetId(testTargetId)
                    .build();

            mockMvc.perform(post("/api/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // 2. 두 번째 타겟용 댓글 2개 생성
        for (int i = 0; i < 2; i++) {
            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("Target 2 - Comment " + i)
                    .targetId(testPost2Id)
                    .build();

            mockMvc.perform(post("/api/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // 3. 첫 번째 타겟의 댓글만 조회 및 확인
        mockMvc.perform(get("/api/comments/target/" + testTargetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(3))
                .andExpect(jsonPath("$.comments.length()").value(3))
                .andExpect(jsonPath("$.comments[0].content").value("Target 1 - Comment 0"))
                .andExpect(jsonPath("$.comments[1].content").value("Target 1 - Comment 1"))
                .andExpect(jsonPath("$.comments[2].content").value("Target 1 - Comment 2"));

        // 4. 두 번째 타겟의 댓글만 조회 및 확인
        mockMvc.perform(get("/api/comments/target/" + testPost2Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.comments.length()").value(2))
                .andExpect(jsonPath("$.comments[0].content").value("Target 2 - Comment 0"))
                .andExpect(jsonPath("$.comments[1].content").value("Target 2 - Comment 1"));
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("댓글 내용 유효성 검사 테스트 - 빈 내용")
    void testCommentValidationEmptyContent() throws Exception {
        // 빈 내용으로 댓글 생성 시도
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("")
                .targetId(testTargetId)
                .build();

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").exists()); // 유효성 검사 오류 메시지 존재 확인
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("댓글 내용 유효성 검사 테스트 - 최대 길이 초과")
    void testCommentValidationTooLongContent() throws Exception {
        // 1000자를 초과하는 내용으로 댓글 생성 시도
        StringBuilder tooLongContent = new StringBuilder();
        for (int i = 0; i < 1001; i++) {
            tooLongContent.append("a");
        }

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content(tooLongContent.toString())
                .targetId(testTargetId)
                .build();

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").exists()); // 유효성 검사 오류 메시지 존재 확인
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("대댓글에 대한 대댓글 생성 및 조회 테스트")
    void testNestedReplies() throws Exception {
        // 1. 최상위 댓글 생성
        CreateCommentRequest rootRequest = CreateCommentRequest.builder()
                .content("Root comment")
                .targetId(testTargetId)
                .build();

        MvcResult rootResult = mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rootRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CommentResponse rootComment = objectMapper.readValue(
                rootResult.getResponse().getContentAsString(),
                CommentResponse.class
        );

        // 2. 첫 번째 대댓글 생성
        CreateCommentRequest level1Request = CreateCommentRequest.builder()
                .content("Level 1 reply")
                .targetId(testTargetId)
                .parentId(rootComment.getId())
                .build();

        MvcResult level1Result = mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level1Request)))
                .andExpect(status().isCreated())
                .andReturn();

        CommentResponse level1Comment = objectMapper.readValue(
                level1Result.getResponse().getContentAsString(),
                CommentResponse.class
        );

        // 3. 두 번째 대댓글 생성
        CreateCommentRequest level2Request = CreateCommentRequest.builder()
                .content("Level 2 reply")
                .targetId(testTargetId)
                .parentId(level1Comment.getId())
                .build();

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level2Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.depth").value(2));

        // 4. 대상 ID로 모든 댓글 조회 (계층 구조 확인)
        mockMvc.perform(get("/api/comments/target/" + testTargetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(3))
                .andExpect(jsonPath("$.comments.length()").value(3))
                .andExpect(jsonPath("$.comments[0].content").value("Root comment"))
                .andExpect(jsonPath("$.comments[0].depth").value(0))
                .andExpect(jsonPath("$.comments[0].parentId").doesNotExist())
                .andExpect(jsonPath("$.comments[1].content").value("Level 1 reply"))
                .andExpect(jsonPath("$.comments[1].depth").value(1))
                .andExpect(jsonPath("$.comments[1].parentId").value(rootComment.getId().toString()))
                .andExpect(jsonPath("$.comments[2].content").value("Level 2 reply"))
                .andExpect(jsonPath("$.comments[2].depth").value(2))
                .andExpect(jsonPath("$.comments[2].parentId").value(level1Comment.getId().toString()));
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("대댓글 최대 깊이 제한 테스트")
    void testReplyDepthLimit() throws Exception {
        // 1. 최상위 댓글 생성 (깊이 0)
        CreateCommentRequest level0Request = CreateCommentRequest.builder()
                .content("Level 0 comment")
                .targetId(testTargetId)
                .build();

        MvcResult level0Result = mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level0Request)))
                .andExpect(status().isCreated())
                .andReturn();

        CommentResponse level0Comment = objectMapper.readValue(
                level0Result.getResponse().getContentAsString(),
                CommentResponse.class
        );

        // 2. 1단계 대댓글 생성 (깊이 1)
        CreateCommentRequest level1Request = CreateCommentRequest.builder()
                .content("Level 1 comment")
                .targetId(testTargetId)
                .parentId(level0Comment.getId())
                .build();

        MvcResult level1Result = mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level1Request)))
                .andExpect(status().isCreated())
                .andReturn();

        CommentResponse level1Comment = objectMapper.readValue(
                level1Result.getResponse().getContentAsString(),
                CommentResponse.class
        );

        // 3. 2단계 대댓글 생성 (깊이 2)
        CreateCommentRequest level2Request = CreateCommentRequest.builder()
                .content("Level 2 comment")
                .targetId(testTargetId)
                .parentId(level1Comment.getId())
                .build();

        MvcResult level2Result = mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level2Request)))
                .andExpect(status().isCreated())
                .andReturn();

        CommentResponse level2Comment = objectMapper.readValue(
                level2Result.getResponse().getContentAsString(),
                CommentResponse.class
        );

        // 4. 3단계 대댓글 생성 (깊이 3) - 최대 깊이
        CreateCommentRequest level3Request = CreateCommentRequest.builder()
                .content("Level 3 comment")
                .targetId(testTargetId)
                .parentId(level2Comment.getId())
                .build();

        MvcResult level3Result = mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level3Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.depth").value(3))
                .andReturn();

        CommentResponse level3Comment = objectMapper.readValue(
                level3Result.getResponse().getContentAsString(),
                CommentResponse.class
        );

        // 5. 4단계 대댓글 생성 시도 (깊이 4) - 실패해야 함
        CreateCommentRequest tooDeepRequest = CreateCommentRequest.builder()
                .content("Too deep reply")
                .targetId(testTargetId)
                .parentId(level3Comment.getId())
                .build();

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooDeepRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("존재하지 않는 댓글 조회 시 404 응답 테스트")
    void testGetNonExistentComment() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/comments/" + nonExistentId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "USER")
    @DisplayName("존재하지 않는 부모 댓글에 대댓글 작성 시 400 응답 테스트")
    void testCreateReplyToNonExistentParent() throws Exception {
        UUID nonExistentParentId = UUID.randomUUID();

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("Reply to non-existent parent")
                .targetId(testTargetId)
                .parentId(nonExistentParentId)
                .build();

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174001", roles = "USER") // 유효한 UUID 형식 사용
    @DisplayName("다른 사용자의 댓글 삭제 시도 시 403 응답 테스트")
    void testDeleteOtherUsersComment() throws Exception {
        // 1. 테스트용 댓글을 생성할 사용자로 로그인 (인증 대체)
        String testUserUuid = "123e4567-e89b-12d3-a456-426614174000"; // 최초 생성자 UUID

        // 2. 댓글 생성 (원래 소유자)
        UUID commentId;

        // CommentJpaEntity 직접 생성 및 저장
        CommentJpaEntity commentEntity = new CommentJpaEntity(
                UUID.randomUUID(),
                "Comment by original user",
                testTargetId,
                UUID.fromString(testUserUuid), // 원래 소유자의 UUID
                null,
                0,
                CommentStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        commentJpaRepository.save(commentEntity);
        commentId = commentEntity.getId();

        // 3. 다른 사용자로 로그인하여 댓글 삭제 시도
        // 현재 테스트는 이미 다른 사용자로 설정되어 있음 (WithMockUser 어노테이션)
        mockMvc.perform(delete("/api/comments/" + commentId)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}