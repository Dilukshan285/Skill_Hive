package com.example.demo.controller;

import com.example.demo.model.Skill;
import com.example.demo.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// REST controller for Skill APIs
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    // Debug constructor to confirm controller registration
    public SkillController() {
        System.out.println("SkillController initialized");
    }

    // Test endpoint to verify controller
    @GetMapping("/test")
    public String test() {
        return "Skill API is working!";
    }

    // Get all skills
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    // Get skill by ID
    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable String id) {
        Optional<Skill> skill = skillService.getSkillById(id);
        return skill.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get skills by category
    @GetMapping("/category/{category}")
    public List<Skill> getSkillsByCategory(@PathVariable String category) {
        return skillService.getSkillsByCategory(category);
    }

    // Get skills by user ID
    @GetMapping("/user/{userId}")
    public List<Skill> getSkillsByUserId(@PathVariable String userId) {
        return skillService.getSkillsByUserId(userId);
    }

    // Create a new skill
    @PostMapping
    public Skill createSkill(@RequestBody Skill skill) {
        return skillService.createSkill(skill);
    }

    // Update an existing skill
    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable String id, @RequestBody Skill skillDetails) {
        Skill updatedSkill = skillService.updateSkill(id, skillDetails);
        if (updatedSkill != null) {
            return ResponseEntity.ok(updatedSkill);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete a skill
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable String id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok().build();
    }
}