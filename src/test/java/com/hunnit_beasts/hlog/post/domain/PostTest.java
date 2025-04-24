package com.hunnit_beasts.hlog.post.domain;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.Content;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;
import com.hunnit_beasts.hlog.post.domain.model.vo.Title;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Post 엔티티 테스트")
class PostTest {

    @Test
    @DisplayName("포스트 생성 시 유효한 포스트가 반환되어야 한다")
    void createPost_ShouldReturnValidPost() {
        // Given
        String title = "Test Title";
        String content = "Test Content";
        UUID authorId = UUID.randomUUID();
        Set<String> tags = new HashSet<>();
        tags.add("java");
        tags.add("spring");

        // When
        Post post = Post.create(
                Title.of(title),
                Content.ofMarkdown(content),
                authorId,
                tags
        );

        // Then
        assertNotNull(post);
        assertNotNull(post.getId());
        assertEquals(title, post.getTitle().getValue());
        assertEquals(content, post.getContent().getValue());
        assertEquals(authorId, post.getAuthorId());
        assertEquals(PostStatus.DRAFT, post.getStatus());
        assertEquals(2, post.getTagNames().size());
        assertTrue(post.getTagNames().contains("java"));
        assertTrue(post.getTagNames().contains("spring"));
        assertNotNull(post.getCreatedAt());
        assertEquals(post.getCreatedAt(), post.getUpdatedAt());
        assertNull(post.getPublishedAt());
    }

    @Test
    @DisplayName("포스트 발행 시 상태가 변경되고 발행일이 설정되어야 한다")
    void publishPost_ShouldChangeStatusAndSetPublishedAt() {
        // Given
        Post post = Post.create(
                Title.of("Test Title"),
                Content.ofMarkdown("Test Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        post.publish();

        // Then
        assertEquals(PostStatus.PUBLISHED, post.getStatus());
        assertNotNull(post.getPublishedAt());
        assertNotEquals(post.getCreatedAt(), post.getUpdatedAt());
    }

    @Test
    @DisplayName("포스트 발행 취소 시 상태가 초안으로 변경되어야 한다")
    void unpublishPost_ShouldChangeStatusBackToDraft() {
        // Given
        Post post = Post.create(
                Title.of("Test Title"),
                Content.ofMarkdown("Test Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        post.publish();

        LocalDateTime publishedAt = post.getPublishedAt();

        // When
        post.unpublish();

        // Then
        assertEquals(PostStatus.DRAFT, post.getStatus());
        assertEquals(publishedAt, post.getPublishedAt()); // PublishedAt should not change
        assertNotEquals(post.getCreatedAt(), post.getUpdatedAt());
    }

    @Test
    @DisplayName("제목 업데이트 시 제목이 변경되어야 한다")
    void updateTitle_ShouldChangeTitle() {
        // Given
        Post post = Post.create(
                Title.of("Old Title"),
                Content.ofMarkdown("Test Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        post.updateTitle(Title.of("New Title"));

        // Then
        assertEquals("New Title", post.getTitle().getValue());
        assertNotEquals(post.getCreatedAt(), post.getUpdatedAt());
    }

    @Test
    @DisplayName("내용 업데이트 시 내용이 변경되어야 한다")
    void updateContent_ShouldChangeContent() {
        // Given
        Post post = Post.create(
                Title.of("Test Title"),
                Content.ofMarkdown("Old Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        post.updateContent(Content.ofMarkdown("New Content"));

        // Then
        assertEquals("New Content", post.getContent().getValue());
        assertNotEquals(post.getCreatedAt(), post.getUpdatedAt());
    }

    @Test
    @DisplayName("태그 추가 및 제거 시 태그 목록이 수정되어야 한다")
    void addAndRemoveTags_ShouldModifyTagSet() {
        // Given
        Post post = Post.create(
                Title.of("Test Title"),
                Content.ofMarkdown("Test Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        // When
        post.addTag("java");
        post.addTag("spring");

        // Then
        assertEquals(2, post.getTagNames().size());
        assertTrue(post.getTagNames().contains("java"));
        assertTrue(post.getTagNames().contains("spring"));

        // When
        post.removeTag("java");

        // Then
        assertEquals(1, post.getTagNames().size());
        assertFalse(post.getTagNames().contains("java"));
        assertTrue(post.getTagNames().contains("spring"));
    }
}
