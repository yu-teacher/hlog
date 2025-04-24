package com.hunnit_beasts.hlog.user.infrastructure.persistence.entity;

import com.hunnit_beasts.hlog.user.domain.model.vo.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Builder
public class RoleJpaEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private RoleName name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

}
