package com.hunnit_beasts.hlog.post.infrastructure.service;

import com.hunnit_beasts.hlog.post.domain.service.MarkdownService;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkdownServiceImpl implements MarkdownService {
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    @Override
    public String renderToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return htmlRenderer.render(document);
    }

    @Override
    public String getPlainText(String markdown) {
        Node document = parser.parse(markdown);
        return TextContentRenderer.builder().build().render(document);
    }

    @Override
    public String sanitize(String html) {
        return Jsoup.clean(html, Safelist.relaxed());
    }
}