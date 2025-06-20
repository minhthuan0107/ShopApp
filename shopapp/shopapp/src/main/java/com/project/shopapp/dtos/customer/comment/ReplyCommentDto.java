package com.project.shopapp.dtos.customer.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCommentDto {
    @JsonProperty("parent_id")
    private Long parentId;
    private String content;
}
