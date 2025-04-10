package synapps.resona.api.mysql.socialMedia.dto.feed.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.feedImage.FeedImageDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedResponse {
    private String id;
    private String content;
    private List<FeedImageDto> feedImageDtos;
    private String createdAt;
}
