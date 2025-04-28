package com.project.shopapp.configurations;

import com.project.shopapp.components.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;
    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String signinUrl = String.format("%s/users/signin", apiPrefix);
        String signupUrl = String.format("%s/users/signup", apiPrefix);
        String ordersUrl = String.format("%s/orders/**", apiPrefix);
        String categoryUrl = String.format("%s/categories/**", apiPrefix);
        String productUrl = String.format("%s/products/**", apiPrefix);
        String productimageUrl = String.format("%s/product-images/**", apiPrefix);
        String orderDetailUrl = String.format("%s/orderdetails/**", apiPrefix);
        String cartUrl = String.format("%s/carts/**", apiPrefix);
        String cartDetailtUrl = String.format("%s/cartdetails/**", apiPrefix);
        String paymentUrl = String.format("%s/payments/**", apiPrefix);
        String commentUrl = String.format("%s/comments/**", apiPrefix);
        String rateUrl = String.format("%s/rates/**", apiPrefix);
        String brandUrl = String.format("%s/brands/**", apiPrefix);
        String tokenUrl = String.format("%s/tokens/**", apiPrefix);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            // Các URL public
                            .requestMatchers(signinUrl, signupUrl)
                            .permitAll()
                            .requestMatchers(HttpMethod.GET, categoryUrl, productUrl, brandUrl,
                                    productimageUrl, commentUrl, rateUrl)
                            .permitAll()
                            //Phân quyền cho websocket
                            .requestMatchers("/ws/**", "/topic/**", "/app/**").permitAll()
                            .requestMatchers(HttpMethod.POST,tokenUrl).permitAll()
                            //Định nghĩa quyên cho payment
                            .requestMatchers(HttpMethod.POST, paymentUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.PUT, paymentUrl).hasRole("USER")
                            //Định nghĩa quyền cho comment
                            .requestMatchers(HttpMethod.POST, commentUrl).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(HttpMethod.PUT, commentUrl).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(HttpMethod.DELETE, commentUrl).hasAnyRole("USER", "ADMIN")
                            //Định nghĩa quyền cho cart
                            .requestMatchers(HttpMethod.POST, cartUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.DELETE, cartUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.PUT, cartUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.GET, cartUrl).hasRole("USER")
                            //Định nghĩa quyền cho cartdetails
                            .requestMatchers(HttpMethod.POST, cartDetailtUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.DELETE, cartDetailtUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.PUT, cartDetailtUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.GET, cartDetailtUrl).hasRole("USER")
                            //  Định nghĩa quyền cho orders
                            .requestMatchers(HttpMethod.POST, ordersUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.PUT, ordersUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, ordersUrl).hasAnyRole("USER", "ADMIN")
                            // Định nghĩa quyền cho categories
                            .requestMatchers(HttpMethod.DELETE, categoryUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, categoryUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, categoryUrl).hasRole("ADMIN")
                            //// Định nghĩa quyền cho product
                            .requestMatchers(HttpMethod.PUT, productUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, productUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, productUrl).hasRole("ADMIN")
                            //Định nghĩa quyền cho productimage
                            .requestMatchers(HttpMethod.PUT, productimageUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, productimageUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, productimageUrl).hasRole("ADMIN")
                            // Định nghĩa quyền cho orderDetail
                            .requestMatchers(HttpMethod.POST, orderDetailUrl).hasRole("USER")
                            .requestMatchers(HttpMethod.DELETE, orderDetailUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, orderDetailUrl).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, orderDetailUrl).hasAnyRole("USER", "ADMIN")
                            .anyRequest().authenticated();
                });
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Chỉ cho phép Angular (hoặc dùng setAllowedOriginPatterns)
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "CONNECT"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "sec-websocket-key",
                        "sec-websocket-version", "sec-websocket-extensions", "upgrade", "connection"));
                configuration.setExposedHeaders(List.of("x-auth-token", "sec-websocket-accept"));
                configuration.setAllowCredentials(true); // Quan trọng khi có session hoặc cooki
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                source.registerCorsConfiguration("/ws/**", configuration);
                source.registerCorsConfiguration("/ws/info", configuration);
                source.registerCorsConfiguration("/ws/info/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
