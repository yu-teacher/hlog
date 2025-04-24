package com.hunnit_beasts.hlog.post.infrastructure.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.Tag;
import com.hunnit_beasts.hlog.post.domain.repository.TagRepository;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.TagJpaEntity;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper.TagEntityMapper;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.repository.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaTagRepositoryImpl implements TagRepository {
    private final TagJpaRepository tagJpaRepository;
    private final TagEntityMapper tagEntityMapper;

    @Override
    public Tag save(Tag tag) {
        TagJpaEntity entity = tagEntityMapper.toJpaEntity(tag);
        TagJpaEntity savedEntity = tagJpaRepository.save(entity);
        return tagEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagJpaRepository.findById(name)
                .map(tagEntityMapper::toDomainEntity);
    }

    @Override
    public List<Tag> findAll() {
        return tagJpaRepository.findAll().stream()
                .map(tagEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findPopularTags(int limit) {
        return tagJpaRepository.findTopByUsageCount(limit).stream()
                .map(tagEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Tag tag) {
        tagJpaRepository.deleteById(tag.getName());
    }

    @Override
    public boolean existsByName(String name) {
        return tagJpaRepository.existsById(name);
    }
}