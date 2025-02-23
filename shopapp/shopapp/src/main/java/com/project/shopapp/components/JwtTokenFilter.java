package com.project.shopapp.components;

import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (isByPassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            // Lấy JWT từ request
            String jwt = parseJwt(request);
            if (jwt == null || !jwtTokenUtils.validateJwtToken(jwt))  {
                throw new JwtAuthenticationException("Invalid JWT Token");
            }
            // Trích xuất thông tin từ JWT
            String phoneNumber = jwtTokenUtils.getphoneNumberFromJwtToken(jwt);
            if (phoneNumber != null) {
                request.setAttribute("phoneNumber", phoneNumber);
            }
            // Tải thông tin người dùng từ UserDetailsService
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(phoneNumber);
            // Thiết lập thông tin xác thực
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Đặt thông tin xác thực vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is invalid or has expired");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/productimages/product", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/signin", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/signup", apiPrefix), "POST")
        );
        for (Pair<String, String> bypasstoken : bypassTokens) {
            if (request.getServletPath().contains(bypasstoken.getFirst()) &&
                    request.getMethod().equals(bypasstoken.getSecond())) {
                return true;
            }
        }
        return false;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
