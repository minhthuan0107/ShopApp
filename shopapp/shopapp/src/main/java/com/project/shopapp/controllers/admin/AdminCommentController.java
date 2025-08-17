package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.responses.admin.comment.CommentListResponse;
import com.project.shopapp.responses.customer.comment.CommentResponse;
import com.project.shopapp.services.admin.comment.CommentAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.admin-prefix}/comments")
public class AdminCommentController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private CommentAdminService commentAdminService;

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllComments(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(required = false, defaultValue = "") String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<CommentResponse> commentPage = commentAdminService.getAllParentComments(pageRequest, keyword);
        //Lấy tổng số trang
        int totalPages = commentPage.getTotalPages();
        //Lấy tổng số bình luận
        long totalItems = commentPage.getTotalElements();
        List<CommentResponse> commentResponseList = commentPage.getContent();
        CommentListResponse commentListResponse = CommentListResponse.builder()
                .commentResponses(commentResponseList)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .currentPage(page + 1)
                .build();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_GET_ALL_SUCCESSFULLY))
                .data(commentListResponse)
                .build());
    }


    @DeleteMapping("delete/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteCommentById(@PathVariable("commentId") Long commentId) {
        try {
            commentAdminService.deleteCommentById(commentId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.COMMENT_DELETE_SUCCESSFULLY, commentId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
