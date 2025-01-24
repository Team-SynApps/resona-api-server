package synapps.resona.api.mysql.social_media.dto.feed.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.social_media.dto.feed.FeedImageDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedPostResponse {
    private String id;
    private String content;
    private List<FeedImageDto> feedImageDtos;
    private String createdAt;
}
