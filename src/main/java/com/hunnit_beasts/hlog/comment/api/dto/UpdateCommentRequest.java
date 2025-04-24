package com.hunnit_beasts.hlog.comment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentRequest {
    @NotNull(message = "Comment ID is required")
    private UUID id;

    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 1000, message = "Comment content must not exceed 1000 characters")
    private String content;
}