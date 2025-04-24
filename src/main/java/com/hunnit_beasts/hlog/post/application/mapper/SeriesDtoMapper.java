package com.hunnit_beasts.hlog.post.application.mapper;

import com.hunnit_beasts.hlog.post.application.dto.CreateSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.PostInSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.SeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdateSeriesDto;
import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.entity.SeriesPost;
import com.hunnit_beasts.hlog.post.domain.model.vo.Description;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesName;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SeriesDtoMapper {

    public Series toEntity(CreateSeriesDto dto) {
        return Series.create(
                SeriesName.of(dto.getName()),
                dto.getDescription() != null ? Description.of(dto.getDescription()) : Description.empty(),
                dto.getAuthorId()
        );
    }

    public void updateEntityFromDto(Series series, UpdateSeriesDto dto) {
        if (dto.getName() != null) {
            series.updateName(SeriesName.of(dto.getName()));
        }

        if (dto.getDescription() != null) {
            series.updateDescription(Description.of(dto.getDescription()));
        }
    }

    public SeriesDto toDto(Series series, List<SeriesPost> seriesPosts, Map<UUID, Post> postsMap) {
        List<PostInSeriesDto> postDtos = seriesPosts.stream()
                .sorted(Comparator.comparing(SeriesPost::getOrder))
                .map(sp -> {
                    Post post = postsMap.get(sp.getPostId().getValue());
                    if (post != null) {
                        return PostInSeriesDto.builder()
                                .id(post.getId().getValue())
                                .title(post.getTitle().getValue())
                                .order(sp.getOrder())
                                .build();
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        return SeriesDto.builder()
                .id(series.getId().getValue())
                .name(series.getName().getValue())
                .description(series.getDescription() != null ? series.getDescription().getValue() : "")
                .authorId(series.getAuthorId())
                .status(series.getStatus().toString())
                .createdAt(series.getCreatedAt())
                .updatedAt(series.getUpdatedAt())
                .posts(postDtos)
                .build();
    }
}