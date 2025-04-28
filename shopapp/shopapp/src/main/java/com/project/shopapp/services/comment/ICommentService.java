package com.project.shopapp.services.comment;

import com.project.shopapp.dtos.comment.CommentDto;
import com.project.shopapp.dtos.comment.ReplyCommentDto;
import com.project.shopapp.response.comment.CommentReplyResponse;
import com.project.shopapp.response.comment.CommentResponse;

import java.util.List;

public interface ICommentService {
    CommentResponse createComment (Long userId, CommentDto commentDto) throws Exception;
    CommentReplyResponse createReplyComment (Long userId, ReplyCommentDto replyDto) throws Exception;
    List<CommentResponse> getParentCommentsByProductId(Long productId);

}
