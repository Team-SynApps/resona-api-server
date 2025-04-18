package synapps.resona.api.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import synapps.resona.api.global.dto.metadata.MetaDataDto;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseDto {
    private MetaDataDto meta;

    private List<?> data;

    public ResponseDto() {
    }
}