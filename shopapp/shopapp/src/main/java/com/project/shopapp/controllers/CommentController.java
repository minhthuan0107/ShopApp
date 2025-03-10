package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.comment.CommentDto;
import com.project.shopapp.dtos.comment.ReplyCommentDto;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.response.comment.CommentReplyResponse;
import com.project.shopapp.response.comment.CommentResponse;
import com.project.shopapp.services.comment.CommentServie;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/comments")
public class CommentController {
    @Autowired
    private CommentServie commentService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("create-coment/{userId}")
    public ResponseEntity<ResponseObject> createComment(@PathVariable Long userId,
                                                        @RequestBody CommentDto commentDto,
                                                        BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            CommentResponse commentResponse = commentService.createComment(userId, commentDto);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(commentResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.COMMENT_CREATED_SUCCESSFULLY, commentResponse.getId()))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());

        }
    }

    @PostMapping("create-reply-coment/{userId}")
    public ResponseEntity<ResponseObject> createReplyComment(@PathVariable Long userId,
                                                             @RequestBody ReplyCommentDto replyCommentDto,
                                                             BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            CommentReplyResponse replyResponse = commentService.createReplyComment(userId, replyCommentDto);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(replyResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.COMMENT_CREATED_SUCCESSFULLY, replyResponse.getId()))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("get-all/{productId}")
    public ResponseEntity<ResponseObject> getAllParentCommentsByProductId(@PathVariable Long productId) {
        List<CommentResponse> comments = commentService.getAllParentCommentsByProductId(productId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(comments)
                .message(localizationUtils.getLocalizedMessage(
                        MessageKeys.COMMENT_GET_LIST_SUCCESSFULLY, productId))
                .build());
    }
}
