package com.hunnit_beasts.hlog.post.application.service;

import com.hunnit_beasts.hlog.post.application.dto.TagDto;
import com.hunnit_beasts.hlog.post.application.mapper.TagDtoMapper;
import com.hunnit_beasts.hlog.post.domain.model.entity.Tag;
import com.hunnit_beasts.hlog.post.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagDtoMapper tagDtoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagDtoMapper.toDtoList(tagRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getPopularTags(int limit) {
        return tagDtoMapper.toDtoList(tagRepository.findPopularTags(limit));
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto getTagByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with name: " + name));
        return tagDtoMapper.toDto(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tagExists(String name) {
        return tagRepository.existsByName(name);
    }
}