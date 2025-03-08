package com.project.shopapp.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class VNPayConfig {
    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;
    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;
    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;
    @Value("${vnpay.secret-key}")
    private String secretKey;
    @Value("${vnpay.api-url}")
    private String vnpApiUrl;

}
