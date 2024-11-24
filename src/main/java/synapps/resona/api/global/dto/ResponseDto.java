package synapps.resona.api.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseDto {
    private MetaDataDto meta;

    private List<?> data;
    public ResponseDto() {}
}