package com.project.shopapp.controllers.admin;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.responses.admin.rate.RateListResponse;
import com.project.shopapp.responses.admin.rate.RateResponse;
import com.project.shopapp.services.admin.rate.RateAdminService;
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
@RequestMapping("${api.admin-prefix}/rates")
public class AdminRateController {
    @Autowired
    private RateAdminService rateAdminService;
    @Autowired
    private LocalizationUtils localizationUtils;
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllRates(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false, defaultValue = "") String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<RateResponse> ratePage = rateAdminService.getAllRates(pageRequest, keyword);
        //Lấy tổng số trang
        int totalPages = ratePage.getTotalPages();
        //Lấy tổng số đánh giá
        long totalItems = ratePage.getTotalElements();
        List<RateResponse> rateResponseList = ratePage.getContent();
        RateListResponse rateListResponse = RateListResponse.builder()
                .rateResponses(rateResponseList)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .currentPage(page + 1)
                .build();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.RATING_GET_ALL_SUCCESSFULLY))
                .data(rateListResponse)
                .build());
    }
    @DeleteMapping("delete/{rateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteRateById(@PathVariable("rateId") Long rateId) {
        try {
            rateAdminService.deleteRateById(rateId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.RATING_DELETE_SUCCESSFULLY, rateId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
