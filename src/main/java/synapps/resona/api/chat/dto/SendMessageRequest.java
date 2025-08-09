package synapps.resona.api.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMessageRequest {

  @NotBlank(message = "메시지 내용은 비워둘 수 없습니다.")
  private String content;

  // TODO: 이미지나 파일 전송 시 필요한 필드들 나중에 필요시 추가
  // private String fileUrl;
}