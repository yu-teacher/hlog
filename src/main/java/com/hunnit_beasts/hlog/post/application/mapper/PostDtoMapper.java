package com.hunnit_beasts.hlog.post.application.mapper;

import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdatePostDto;
import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.Content;
import com.hunnit_beasts.hlog.post.domain.model.vo.ContentFormat;
import com.hunnit_beasts.hlog.post.domain.model.vo.Title;
import com.hunnit_beasts.hlog.post.domain.service.MarkdownService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class PostDtoMapper {

    private final MarkdownService markdownService;

    public PostDtoMapper(MarkdownService markdownService) {
        this.markdownService = markdownService;
    }

    public Post toEntity(CreatePostDto dto) {
        Content content;
        if ("MARKDOWN".equalsIgnoreCase(dto.getContentFormat())) {
            content = Content.ofMarkdown(dto.getContent());
        } else {
            content = Content.ofHtml(dto.getContent());
        }

        Post post = Post.create(
                Title.of(dto.getTitle()),
                content,
                dto.getAuthorId(),
                dto.getTagNames()
        );

        if (dto.isPublish()) {
            post.publish();
        }

        return post;
    }

    public void updateEntityFromDto(Post post, UpdatePostDto dto) {
        if (dto.getTitle() != null) {
            post.updateTitle(Title.of(dto.getTitle()));
        }

        if (dto.getContent() != null) {
            Content content;
            if ("MARKDOWN".equalsIgnoreCase(dto.getContentFormat())) {
                content = Content.ofMarkdown(dto.getContent());
            } else {
                content = Content.ofHtml(dto.getContent());
            }
            post.updateContent(content);
        }

        if (dto.getTagNames() != null) {
            Set<String> existingTags = new HashSet<>(post.getTagNames());
            for (String tag : existingTags) {
                post.removeTag(tag);
            }

            dto.getTagNames().forEach(post::addTag);
        }
    }

    public PostDto toDto(Post post) {
        String htmlContent = null;
        if (post.getContent().getFormat() == ContentFormat.MARKDOWN) {
            htmlContent = markdownService.renderToHtml(post.getContent().getValue());
        } else {
            htmlContent = post.getContent().getValue();
        }

        return PostDto.builder()
                .id(post.getId().getValue())
                .title(post.getTitle().getValue())
                .content(post.getContent().getValue())
                .contentFormat(post.getContent().getFormat().toString())
                .status(post.getStatus().toString())
                .authorId(post.getAuthorId())
                .tagNames(post.getTagNames())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .htmlContent(htmlContent)
                .build();
    }
}