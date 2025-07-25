package synapps.resona.api.global.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import synapps.resona.api.global.dto.metadata.Meta;

@Getter
@AllArgsConstructor
public class ResponseDto {

  private Meta meta;

  private List<?> data;

  public ResponseDto() {
  }
}