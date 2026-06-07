package com.example.demo.repository;

import com.example.demo.model.Skill;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

// MongoDB repository for Skill
public interface SkillRepository extends MongoRepository<Skill, String> {
    // Find skills by category
    List<Skill> findByCategory(String category);
    // Find skills by user ID
    List<Skill> findByUserId(String userId);
}