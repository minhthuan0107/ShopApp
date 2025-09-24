package com.project.shopapp.services.admin.comment;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CommentRepository;
import com.project.shopapp.responses.customer.comment.CommentReplyResponse;
import com.project.shopapp.responses.customer.comment.CommentResponse;
import com.project.shopapp.services.admin.comment.CommentAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CommentAdminServiceTest {
    @InjectMocks
    private CommentAdminService commentAdminService;
    @Mock
    private CommentRepository commentRepository;

    private PageRequest pageRequest;

    @Mock
    private LocalizationUtils localizationUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pageRequest = PageRequest.of(0, 10);
    }
    //Test Lấy danh sách comment cha (k bao gồm comment đánh giá)
    // Case 1: Keyword là null
    @Test
    void getAllParentComments_ShouldCallFindAll_WhenKeywordIsNull() {
        User user = User.builder()
                .id(1L)
                .fullname("Nguyen Van A")
                .build();

        Product product = Product.builder()
                .id(1L)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .user(user)
                .product(product)
                .content("This is a parent comment")
                .replies(Collections.emptyList())  // parent comment nên để empty replies
                .build();
        // Tạo một trang dữ liệu giả lập (mock data) để dùng trong test.
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageRequest, 1);

        when(commentRepository.findAllParentCommentsExcludingRates(pageRequest))
                .thenReturn(commentPage);
        Page<CommentResponse> result = commentAdminService.getAllParentComments(pageRequest, null);
        assertEquals(1, result.getTotalElements());
        verify(commentRepository, times(1)).findAllParentCommentsExcludingRates(pageRequest);
        verify(commentRepository, never()).searchParentCommentsByUserFullName(anyString(), any());
    }
    //Case 2: Keyword là rỗng
    @Test
    void getAllParentComments_ShouldCallFindAll_WhenKeywordIsEmpty() {
        User user = User.builder()
                .id(1L)
                .fullname("Nguyen Van A")
                .build();

        Product product = Product.builder()
                .id(1L)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .user(user)
                .product(product)
                .content("This is a parent comment")
                .replies(Collections.emptyList())  // parent comment nên để empty replies
                .build();
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageRequest, 1);

        when(commentRepository.findAllParentCommentsExcludingRates(pageRequest))
                .thenReturn(commentPage);

        Page<CommentResponse> result = commentAdminService.getAllParentComments(pageRequest, "");

        assertEquals(1, result.getTotalElements());
        verify(commentRepository, times(1)).findAllParentCommentsExcludingRates(pageRequest);
        verify(commentRepository, never()).searchParentCommentsByUserFullName(anyString(), any());
    }

    // Case 3: Keyword không rỗng | null
    @Test
    void getAllParentComments_ShouldCallSearch_WhenKeywordIsNotEmpty() {
        User user = User.builder()
                .id(1L)
                .fullname("Nguyen Van A")
                .build();

        Product product = Product.builder()
                .id(1L)
                .build();

        Comment reply1 = Comment.builder()
                .id(1L)
                .user(user)
                .product(product)
                .content("This is a reply 1")
                .build();
        reply1.setCreateAt(LocalDateTime.now().minusDays(1));

        Comment reply2 = Comment.builder()
                .id(2L)
                .user(user)
                .product(product)
                .content("This is a reply 2")
                .build();
        reply2.setCreateAt(LocalDateTime.now());

        Comment comment = Comment.builder()
                .id(100L)
                .user(user)
                .product(product)
                .content("This is a parent comment")
                .replies(List.of(reply1, reply2))
                .build();

        // gán ngược lại parent cho reply
        reply1.setParentComment(comment);
        reply2.setParentComment(comment);

        Page<Comment> commentPage = new PageImpl<>(List.of(comment),pageRequest,1);
        when(commentRepository.searchParentCommentsByUserFullName("A", pageRequest))
                .thenReturn(commentPage);
        Page<CommentResponse> result = commentAdminService.getAllParentComments(pageRequest,"A");

        assertEquals(1,result.getTotalElements());
        List<CommentReplyResponse> replies = result.getContent().get(0).getReplies();
        assertEquals(2, replies.size());
        assertEquals(2L, replies.get(0).getId());

        verify(commentRepository, times(1)).searchParentCommentsByUserFullName("A", pageRequest);
        verify(commentRepository, never()).findAllParentCommentsExcludingRates(pageRequest);
    }
    //Test xóa comment dựa trên Id
    //Case 1: Không tìm thấy comment theo Id
    @Test
    void deleteCommentById_ShouldThrowException_WhenCommentNotFound() throws Exception {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId))
                .thenReturn(false);
        when(localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_NOT_FOUND))
                .thenReturn("COMMENT_NOT_FOUND");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
        () -> commentAdminService.deleteCommentById(commentId));

        assertEquals("COMMENT_NOT_FOUND", exception.getMessage());
        verify(commentRepository, never()).deleteById(commentId);
    }
    //Case 2: Xóa comment thành công dựa trên Id
    @Test
    void deleteCommentById_ShouldDelete_WhenCommentExists() throws Exception {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        commentAdminService.deleteCommentById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

}
