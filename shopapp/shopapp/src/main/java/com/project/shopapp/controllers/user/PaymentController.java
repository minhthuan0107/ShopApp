package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.payment.PaymentRequestDto;
import com.project.shopapp.dtos.payment.PaymentQueryDto;
import com.project.shopapp.dtos.payment.PaymentRefundDto;
import com.project.shopapp.dtos.payment.UpdatePaymentStatusDto;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.response.payment.PaymentResponse;
import com.project.shopapp.response.product.ProductResponse;
import com.project.shopapp.services.email.OrderMailService;
import com.project.shopapp.services.payment.PaymentService;
import com.project.shopapp.services.payment.VNPayService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/payments")
public class PaymentController {
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private PaymentService paymentService;



    @PostMapping("/create_payment_url")
    public ResponseEntity<ResponseObject> createPayment(
            @RequestBody PaymentRequestDto paymentRequest,
            HttpServletRequest request) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(paymentRequest, request);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.PAYMENT_URL_GENERATED_SUCCESSFULLY))
                            .data(paymentUrl)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.ERROR_GENERATED_PAYMENT_URL, e.getMessage()))
                            .build());
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<ResponseObject> updatePaymentStatus(@RequestBody UpdatePaymentStatusDto statusDto,
                                                              BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            PaymentResponse paymentResponse = paymentService.updatePaymentStatus(statusDto);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.PAYMENT_UPDATE_SUCCESSFULLY))
                    .data(paymentResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.ERROR_GENERATED_PAYMENT_URL, e.getMessage()))
                            .build());
        }
    }

    @PostMapping("/query")
    public ResponseEntity<ResponseObject> queryTransaction(
            @RequestBody PaymentQueryDto paymentQueryDto,
            HttpServletRequest request) {
        try {
            String result = vnPayService.queryTransaction(paymentQueryDto, request);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.PAYMENT_QUERY_SUCCESSFULLY))
                            .data(result)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.PAYMENT_ERROR_QUERY_TRANSACTION, e.getMessage()))
                    .build());
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<ResponseObject> refundTransaction(
            @Valid @RequestBody PaymentRefundDto paymentRefundDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(String.join(", ", errorMessages))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }

        try {
            String response = vnPayService.refundTransaction(paymentRefundDTO);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.PAYMENT_REFUND_QUERY_TRANSACTION))
                            .status(HttpStatus.OK)
                            .data(response)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.PAYMENT_REFUND_FAILED, e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }
    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseObject> getOrderIdByTransactionId( @PathVariable String transactionId ){
        try {
            Long orderId = paymentService.getOrderIdByTransactionId(transactionId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(Collections.singletonMap("order_id", orderId))
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.ORDER_RETRIEVED_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

}
