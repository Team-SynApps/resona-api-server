package synapps.resona.api.mysql.social_media.dto.feed.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.social_media.dto.feed.FeedImageDto;
import synapps.resona.api.mysql.social_media.entity.Feed;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedReadResponse {
    private String id;
    private String content;
    private List<FeedImageDto> feedImageDtos;

    public static FeedReadResponse from(Feed feed) {
        return FeedReadResponse.builder()
                .id(feed.getId().toString())
                .content(feed.getContent())
                .feedImageDtos(feed.getImages().stream().map(media -> FeedImageDto.from(media)
                ).toList())
                .build();
    }
}
