package com.hunnit_beasts.hlog.post.infrastructure.persistence.repository;

import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.PostJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostJpaRepository extends JpaRepository<PostJpaEntity, UUID> {
    List<PostJpaEntity> findByAuthorId(UUID authorId);
    List<PostJpaEntity> findByStatus(String status);
    List<PostJpaEntity> findByStatusAndAuthorId(String status, UUID authorId);

    // 삭제되지 않은 포스트 조회를 위한 메서드 추가
    List<PostJpaEntity> findByAuthorIdAndStatusNot(UUID authorId, String status);
    List<PostJpaEntity> findByStatusNot(String status);
    Optional<PostJpaEntity> findByIdAndStatusNot(UUID id, String status);

    @Query("SELECT p FROM PostJpaEntity p JOIN p.tagNames t WHERE t = :tagName")
    List<PostJpaEntity> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT p FROM PostJpaEntity p JOIN p.tagNames t WHERE t = :tagName AND p.status != :status")
    List<PostJpaEntity> findByTagNameAndStatusNot(@Param("tagName") String tagName, @Param("status") String status);

    long countByAuthorId(UUID authorId);
}