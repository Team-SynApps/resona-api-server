package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.entity.Scrap;
import synapps.resona.api.mysql.socialMedia.service.ScrapService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping("/scrap/{feedId}")
    public ResponseEntity<?> registerScrap(HttpServletRequest request,
                                           @PathVariable Long feedId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Scrap scrap = scrapService.register(feedId);
        ResponseDto responseData = new ResponseDto(metaData, List.of(scrap));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/scrap/{scrapId}")
    public ResponseEntity<?> readScrap(HttpServletRequest request,
                                       @PathVariable Long scrapId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Scrap scrap = scrapService.read(scrapId);
        ResponseDto responseData = new ResponseDto(metaData, List.of(scrap));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/scrap/{scrapId}")
    public ResponseEntity<?> cancelScrap(HttpServletRequest request,
                                         @PathVariable Long scrapId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Scrap scrap = scrapService.cancelScrap(scrapId);
        ResponseDto responseData = new ResponseDto(metaData, List.of(scrap));
        return ResponseEntity.ok(responseData);
    }
}
