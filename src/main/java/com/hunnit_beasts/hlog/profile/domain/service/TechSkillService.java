package com.hunnit_beasts.hlog.profile.domain.service;

import java.util.List;
import java.util.Set;

public interface TechSkillService {
    Set<String> normalizeSkills(Set<String> skills);
    boolean isValidSkill(String skill);
    List<String> suggestSkills(String prefix);
}