package com.hunnit_beasts.hlog.post.infrastructure.config;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostConfig {

    @Bean
    public Parser markdownParser() {
        return Parser.builder().build();
    }

    @Bean
    public HtmlRenderer htmlRenderer() {
        return HtmlRenderer.builder().build();
    }
}
