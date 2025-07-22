package com.project.shopapp.responses.admin.rate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Rate;
import com.project.shopapp.responses.comment.CommentReplyResponse;
import lombok.Builder;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class RateResponse extends BaseEntity {
    @JsonProperty("rate_id")
    private Long rateId;
    private int rating;
    @JsonProperty("comment_id")
    private Long commentId;
    @JsonProperty("comment_content")
    private String commentContent;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("commenter_name")
    private String commenterName;
    @JsonProperty("replies")
    private List<CommentReplyResponse> replies;

    public static RateResponse fromRate(Rate rate) {
        Comment comment = rate.getComment();
        List<CommentReplyResponse> commentReplyResponses = comment.getReplies().stream()
                .map(CommentReplyResponse::fromReplyComment)
                .sorted(Comparator.comparing(CommentReplyResponse::getCreateAt).reversed())
                .collect(Collectors.toList());

        RateResponse rateWithCommentResponse = RateResponse.builder()
                .rateId(rate.getId())
                .rating(rate.getRating())
                .commentId(comment.getId())
                .commentContent(comment.getContent())
                .productName(rate.getProduct().getName())
                .commenterName(comment.getUser().getFullname())
                .replies(commentReplyResponses)
                .build();
        rateWithCommentResponse.setCreateAt(rate.getCreateAt());
        rateWithCommentResponse.setUpdateAt(rate.getUpdateAt());
        return rateWithCommentResponse;
    }
}
