package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String title;
    private String text;
    private String creatorId;
    private String creatorName;
    private String category;
    private List<String> tags = new ArrayList<>();
    private boolean published;
    private List<String> likes = new ArrayList<>();
    private List<Media> media = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class Media {
        private String path;
        private MediaType type;
    }

    public enum MediaType {
        IMAGE, VIDEO
    }

    @Data
    public static class Comment {
        private String id;
        private String text;
        private String creatorId;
        private String creatorName;
        private LocalDateTime createdAt;
    }
}