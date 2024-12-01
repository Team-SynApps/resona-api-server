package synapps.resona.api.mysql.social_media.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedImageUpdateRequest {
    private Long FeedImageId;
    private String url;
}
