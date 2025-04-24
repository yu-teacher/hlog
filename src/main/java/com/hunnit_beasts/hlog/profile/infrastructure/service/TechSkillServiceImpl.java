package com.hunnit_beasts.hlog.profile.infrastructure.service;

import com.hunnit_beasts.hlog.profile.domain.service.TechSkillService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TechSkillServiceImpl implements TechSkillService {

    // 이 리스트는 예시로, 실제 구현에서는 데이터베이스나 외부 API에서 가져올 수 있습니다.
    private static final List<String> COMMON_SKILLS = Arrays.asList(
            "java", "spring", "spring boot", "jpa", "hibernate",
            "javascript", "typescript", "react", "angular", "vue",
            "node.js", "express", "python", "django", "flask",
            "c#", ".net", "asp.net", "go", "rust",
            "docker", "kubernetes", "aws", "azure", "gcp",
            "git", "jenkins", "ci/cd", "agile", "scrum"
    );

    @Override
    public Set<String> normalizeSkills(Set<String> skills) {
        if (skills == null) {
            return new HashSet<>();
        }

        return skills.stream()
                .filter(this::isValidSkill)
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValidSkill(String skill) {
        return skill != null && !skill.trim().isEmpty() && skill.length() <= 50;
    }

    @Override
    public List<String> suggestSkills(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>(COMMON_SKILLS);
        }

        String normalizedPrefix = prefix.toLowerCase().trim();
        return COMMON_SKILLS.stream()
                .filter(skill -> skill.startsWith(normalizedPrefix))
                .limit(10)
                .collect(Collectors.toList());
    }
}