package com.hunnit_beasts.hlog.post.infrastructure.service;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;
import com.hunnit_beasts.hlog.post.domain.repository.PostRepository;
import com.hunnit_beasts.hlog.post.domain.service.MarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RssFeedGenerator {

    private final PostRepository postRepository;
    private final MarkdownService markdownService;

    public String generateFeed(UUID authorId, String authorName, String blogTitle, String blogDescription, String blogUrl) {
        List<Post> posts = postRepository.findByStatusAndAuthorId(PostStatus.PUBLISHED, authorId);

        StringBuilder rssBuilder = new StringBuilder();
        rssBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        rssBuilder.append("<rss version=\"2.0\">\n");
        rssBuilder.append("  <channel>\n");
        rssBuilder.append("    <title>").append(escapeXml(blogTitle)).append("</title>\n");
        rssBuilder.append("    <link>").append(escapeXml(blogUrl)).append("</link>\n");
        rssBuilder.append("    <description>").append(escapeXml(blogDescription)).append("</description>\n");

        for (Post post : posts) {
            String content = post.getContent().getValue();
            String htmlContent = post.getContent().getFormat().toString().equals("MARKDOWN")
                    ? markdownService.renderToHtml(content)
                    : content;

            Date pubDate = Date.from(post.getPublishedAt().atZone(ZoneId.systemDefault()).toInstant());

            rssBuilder.append("    <item>\n");
            rssBuilder.append("      <title>").append(escapeXml(post.getTitle().getValue())).append("</title>\n");
            rssBuilder.append("      <link>").append(escapeXml(blogUrl + "/posts/" + post.getId().getValue())).append("</link>\n");
            rssBuilder.append("      <description>").append(escapeXml(htmlContent)).append("</description>\n");
            rssBuilder.append("      <pubDate>").append(pubDate).append("</pubDate>\n");
            rssBuilder.append("      <guid>").append(escapeXml(post.getId().getValue().toString())).append("</guid>\n");
            rssBuilder.append("      <author>").append(escapeXml(authorName)).append("</author>\n");

            // 태그 추가
            for (String tag : post.getTagNames()) {
                rssBuilder.append("      <category>").append(escapeXml(tag)).append("</category>\n");
            }

            rssBuilder.append("    </item>\n");
        }

        rssBuilder.append("  </channel>\n");
        rssBuilder.append("</rss>");

        return rssBuilder.toString();
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}