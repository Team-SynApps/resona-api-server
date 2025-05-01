package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.feedComplaint.FeedComplaintRequest;
import synapps.resona.api.mysql.socialMedia.entity.feedComplaint.FeedComplaint;
import synapps.resona.api.mysql.socialMedia.service.FeedComplaintService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedComplaintsController {

  private final FeedComplaintService feedComplaintService;
  private final ServerInfoConfig serverInfo;

  private MetaDataDto createSuccessMetaData(String queryString) {
    return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  @PostMapping("/feed/{feedId}/complaint")
  public ResponseEntity<?> reportFeed(HttpServletRequest request,
      @PathVariable Long feedId,
      @Valid @RequestBody FeedComplaintRequest complaintRequest) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    FeedComplaint complaint = feedComplaintService.reportFeed(feedId, complaintRequest);
    ResponseDto responseData = new ResponseDto(metaData, List.of("reported"));
    return ResponseEntity.ok(responseData);
  }
}
