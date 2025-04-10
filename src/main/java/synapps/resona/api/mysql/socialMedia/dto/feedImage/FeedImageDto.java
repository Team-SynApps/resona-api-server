package synapps.resona.api.mysql.socialMedia.dto.feedImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.feedMedia.FeedMedia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedImageDto {
    private String url;
    private int index;

    public static FeedImageDto from(FeedMedia media) {
        return FeedImageDto.builder()
                .index(media.getIndex())
                .url(media.getUrl())
                .build();
    }
}