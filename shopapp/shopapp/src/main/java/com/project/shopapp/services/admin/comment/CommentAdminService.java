package com.project.shopapp.services.admin.comment;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CommentRepository;
import com.project.shopapp.responses.comment.CommentReplyResponse;
import com.project.shopapp.responses.comment.CommentResponse;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentAdminService implements ICommentAdminService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    public Page<CommentResponse> getAllParentComments(PageRequest pageRequest, String keyword) {
        Page<Comment> commentPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            commentPage = commentRepository.findAllParentCommentsExcludingRates(pageRequest);
        } else {
            commentPage = commentRepository.searchParentCommentsByUserFullName(keyword, pageRequest);
        }

        return commentPage.map(comment -> {
            List<CommentReplyResponse> replyResponses = comment.getReplies()
                    .stream()
                    .map(CommentReplyResponse::fromReplyComment)
                    .sorted(Comparator.comparing(CommentReplyResponse::getCreateAt).reversed())
                    .collect(Collectors.toList());
            return CommentResponse.fromComment(comment, replyResponses);
        });
    }

    @Override
    public void deleteCommentById(Long commentId) throws Exception {
        if (!commentRepository.existsById(commentId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_NOT_FOUND));
        }
        commentRepository.deleteById(commentId);
    }
}
