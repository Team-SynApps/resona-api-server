package synapps.resona.api.mysql.social_media.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedImageRequest {
    private Long feedId;
    private String url;
}
