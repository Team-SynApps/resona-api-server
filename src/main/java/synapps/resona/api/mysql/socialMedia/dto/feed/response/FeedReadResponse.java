package synapps.resona.api.mysql.socialMedia.dto.feed.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.feed.FeedImageDto;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedReadResponse {
    private String id;
    private String content;
    private String createdAt;
    private List<FeedImageDto> feedImageDtos;

    public static FeedReadResponse from(Feed feed) {
        return FeedReadResponse.builder()
                .id(feed.getId().toString())
                .content(feed.getContent())
                .createdAt(feed.getCreatedAt().toString())
                .feedImageDtos(feed.getImages().stream().map(media -> FeedImageDto.from(media)
                ).toList())
                .build();
    }
}
