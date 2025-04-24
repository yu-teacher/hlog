package com.hunnit_beasts.hlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
		"com.hunnit_beasts.hlog.user.infrastructure.persistence.entity",
		"com.hunnit_beasts.hlog.post.infrastructure.persistence.entity",
		"com.hunnit_beasts.hlog.comment.infrastructure.persistence.entity"
})
@EnableJpaRepositories(basePackages = {
		"com.hunnit_beasts.hlog.user.infrastructure.persistence.repository",
		"com.hunnit_beasts.hlog.post.infrastructure.persistence.repository",
		"com.hunnit_beasts.hlog.comment.infrastructure.persistence.repository"
})
public class HlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(HlogApplication.class, args);
	}

}
