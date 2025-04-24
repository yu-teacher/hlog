package com.hunnit_beasts.hlog.post.domain.service;

public interface MarkdownService {
    String renderToHtml(String markdown);
    String getPlainText(String markdown);
    String sanitize(String html);
}