package com.hunnit_beasts.hlog.post.api.controller;

import com.hunnit_beasts.hlog.post.infrastructure.service.RssFeedGenerator;
import com.hunnit_beasts.hlog.user.api.facade.UserServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class RssFeedController {

    private final RssFeedGenerator rssFeedGenerator;
    private final UserServiceFacade userServiceFacade;

    @GetMapping(value = "/rss/{authorId}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getRssFeed(@PathVariable UUID authorId) {
        // 사용자 정보 검증
        if (!userServiceFacade.existsById(authorId.toString())) {
            throw new IllegalArgumentException("Author not found with ID: " + authorId);
        }

        String authorName = userServiceFacade.getUsernameById(authorId.toString());
        String blogTitle = authorName + "'s Blog";
        String blogDescription = "Latest posts from " + authorName;
        String blogUrl = "http://localhost:8080/blog/" + authorId;  // 실제 배포 환경에 맞게 조정 필요

        String feedXml = rssFeedGenerator.generateFeed(
                authorId,
                authorName,
                blogTitle,
                blogDescription,
                blogUrl
        );

        return ResponseEntity.ok(feedXml);
    }
}