package com.project.shopapp.responses.comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Comment;
import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentReplyResponse extends BaseEntity {
    @JsonProperty("reply_comment_id")
    private Long id;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("user_id")
    private Long userId;
    private String content;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("parent_id")
    private Long parentId;
    public static CommentReplyResponse fromReplyComment (Comment comment){
        CommentReplyResponse replyResponse= CommentReplyResponse.builder()
                .id(comment.getId())
                .productId(comment.getProduct().getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getFullname())
                .content(comment.getContent())
                .parentId(comment.getParentComment().getId())
                .build();
        replyResponse.setCreateAt(comment.getCreateAt());
        replyResponse.setUpdateAt(comment.getUpdateAt());
        return replyResponse;
    }
}
