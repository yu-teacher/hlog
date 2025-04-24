package com.hunnit_beasts.hlog.post.infrastructure.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;
import com.hunnit_beasts.hlog.post.domain.repository.PostRepository;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.PostJpaEntity;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper.PostEntityMapper;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaPostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;
    private final PostEntityMapper postEntityMapper;

    @Override
    public Post save(Post post) {
        PostJpaEntity entity = postEntityMapper.toJpaEntity(post);
        PostJpaEntity savedEntity = postJpaRepository.save(entity);
        return postEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Post> findById(PostId id) {
        return postJpaRepository.findById(id.getValue())
                .map(postEntityMapper::toDomainEntity);
    }

    @Override
    public List<Post> findByAuthorId(UUID authorId) {
        return postJpaRepository.findByAuthorId(authorId).stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByStatus(PostStatus status) {
        return postJpaRepository.findByStatus(status.toString()).stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByStatusAndAuthorId(PostStatus status, UUID authorId) {
        return postJpaRepository.findByStatusAndAuthorId(status.toString(), authorId).stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByTagName(String tagName) {
        return postJpaRepository.findByTagName(tagName).stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findAll() {
        return postJpaRepository.findAll().stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Post post) {
        postJpaRepository.deleteById(post.getId().getValue());
    }

    @Override
    public long countByAuthorId(UUID authorId) {
        return postJpaRepository.countByAuthorId(authorId);
    }

    @Override
    public boolean existsById(PostId id) {
        return postJpaRepository.existsById(id.getValue());
    }

    @Override
    public List<Post> findActiveByAuthorId(UUID authorId) {
        return postJpaRepository.findByAuthorIdAndStatusNot(authorId, PostStatus.DELETED.toString())
                .stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findActiveByTagName(String tagName) {
        return postJpaRepository.findByTagNameAndStatusNot(tagName, PostStatus.DELETED.toString())
                .stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findActiveAll() {
        return postJpaRepository.findByStatusNot(PostStatus.DELETED.toString())
                .stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findPublishedPosts() {
        return postJpaRepository.findByStatus(PostStatus.PUBLISHED.toString())
                .stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findPublishedByAuthorId(UUID authorId) {
        return postJpaRepository.findByStatusAndAuthorId(PostStatus.PUBLISHED.toString(), authorId)
                .stream()
                .map(postEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> findActiveById(PostId id) {
        return postJpaRepository.findByIdAndStatusNot(id.getValue(), PostStatus.DELETED.toString())
                .map(postEntityMapper::toDomainEntity);
    }
}