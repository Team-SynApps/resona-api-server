package com.synapps.resona.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
    @NotBlank(message = "메시지 내용은 비워둘 수 없습니다.")
    String content
) {
  // 이미지나 파일 관련 필드가 필요하면 여기에 추가하면 됩니다.
  // String fileUrl
}