// [FILE]
// - 목적: JWT 인증 필터
// - 주요 역할: HTTP 요청에서 JWT 토큰을 추출하고 인증 처리
// - 관련 토이 버전: [BE-v0.6]
// - 권장 읽는 순서: doFilterInternal() → resolveToken() → 인증 설정
//
// [LEARN] OncePerRequestFilter를 상속하여 요청당 한 번 실행되는 필터를 만든다.
//         SecurityContextHolder에 인증 정보를 설정한다.

package com.example.minijob.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// [Order 1] JWT 인증 필터
// - 모든 요청에 대해 JWT 토큰을 검사
// - 토이 버전: [BE-v0.6]+
// [LEARN] Authorization 헤더에서 Bearer 토큰을 추출한다.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // [Order 2] 필터 처리
    // [LEARN] 토큰이 유효하면 SecurityContextHolder에 인증 정보를 설정한다.
    @Override
    // [LEARN] Servlet Filter의 doFilterInternal 메서드는 요청이 들어올 때마다 호출됩니다.
    //         여기서는 JWT 토큰 파싱/검증 로직을 수행하고 인증 정보를 SecurityContext에 설정합니다.
    //         실제 배포 환경에서는 예외 처리와 로깅 정책을 강화하세요.
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmail(token);
            String role = jwtTokenProvider.getRole(token);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    // [Order 3] 토큰 추출
    // - Authorization: Bearer {token} 형식에서 토큰만 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
