package com.hunnit_beasts.hlog.post.domain.service;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.entity.Tag;
import com.hunnit_beasts.hlog.post.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagManagementService {
    private final TagRepository tagRepository;

    public Set<String> processPostTags(Post post, Set<String> oldTagNames) {
        Set<String> currentTagNames = new HashSet<>(post.getTagNames());

        // 제거된 태그는 사용 횟수 감소
        oldTagNames.stream()
                .filter(tagName -> !currentTagNames.contains(tagName))
                .forEach(tagName -> {
                    tagRepository.findByName(tagName).ifPresent(tag -> {
                        tag.decrementUsage();
                        if (tag.getUsageCount() == 0) {
                            tagRepository.delete(tag);
                        } else {
                            tagRepository.save(tag);
                        }
                    });
                });

        // 새로 추가된 태그는 사용 횟수 증가 또는 새로 생성
        currentTagNames.stream()
                .filter(tagName -> !oldTagNames.contains(tagName))
                .forEach(tagName -> {
                    Tag tag = tagRepository.findByName(tagName)
                            .orElseGet(() -> Tag.create(tagName));
                    tag.incrementUsage();
                    tagRepository.save(tag);
                });

        return currentTagNames;
    }

    public List<Tag> getPopularTags(int limit) {
        return tagRepository.findPopularTags(limit);
    }
}