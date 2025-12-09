// [FILE]
// - 목적: 서버 상태를 확인하는 헬스체크 엔드포인트 제공
// - 주요 역할: GET /health 요청에 대해 서버 상태를 JSON으로 응답
// - 관련 토이 버전: [BE-v0.1]
// - 권장 읽는 순서: health() 메서드 확인
//
// [LEARN] @RestController는 @Controller + @ResponseBody의 조합이다.
//         반환값이 자동으로 JSON으로 직렬화되어 HTTP 응답 본문에 포함된다.

package com.example.minijob;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// [Order 1] 헬스체크 컨트롤러
// - 서버가 정상적으로 실행 중인지 확인하는 엔드포인트
// - 토이 버전: [BE-v0.1]
// [LEARN] Map을 반환하면 Jackson이 자동으로 JSON 객체로 변환한다.
//         {"status": "ok"} 형태로 응답된다.
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
