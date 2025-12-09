// [FILE]
// - 목적: User 비즈니스 로직
// - 주요 역할: 회원가입, 로그인 처리
// - 관련 토이 버전: [BE-v0.6]
// - 권장 읽는 순서: signup() → login()
//
// [LEARN] 비밀번호는 BCryptPasswordEncoder로 해시한다.
//         로그인 성공 시 JWT 토큰을 발급한다.

package com.example.minijob.user.service;

import com.example.minijob.config.JwtTokenProvider;
import com.example.minijob.user.domain.User;
import com.example.minijob.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// [Order 1] User 서비스
// - 토이 버전: [BE-v0.6]+
// [LEARN] 인증 로직은 서비스 레이어에서 처리한다.
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // [Order 2] 회원가입
    // [LEARN] 비밀번호를 해시하여 저장한다.
    @Transactional
    public User signup(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, "USER");
        return userRepository.save(user);
    }

    // [Order 3] 로그인
    // - 이메일/비밀번호 확인 후 JWT 토큰 발급
    // [LEARN] BCrypt의 matches 메서드로 비밀번호를 검증한다.
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole());
    }

    // [Order 4] 이메일로 사용자 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
