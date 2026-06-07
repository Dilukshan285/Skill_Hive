package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Skill entity for MongoDB
@Data
@Document(collection = "skills")
public class Skill {
    @Id
    private String id; // Unique identifier
    private String title; // Skill title
    private String description; // Skill description
    private String category; // Skill category (e.g., Programming)
    private String userId; // User who offers the skill
    private String status; // Skill status (e.g., Available, Offered)
}