package synapps.resona.api.external.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailCheckExceptionDto {

  private final String message;
  private final Integer count;
}
