package com.hunnit_beasts.hlog.post.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig implements WebMvcConfigurer {

    @Value("${upload.directory.window:C:\\Temp\\hlog\\}")
    private String windowsUploadDir;

    @Value("${upload.directory.linux:/tmp/hlog/}")
    private String linuxUploadDir;

    @Value("${upload.directory.mac:/temp/hlog/}")
    private String macUploadDir;

    @Value("${app.image.url.prefix:/uploads/}")
    private String imageUrlPrefix;

    private String getUploadDir() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return windowsUploadDir;
        } else if (osName.contains("mac")) {
            return macUploadDir;
        } else {
            return linuxUploadDir;
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = getUploadDir();
        Path uploadPath = Paths.get(uploadDir);
        String absolutePath = uploadPath.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}