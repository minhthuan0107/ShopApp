package com.project.shopapp.controllers.websocket;
import com.project.shopapp.response.comment.CommentReplyResponse;
import com.project.shopapp.response.comment.CommentResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class CommentWebSocketController {
    @MessageMapping("/comment") // Nhận tin nhắn từ client gửi lên "/app/comment"
    @SendTo("/topic/comments") // Gửi tin nhắn đến tất cả client đang subscribe "/topic/comments"
    public CommentResponse sendComment(CommentResponse commentResponse) {
        return commentResponse;
    }
    @MessageMapping("/reply-comment")
    @SendTo("/topic/comments")
    public CommentReplyResponse sendReplyComment(CommentReplyResponse commentReplyResponse) {
        return commentReplyResponse;
    }
}
