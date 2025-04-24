package com.hunnit_beasts.hlog.post.api.mapper;

import com.hunnit_beasts.hlog.post.api.dto.CreatePostRequest;
import com.hunnit_beasts.hlog.post.api.dto.PostResponse;
import com.hunnit_beasts.hlog.post.api.dto.PostSummaryResponse;
import com.hunnit_beasts.hlog.post.api.dto.UpdatePostRequest;
import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdatePostDto;
import com.hunnit_beasts.hlog.post.domain.service.MarkdownService;
import com.hunnit_beasts.hlog.user.api.facade.UserServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostApiMapper {

    private final UserServiceFacade userServiceFacade;
    private final MarkdownService markdownService;

    public CreatePostDto toCreatePostDto(CreatePostRequest request) {
        return CreatePostDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .contentFormat(request.getContentFormat())
                .authorId(request.getAuthorId())
                .tagNames(request.getTagNames())
                .publish(request.isPublish())
                .build();
    }

    public UpdatePostDto toUpdatePostDto(UpdatePostRequest request) {
        return UpdatePostDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .contentFormat(request.getContentFormat())
                .tagNames(request.getTagNames())
                .build();
    }

    public PostResponse toPostResponse(PostDto dto) {
        String authorName = dto.getAuthorId() != null
                ? userServiceFacade.getUsernameById(dto.getAuthorId().toString())
                : "Unknown";

        return PostResponse.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .contentFormat(dto.getContentFormat())
                .status(dto.getStatus())
                .authorId(dto.getAuthorId())
                .authorName(authorName)
                .tagNames(dto.getTagNames())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .publishedAt(dto.getPublishedAt())
                .htmlContent(dto.getHtmlContent())
                .build();
    }

    public PostSummaryResponse toPostSummaryResponse(PostDto dto) {
        String authorName = dto.getAuthorId() != null
                ? userServiceFacade.getUsernameById(dto.getAuthorId().toString())
                : "Unknown";

        // 요약글 생성 - 본문의 일부분만 추출
        String summary = "";
        if (dto.getContent() != null) {
            if ("MARKDOWN".equals(dto.getContentFormat())) {
                // 마크다운에서 일반 텍스트 추출
                String plainText = markdownService.getPlainText(dto.getContent());
                summary = truncateSummary(plainText);
            } else {
                summary = truncateSummary(dto.getContent());
            }
        }

        return PostSummaryResponse.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .summary(summary)
                .status(dto.getStatus())
                .authorId(dto.getAuthorId())
                .authorName(authorName)
                .tagNames(dto.getTagNames())
                .createdAt(dto.getCreatedAt())
                .publishedAt(dto.getPublishedAt())
                .build();
    }

    private String truncateSummary(String text) {
        final int MAX_SUMMARY_LENGTH = 200;
        if (text.length() <= MAX_SUMMARY_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_SUMMARY_LENGTH) + "...";
    }
}