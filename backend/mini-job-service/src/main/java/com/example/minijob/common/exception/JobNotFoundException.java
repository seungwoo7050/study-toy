// [FILE]
// - 목적: Job을 찾을 수 없을 때 발생하는 예외
// - 주요 역할: 비즈니스 예외 정의
// - 관련 토이 버전: [BE-v0.5]
// - 권장 읽는 순서: 클래스 선언 확인
//
// [LEARN] 커스텀 예외를 정의하여 비즈니스 오류를 명확히 표현한다.
//         RuntimeException을 상속하여 unchecked 예외로 만든다.

package com.example.minijob.common.exception;

// [Order 1] Job 미존재 예외
// - 토이 버전: [BE-v0.5]
// [LEARN] 도메인별로 의미 있는 예외를 정의하면 예외 처리가 명확해진다.
public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
