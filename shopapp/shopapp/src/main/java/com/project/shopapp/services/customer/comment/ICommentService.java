package com.project.shopapp.services.customer.comment;

import com.project.shopapp.dtos.customer.comment.CommentDto;
import com.project.shopapp.dtos.customer.comment.ReplyCommentDto;
import com.project.shopapp.responses.comment.CommentReplyResponse;
import com.project.shopapp.responses.comment.CommentResponse;

import java.util.List;

public interface ICommentService {
    CommentResponse createComment (Long userId, CommentDto commentDto) throws Exception;
    CommentReplyResponse createReplyComment (Long userId, ReplyCommentDto replyDto) throws Exception;
    List<CommentResponse> getParentCommentsByProductId(Long productId);

}
