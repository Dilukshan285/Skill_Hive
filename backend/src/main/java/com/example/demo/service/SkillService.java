package com.example.demo.service;

import com.example.demo.model.Skill;
import com.example.demo.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SkillService {
    
    @Autowired
    private SkillRepository skillRepository;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Optional<Skill> getSkillById(String id) {
        return skillRepository.findById(id);
    }

    public List<Skill> getSkillsByCategory(String category) {
        return skillRepository.findByCategory(category);
    }

    public List<Skill> getSkillsByUserId(String userId) {
        return skillRepository.findByUserId(userId);
    }

    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public Skill updateSkill(String id, Skill skillDetails) {
        Optional<Skill> skillOptional = skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            Skill skill = skillOptional.get();
            skill.setTitle(skillDetails.getTitle());
            skill.setDescription(skillDetails.getDescription());
            skill.setCategory(skillDetails.getCategory());
            skill.setStatus(skillDetails.getStatus());
            return skillRepository.save(skill);
        }
        return null;
    }

    public void deleteSkill(String id) {
        skillRepository.deleteById(id);
    }
}