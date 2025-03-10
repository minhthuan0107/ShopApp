package com.project.shopapp.response.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Cart;
import com.project.shopapp.models.Comment;
import com.project.shopapp.response.cart.CartResponse;
import com.project.shopapp.response.cartdetail.CartDetailResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse extends BaseEntity {
    @JsonProperty("comment_id")
    private Long id;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("user_id")
    private Long userId;
    private String content;
    private List<CommentReplyResponse> rereplies;
    public static CommentResponse fromComment (Comment comment,List<CommentReplyResponse> replyResponses){
        CommentResponse commentResponse=  CommentResponse.builder()
                .id(comment.getId())
                .productId(comment.getProduct().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .rereplies(replyResponses)
                .build();
        commentResponse.setCreateAt(comment.getCreateAt());
        commentResponse.setUpdateAt(comment.getUpdateAt());
        return commentResponse;
    }

}
