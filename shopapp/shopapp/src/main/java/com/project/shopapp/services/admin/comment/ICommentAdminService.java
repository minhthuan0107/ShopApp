package com.project.shopapp.services.admin.comment;
import com.project.shopapp.responses.comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ICommentAdminService {
    Page<CommentResponse> getAllParentComments(PageRequest pageRequest, String keyword);
    void deleteCommentById (Long commentId) throws Exception;
}
