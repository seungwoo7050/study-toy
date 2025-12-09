// [FILE]
// - 목적: Job 생성 요청 DTO
// - 주요 역할: 클라이언트로부터 Job 생성에 필요한 데이터를 받음
// - 관련 토이 버전: [BE-v0.2], [BE-v0.5]
// - 권장 읽는 순서: 필드 → 검증 어노테이션 → getter
//
// [LEARN] DTO(Data Transfer Object)는 계층 간 데이터 전달에 사용된다.
//         엔티티를 직접 노출하지 않고 API 스펙에 맞는 DTO를 사용한다.

package com.example.minijob.job.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// [Order 1] 생성 요청 DTO
// - Job 생성에 필요한 필드만 포함
// - 토이 버전: [BE-v0.2], [BE-v0.5] (검증 추가)
// [LEARN] @Valid와 함께 사용하면 요청 데이터를 자동으로 검증한다.
public class CreateJobRequest {

    @NotBlank(message = "Job type is required")
    @Size(max = 50, message = "Type must be less than 50 characters")
    private String type;

    @Size(max = 10000, message = "Payload must be less than 10000 characters")
    private String payload;

    // 기본 생성자 (JSON 역직렬화용)
    public CreateJobRequest() {
    }

    public CreateJobRequest(String type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
