package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            UPLOAD_DIR = uploadPath.toString();
            logger.info("Checking upload directory: {}", uploadPath);
            if (!Files.exists(uploadPath)) {
                logger.info("Upload directory {} does not exist, creating it", uploadPath);
                Files.createDirectories(uploadPath);
                logger.info("Upload directory created successfully: {}", uploadPath);
            } else {
                logger.info("Upload directory already exists: {}", uploadPath);
            }
            if (!Files.isWritable(uploadPath)) {
                logger.error("Upload directory {} is not writable", uploadPath);
                throw new RuntimeException("Upload directory is not writable: " + uploadPath);
            }
            logger.info("Upload directory {} is writable", uploadPath);
        } catch (IOException e) {
            logger.error("Failed to create upload directory: {}", UPLOAD_DIR, e);
            throw new RuntimeException("Failed to create upload directory: " + UPLOAD_DIR, e);
        }
    }

    @GetMapping("/test")
    public String test() {
        logger.info("Test endpoint called");
        return "Post API is working!";
    }

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam String creatorId,
            @RequestParam String creatorName,
            @RequestParam String category,
            @RequestParam String tags,
            @RequestParam boolean published,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "video", required = false) MultipartFile video) throws IOException {
        logger.info("Creating post for creatorId: {}", creatorId);
        Post post = postService.createPost(title, text, creatorId, creatorName, category, tags, published, images, video);
        logger.info("Post created with ID: {}", post.getId());
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching all posts, page: {}, size: {}", page, size);
        if (size > 100) {
            size = 100;
        }
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        logger.info("Fetching post with ID: {}", id);
        Post post = postService.getPostById(id);
        if (post == null) {
            logger.warn("Post not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable String userId) {
        logger.info("Fetching posts for userId: {}", userId);
        List<Post> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam String category,
            @RequestParam String tags,
            @RequestParam boolean published,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "video", required = false) MultipartFile video) throws IOException {
        logger.info("Updating post with ID: {}", id);
        Post post = postService.updatePost(id, title, text, category, tags, published, images, video);
        if (post == null) {
            logger.warn("Post not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Post updated with ID: {}", post.getId());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        logger.info("Deleting post with ID: {}", id);
        boolean deleted = postService.deletePost(id);
        if (!deleted) {
            logger.warn("Post not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Post deleted with ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable String id, @RequestParam String userId) {
        logger.info("Liking post with ID: {} by user: {}", id, userId);
        boolean liked = postService.likePost(id, userId);
        if (!liked) {
            logger.warn("Post not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Post liked/unliked with ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Post> addComment(
            @PathVariable String postId,
            @RequestParam String text,
            @RequestParam String creatorId,
            @RequestParam String creatorName) {
        logger.info("Adding comment to post with ID: {}", postId);
        Post post = postService.addComment(postId, text, creatorId, creatorName);
        if (post == null) {
            logger.warn("Post not found with ID: {}", postId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Comment added to post with ID: {}", postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Post.Comment>> getComments(@PathVariable String postId) {
        logger.info("Fetching comments for post with ID: {}", postId);
        List<Post.Comment> comments = postService.getComments(postId);
        if (comments == null) {
            logger.warn("Post not found with ID: {}", postId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Post> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestParam String userId) {
        logger.info("Deleting comment {} from post {}", commentId, postId);
        Post post = postService.deleteComment(postId, commentId, userId);
        if (post == null) {
            logger.warn("Comment {} not found or unauthorized for post {}", commentId, postId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Comment {} deleted from post {}", commentId, postId);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Post> updateComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestParam String userId,
            @RequestParam String text,
            @RequestParam String creatorId,
            @RequestParam String creatorName) {
        logger.info("Updating comment {} in post {}", commentId, postId);
        Post post = postService.updateComment(postId, commentId, userId, text, creatorId, creatorName);
        if (post == null) {
            logger.warn("Comment {} not found or unauthorized for post {}", commentId, postId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Comment {} updated in post {}", commentId, postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping(value = "/uploads/{filename:.+}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        logger.info("Serving file: {}", filename);
        Path uploadPath = Paths.get(UPLOAD_DIR).normalize();
        Path filePath = uploadPath.resolve(filename).normalize();
        if (!filePath.startsWith(uploadPath)) {
            logger.warn("Invalid file path: {}", filePath);
            return ResponseEntity.badRequest().build();
        }
        if (!Files.exists(filePath)) {
            logger.warn("File not found: {}", filePath);
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(filePath);
        String contentType = Files.probeContentType(filePath);
        logger.info("Serving file {} with content type: {}", filename, contentType);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(resource);
    }

    @GetMapping("/sample")
    public ResponseEntity<List<Post>> getSamplePosts() {
        logger.info("Fetching sample posts");
        List<Post> samplePosts = Arrays.asList(createSamplePost1(), createSamplePost2());
        return ResponseEntity.ok(samplePosts);
    }

    private Post createSamplePost1() {
        Post post = new Post();
        post.setId("sample1");
        post.setTitle("Sample Post 1");
        post.setText("This is the content of the first sample post.");
        post.setCreatorId("user1");
        post.setCreatorName("John Doe");
        post.setCategory("Strength");
        post.setTags(Arrays.asList("fitness", "strength"));
        post.setPublished(true);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post.Media media = new Post.Media();
        media.setPath("sample-image1.jpg");
        media.setType(Post.MediaType.IMAGE);
        post.setMedia(Arrays.asList(media));

        Post.Comment comment = new Post.Comment();
        comment.setId("comment1");
        comment.setText("Great post!");
        comment.setCreatorId("user2");
        comment.setCreatorName("Jane Smith");
        comment.setCreatedAt(LocalDateTime.now());
        post.setComments(Arrays.asList(comment));

        return post;
    }

    private Post createSamplePost2() {
        Post post = new Post();
        post.setId("sample2");
        post.setTitle("Sample Post 2");
        post.setText("This is the content of the second sample post.");
        post.setCreatorId("user3");
        post.setCreatorName("Alice Johnson");
        post.setCategory("Cardio");
        post.setTags(Arrays.asList("cardio", "workout"));
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post.Media media = new Post.Media();
        media.setPath("sample-video1.mp4");
        media.setType(Post.MediaType.VIDEO);
        post.setMedia(Arrays.asList(media));

        return post;
    }
}