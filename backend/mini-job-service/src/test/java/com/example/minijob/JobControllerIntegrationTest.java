// [FILE]
// - 목적: Job 컨트롤러 통합 테스트
// - 주요 역할: 전체 애플리케이션 컨텍스트에서 API 엔드포인트 테스트
// - 관련 토이 버전: [BE-v0.8]
// - 권장 읽는 순서: 설정 → createJob_Success() → getJob_NotFound() → listJobs()
//
// [LEARN] @SpringBootTest로 전체 애플리케이션 컨텍스트를 로드한다.
//         MockMvc를 사용하여 HTTP 요청을 시뮬레이션한다.

package com.example.minijob;

import com.example.minijob.job.api.CreateJobRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// [Order 1] 통합 테스트 클래스
// - 토이 버전: [BE-v0.8]
// [LEARN] @SpringBootTest는 실제 애플리케이션과 동일한 환경에서 테스트한다.
//         @AutoConfigureMockMvc로 MockMvc를 자동 설정한다.
@SpringBootTest
@AutoConfigureMockMvc
class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // [Order 2] 헬스체크 테스트
    // [LEARN] GET 요청을 보내고 응답을 검증한다.
    @Test
    void healthCheck_ReturnsOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    // [Order 3] Job 생성 성공 테스트
    // [LEARN] POST 요청과 함께 JSON 본문을 전송한다.
    @Test
    void createJob_Success() throws Exception {
        CreateJobRequest request = new CreateJobRequest("VIDEO_TRIM", "{\"duration\": 60}");

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("VIDEO_TRIM"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // [Order 4] 잘못된 요청 테스트
    // [LEARN] 검증 실패 시 400 Bad Request를 반환한다.
    @Test
    void createJob_InvalidRequest_ReturnsBadRequest() throws Exception {
        String invalidRequest = "{\"type\": \"\", \"payload\": \"test\"}";

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    // [Order 5] Job 목록 조회 테스트
    @Test
    void listJobs_ReturnsJobList() throws Exception {
        // 먼저 Job 생성
        CreateJobRequest request = new CreateJobRequest("MATCHMAKING", null);
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // 목록 조회
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // [Order 6] 존재하지 않는 Job 조회 테스트
    // [LEARN] 404 Not Found 응답을 검증한다.
    @Test
    void getJob_NotFound() throws Exception {
        mockMvc.perform(get("/api/jobs/99999"))
                .andExpect(status().isNotFound());
    }
}
