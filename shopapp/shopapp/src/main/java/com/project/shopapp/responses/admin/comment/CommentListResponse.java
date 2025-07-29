package com.project.shopapp.responses.admin.comment;

import com.project.shopapp.responses.customer.comment.CommentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class CommentListResponse {
    private List<CommentResponse> commentResponses;
    private int totalPages;
    private long totalItems;
    private int currentPage;
}
