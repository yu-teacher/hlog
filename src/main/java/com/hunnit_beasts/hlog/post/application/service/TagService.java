package com.hunnit_beasts.hlog.post.application.service;

import com.hunnit_beasts.hlog.post.application.dto.TagDto;

import java.util.List;

public interface TagService {
    List<TagDto> getAllTags();
    List<TagDto> getPopularTags(int limit);
    TagDto getTagByName(String name);
    boolean tagExists(String name);
}