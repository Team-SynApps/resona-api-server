package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.MetaDataDto;
import synapps.resona.api.global.dto.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.interests.InterestsRequest;
import synapps.resona.api.mysql.member.service.InterestsService;

import java.util.List;

@RestController
@RequestMapping("/interests")
@RequiredArgsConstructor
public class InterestsController {
    private final InterestsService interestsService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    public ResponseEntity<?> registerInterests(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @Valid @RequestBody InterestsRequest interestsRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(interestsService.registerInterests(interestsRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping()
    public ResponseEntity<?> getInterests(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @PathVariable Long memberId) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(interestsService.getInterests()));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping()
    public ResponseEntity<?> editInterests(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable Long memberId,
                                           @Valid @RequestBody InterestsRequest interestsRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(interestsService.editInterests(interestsRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteInterests(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @PathVariable Long memberId) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(interestsService.deleteInterests()));
        return ResponseEntity.ok(responseData);
    }
}
