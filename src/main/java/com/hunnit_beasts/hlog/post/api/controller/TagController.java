package com.hunnit_beasts.hlog.post.api.controller;

import com.hunnit_beasts.hlog.post.api.dto.TagResponse;
import com.hunnit_beasts.hlog.post.api.mapper.TagApiMapper;
import com.hunnit_beasts.hlog.post.application.dto.TagDto;
import com.hunnit_beasts.hlog.post.application.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagApiMapper tagApiMapper;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagDto> tags = tagService.getAllTags();
        List<TagResponse> response = tags.stream()
                .map(tagApiMapper::toTagResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TagResponse>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {

        List<TagDto> tags = tagService.getPopularTags(limit);
        List<TagResponse> response = tags.stream()
                .map(tagApiMapper::toTagResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<TagResponse> getTagByName(@PathVariable String name) {
        TagDto tag = tagService.getTagByName(name);
        return ResponseEntity.ok(tagApiMapper.toTagResponse(tag));
    }
}