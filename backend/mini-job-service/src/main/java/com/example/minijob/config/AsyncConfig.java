// [FILE]
// - 목적: 비동기 및 스케줄링 설정
// - 주요 역할: @Async, @Scheduled 활성화
// - 관련 토이 버전: [BE-v0.7]
// - 권장 읽는 순서: 어노테이션 확인
//
// [LEARN] @EnableScheduling으로 @Scheduled 어노테이션을 활성화한다.
//         @EnableAsync로 @Async 어노테이션을 활성화한다.

package com.example.minijob.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// [Order 1] 비동기/스케줄링 설정
// - 토이 버전: [BE-v0.7]+
// [LEARN] @Configuration 클래스에 활성화 어노테이션을 추가한다.
@Configuration
@EnableScheduling
@EnableAsync
public class AsyncConfig {
    // 별도의 ThreadPoolTaskExecutor 빈을 정의하여 스레드 풀을 커스터마이징할 수 있다.
    // 기본 설정을 사용하므로 여기서는 비워둔다.
}
