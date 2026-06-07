package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "post_interactions")
public class PostInteraction {
    @Id
    private String id;
    private String recipientId; // The recipient (post creator)
    private String postId;
    private String type; // e.g., "LIKE", "COMMENT"
    private String message;
    private String commentId; // Add commentId for COMMENT type interactions
    private boolean read;
    private LocalDateTime createdAt;
}