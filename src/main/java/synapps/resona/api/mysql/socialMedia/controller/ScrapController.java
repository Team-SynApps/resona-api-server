package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.dto.metadata.CursorBasedMetaDataDto;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.scrap.ScrapReadResponse;
import synapps.resona.api.mysql.socialMedia.service.ScrapService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ScrapController {

  private final ScrapService scrapService;
  private final ServerInfoConfig serverInfo;

  private MetaDataDto createSuccessMetaData(String queryString) {
    return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  private MetaDataDto createCursorMetaData(String queryString, String cursor, int size,
      boolean hasNext) {
    return CursorBasedMetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName(), cursor, size, hasNext);
  }

  @PostMapping("/scrap/{feedId}")
  public ResponseEntity<?> registerScrap(HttpServletRequest request,
      @PathVariable Long feedId) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ScrapReadResponse scrap = ScrapReadResponse.from(scrapService.register(feedId));
    ResponseDto responseData = new ResponseDto(metaData, List.of(scrap));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/scrap/{scrapId}")
  public ResponseEntity<?> readScrap(HttpServletRequest request,
      @PathVariable Long scrapId) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ScrapReadResponse scrap = ScrapReadResponse.from(scrapService.read(scrapId));
    ResponseDto responseData = new ResponseDto(metaData, List.of(scrap));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/scraps")
  public ResponseEntity<?> readScraps(HttpServletRequest request,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false, defaultValue = "10") int size) {
    CursorResult<ScrapReadResponse> result = scrapService.readScrapsByCursor(cursor, size);
    MetaDataDto metaData = createCursorMetaData(request.getQueryString(), result.getCursor(), size,
        result.isHasNext());
    ResponseDto responseData = new ResponseDto(metaData, List.of(result.getValues()));
    return ResponseEntity.ok(responseData);
  }

  @DeleteMapping("/scrap/{scrapId}")
  @PreAuthorize("@socialSecurity.isScrapMemberProperty(#scrapId) or hasRole('ADMIN')")
  public ResponseEntity<?> cancelScrap(HttpServletRequest request,
      @PathVariable Long scrapId) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ScrapReadResponse scrap = ScrapReadResponse.from(scrapService.cancelScrap(scrapId));
    ResponseDto responseData = new ResponseDto(metaData, List.of(scrap));
    return ResponseEntity.ok(responseData);
  }
}
