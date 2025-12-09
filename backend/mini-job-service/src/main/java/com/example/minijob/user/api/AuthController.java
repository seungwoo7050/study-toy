// [FILE]
// - 목적: 인증 관련 HTTP 엔드포인트
// - 주요 역할: 회원가입, 로그인 API 제공
// - 관련 토이 버전: [BE-v0.6]
// - 권장 읽는 순서: signup() → login()
//
// [LEARN] 인증 엔드포인트는 보안 필터에서 제외하여 누구나 접근 가능하게 한다.

package com.example.minijob.user.api;

import com.example.minijob.user.domain.User;
import com.example.minijob.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// [Order 1] Auth 컨트롤러
// - 토이 버전: [BE-v0.6]+
// [LEARN] /api/auth 경로는 SecurityConfig에서 permitAll로 설정한다.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // [Order 2] 회원가입
    // [LEARN] 성공 시 201 Created와 사용자 정보를 반환한다.
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            User user = userService.signup(request.email(), request.password());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "message", "User registered successfully"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // [Order 3] 로그인
    // [LEARN] 성공 시 JWT 토큰을 반환한다.
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.login(request.email(), request.password());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // DTO Records
    public record SignupRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6) String password
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}
}
