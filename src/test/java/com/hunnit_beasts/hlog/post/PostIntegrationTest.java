package com.hunnit_beasts.hlog.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.post.api.dto.CreatePostRequest;
import com.hunnit_beasts.hlog.post.api.dto.PostResponse;
import com.hunnit_beasts.hlog.post.api.dto.UpdatePostRequest;
import com.hunnit_beasts.hlog.post.application.service.PostService;
import com.hunnit_beasts.hlog.post.domain.repository.PostRepository;
import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("포스트 통합 테스트")
@Transactional
public class PostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    private UUID authorId;
    private Set<String> tagNames;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("test@gmail.com","testUser","testPass");
        authorId = UUID.fromString(userService.registerUser(createUserDto).getId());
        tagNames = new HashSet<>();
        tagNames.add("java");
        tagNames.add("spring");
    }

    @Test
    @DisplayName("포스트 생성, 조회, 수정, 발행, 삭제가 정상적으로 동작해야 한다")
    void createAndGetPost() throws Exception {
        // Create a post
        CreatePostRequest createRequest = CreatePostRequest.builder()
                .title("Integration Test Title")
                .content("Integration Test Content")
                .contentFormat("MARKDOWN")
                .authorId(authorId)
                .tagNames(tagNames)
                .publish(false)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/posts")
                        .with(user(authorId.toString()).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(createRequest.getTitle()))
                .andExpect(jsonPath("$.content").value(createRequest.getContent()))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.tagNames").isArray())
                .andReturn();

        // Extract post ID from response
        PostResponse postResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                PostResponse.class
        );
        UUID postId = postResponse.getId();

        // Get the created post
        mockMvc.perform(get("/api/v1/posts/{postId}", postId)
                        .with(user(authorId.toString()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.title").value(createRequest.getTitle()))
                .andExpect(jsonPath("$.content").value(createRequest.getContent()));

        // Update the post
        UpdatePostRequest updateRequest = UpdatePostRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .contentFormat("MARKDOWN")
                .tagNames(tagNames)
                .build();

        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .with(user(authorId.toString()).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateRequest.getTitle()))
                .andExpect(jsonPath("$.content").value(updateRequest.getContent()));

        // Publish the post
        mockMvc.perform(put("/api/v1/posts/{postId}/publish", postId)
                        .with(user(authorId.toString()).roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());

        // Delete the post
        mockMvc.perform(delete("/api/v1/posts/{postId}", postId)
                        .with(user(authorId.toString()).roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // Verify post was deleted
        mockMvc.perform(get("/api/v1/posts/{postId}", postId)
                        .with(user(authorId.toString()).roles("USER")))
                .andExpect(status().isForbidden());
    }
}