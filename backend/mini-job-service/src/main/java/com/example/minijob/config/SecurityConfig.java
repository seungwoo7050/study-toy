// [FILE]
// - 목적: Spring Security 설정
// - 주요 역할: 보안 정책, CORS, JWT 필터 등록
// - 관련 토이 버전: [BE-v0.6]
// - 권장 읽는 순서: securityFilterChain() → cors() → passwordEncoder()
//
// [LEARN] Spring Security 6.x에서는 SecurityFilterChain 빈으로 보안을 설정한다.
//         lambda DSL을 사용하여 설정을 구성한다.

package com.example.minijob.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// [Order 1] Security 설정
// - 토이 버전: [BE-v0.6]+
// [LEARN] @EnableWebSecurity로 Spring Security를 활성화한다.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // [Order 2] 보안 필터 체인
    // [LEARN] 각 경로별로 접근 권한을 설정한다.
    //         JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가한다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/jobs/**").permitAll()  // 토이 프로젝트이므로 허용
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))  // H2 콘솔용
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // [Order 3] CORS 설정
    // - 프론트엔드(localhost:5173, 3000)에서의 요청 허용
    // [LEARN] 개발 환경에서는 넓은 CORS 설정을 사용하지만,
    //         운영 환경에서는 필요한 origin만 허용해야 한다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:8081"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // [Order 4] 비밀번호 인코더
    // [LEARN] BCrypt는 솔트를 자동으로 생성하여 레인보우 테이블 공격을 방지한다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// [Reader Notes]
// ------------------------------------------------------------
// 이 파일에서 새로 등장한 개념:
// 1. SecurityFilterChain을 이용한 Spring Security 설정
// 2. JWT 기반 stateless 인증
// 3. CORS 설정
// 4. BCryptPasswordEncoder를 이용한 비밀번호 해싱
//
// 다음에 보면 좋은 파일:
// - JwtTokenProvider.java: JWT 토큰 생성/검증
// - JwtAuthenticationFilter.java: JWT 필터
