package com.example.demo.controller;

import com.example.demo.model.PostInteraction;
import com.example.demo.model.User;
import com.example.demo.repository.PostInteractionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post-interactions")
@RequiredArgsConstructor
public class PostInteractionController {

    private static final Logger logger = LoggerFactory.getLogger(PostInteractionController.class);

    private final PostInteractionRepository postInteractionRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getPostInteractions(@RequestParam(required = false) String recipientId) {
        logger.info("Fetching post interactions for recipient: {}", recipientId);
        try {
            String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Authenticated email: {}", authenticatedEmail);
            if (authenticatedEmail == null || authenticatedEmail.isEmpty()) {
                logger.warn("No authenticated user found for getPostInteractions request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
            logger.info("Authenticated user ID: {}", authenticatedUser.getId());

            if (recipientId == null || recipientId.isEmpty()) {
                recipientId = authenticatedUser.getId();
                logger.info("No recipientId provided, using authenticated user's ID: {}", recipientId);
            }

            if (!recipientId.equals(authenticatedUser.getId())) {
                logger.warn("User {} attempted to access interactions for recipient {}", authenticatedUser.getId(), recipientId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot access interactions for other users");
            }

            List<PostInteraction> interactions = postInteractionRepository.findByRecipientId(recipientId);
            interactions = interactions.stream()
                    .filter(interaction -> {
                        if ("COMMENT".equals(interaction.getType()) && (interaction.getCommentId() == null || interaction.getCommentId().isEmpty())) {
                            logger.warn("Found invalid COMMENT interaction without commentId: {}", interaction);
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            logger.info("Fetched {} post interactions for recipient: {}", interactions.size(), recipientId);
            return ResponseEntity.ok(interactions);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for fetching post interactions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to fetch post interactions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch post interactions: " + e.getMessage());
        }
    }

    @PostMapping("/mark-read")
    public ResponseEntity<?> markPostInteractionsAsRead(@RequestBody MarkReadRequest request) {
        logger.info("Marking post interactions as read for recipient: {}, type: {}", request.getRecipientId(), request.getType());
        try {
            String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Authenticated email: {}", authenticatedEmail);
            if (authenticatedEmail == null || authenticatedEmail.isEmpty()) {
                logger.warn("No authenticated user found for markPostInteractionsAsRead request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
            logger.info("Authenticated user ID: {}", authenticatedUser.getId());

            String recipientId = request.getRecipientId();
            if (recipientId == null || recipientId.isEmpty()) {
                recipientId = authenticatedUser.getId();
                logger.info("No recipientId provided, using authenticated user's ID: {}", recipientId);
            }

            if (!recipientId.equals(authenticatedUser.getId())) {
                logger.warn("User {} attempted to mark interactions as read for recipient {}", authenticatedUser.getId(), recipientId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot mark interactions as read for other users");
            }

            List<PostInteraction> interactions;
            if (request.getType() != null && !request.getType().isEmpty()) {
                logger.info("Fetching interactions of type: {} for recipient: {}", request.getType(), recipientId);
                interactions = postInteractionRepository.findByRecipientIdAndType(recipientId, request.getType());
            } else {
                logger.info("Fetching all interactions for recipient: {}", recipientId);
                interactions = postInteractionRepository.findByRecipientId(recipientId);
            }

            if (interactions.isEmpty()) {
                logger.info("No interactions found to mark as read for recipient: {}", recipientId);
            } else {
                for (PostInteraction interaction : interactions) {
                    interaction.setRead(true);
                }
                postInteractionRepository.saveAll(interactions);
                logger.info("Marked {} post interactions as read for recipient: {}", interactions.size(), recipientId);
            }
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for marking post interactions as read: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to mark post interactions as read: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark post interactions as read: " + e.getMessage());
        }
    }

    public static class MarkReadRequest {
        private String recipientId;
        private String type;

        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}