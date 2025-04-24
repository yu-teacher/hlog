package com.hunnit_beasts.hlog.post.domain.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
    Tag save(Tag tag);
    Optional<Tag> findByName(String name);
    List<Tag> findAll();
    List<Tag> findPopularTags(int limit);
    void delete(Tag tag);
    boolean existsByName(String name);
}