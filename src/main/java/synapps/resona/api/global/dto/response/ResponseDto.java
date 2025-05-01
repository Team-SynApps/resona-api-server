package synapps.resona.api.global.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import synapps.resona.api.global.dto.metadata.MetaDataDto;

@Getter
@AllArgsConstructor
public class ResponseDto {

  private MetaDataDto meta;

  private List<?> data;

  public ResponseDto() {
  }
}