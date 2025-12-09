// [FILE]
// - 목적: Spring Boot 애플리케이션의 메인 진입점
// - 주요 역할: Spring Boot 애플리케이션을 부트스트랩하고 실행
// - 관련 토이 버전: [BE-v0.1]
// - 권장 읽는 순서: main() 메서드 확인
//
// [LEARN] @SpringBootApplication 어노테이션은 @Configuration, @EnableAutoConfiguration,
//         @ComponentScan을 합친 것이다. Spring Boot가 자동으로 설정을 구성하는 방식을 이해한다.

package com.example.minijob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// [Order 1] 메인 클래스
// - Spring Boot 애플리케이션의 시작점
// - 토이 버전: [BE-v0.1]
// [LEARN] SpringApplication.run()은 내장 톰캣 서버를 시작하고,
//         컴포넌트 스캔을 통해 빈을 등록한다.
@SpringBootApplication
public class MiniJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniJobApplication.class, args);
    }
}
