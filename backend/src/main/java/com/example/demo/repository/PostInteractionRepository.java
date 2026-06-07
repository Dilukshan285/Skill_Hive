package com.example.demo.repository;

import com.example.demo.model.PostInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostInteractionRepository extends MongoRepository<PostInteraction, String> {
    List<PostInteraction> findByRecipientId(String recipientId);
    List<PostInteraction> findByRecipientIdAndType(String recipientId, String type);
    List<PostInteraction> findByPostId(String postId);
    List<PostInteraction> findByPostIdAndCommentId(String postId, String commentId);
}