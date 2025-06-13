package com.project.shopapp.components;

import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.configurations.UserDetailsServiceImpl;
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
import org.springframework.util.AntPathMatcher;
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
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (isByPassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Nếu là WebSocket request, bỏ qua filter
        if ("websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            // Lấy JWT từ request
            String jwt = parseJwt(request);
            // Nếu token không hợp lệ sẽ ném exception ở đây
            jwtTokenUtils.validateJwtToken(jwt);

            String loginType = jwtTokenUtils.getLoginTypeFromJwtToken(jwt);
            request.setAttribute("loginType", loginType);
            if("Phone".equalsIgnoreCase(loginType)) {
                // Trích xuất thông tin từ JWT
                String phoneNumber = jwtTokenUtils.getSubjectFromJwtToken(jwt);
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
            } else if ("Google".equalsIgnoreCase(loginType)){
                String googleAccountId = jwtTokenUtils.getSubjectFromJwtToken(jwt);
                if (googleAccountId != null) {
                    request.setAttribute("googleAccountId", googleAccountId);
                }
                UserDetailsImpl userDetails = (UserDetailsImpl) ((UserDetailsServiceImpl) userDetailsService).loadUserByGoogleAccountId(googleAccountId);
                // Thiết lập thông tin xác thực
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Đặt thông tin xác thực vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is invalid or has expired");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/products/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/product-images/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/signin", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/signup", apiPrefix), "POST"),
                Pair.of(String.format("%s/comments/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/rates/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/brands", apiPrefix), "GET"),
                Pair.of(String.format("%s/tokens/new-access-token", apiPrefix), "POST"),
                Pair.of(String.format("%s/tokens/refresh-token", apiPrefix), "PATCH"),
                Pair.of(String.format("%s/users/auth/social-login", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/auth/social/callback", apiPrefix), "GET"),


                Pair.of("/ws/**", "GET"),
                Pair.of("/topic/**", "GET"),
                Pair.of("/app/**", "GET")
        );
        for (Pair<String, String> bypass : bypassTokens) {
            if (request.getMethod().equalsIgnoreCase(bypass.getSecond())
                    && antPathMatcher.match(bypass.getFirst(), request.getServletPath())) {
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
