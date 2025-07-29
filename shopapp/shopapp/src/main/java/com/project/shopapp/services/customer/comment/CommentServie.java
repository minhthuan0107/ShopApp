package com.project.shopapp.services.customer.comment;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.comment.CommentDto;
import com.project.shopapp.dtos.customer.comment.ReplyCommentDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CommentRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.customer.comment.CommentReplyResponse;
import com.project.shopapp.responses.customer.comment.CommentResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServie implements ICommentService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentResponse createComment(Long userId, CommentDto commentDto) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId)));
        Product product = productRepository.findById(commentDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(
                                MessageKeys.PRODUCT_NOT_FOUND, commentDto.getProductId())
                ));
        Comment newComment = Comment.builder()
                .content(commentDto.getContent())
                .user(existingUser)
                .product(product)
                .build();
        Comment savedComment = commentRepository.save(newComment);
        return CommentResponse.fromComment(savedComment, new ArrayList<>());
    }

    @Override
    @Transactional
    public CommentReplyResponse createReplyComment(Long userId, ReplyCommentDto replyDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId)));
        // Kiểm tra parent comment có tồn tại không
        Comment parentComment = commentRepository.findById(replyDto.getParentId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_NOT_FOUND, replyDto.getParentId())));
        Comment replyComment = Comment.builder()
                .product(parentComment.getProduct())
                .user(user)
                .content(replyDto.getContent())
                .parentComment(parentComment)
                .build();
        commentRepository.save(replyComment);
        return CommentReplyResponse.fromReplyComment(replyComment);
    }

    @Override
    public List<CommentResponse> getParentCommentsByProductId(Long productId) {
        // Lấy tất cả comment cha (parent_id = null)
        List<Comment> parentComments = commentRepository.findByProductIdAndParentCommentIsNull(productId);
        return parentComments.stream()
                .map(comment -> {
                    // Lấy danh sách replies của comment cha
                    List<CommentReplyResponse> replyResponses = comment.getReplies()
                            .stream()
                            .map(CommentReplyResponse::fromReplyComment)
                            .sorted(Comparator.comparing(CommentReplyResponse::getCreateAt).reversed())
                            .collect(Collectors.toList());
                    return CommentResponse.fromComment(comment, replyResponses);
                })
                .collect(Collectors.toList());
    }



}
