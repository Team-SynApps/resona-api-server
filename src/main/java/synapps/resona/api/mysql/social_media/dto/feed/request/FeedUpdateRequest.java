package synapps.resona.api.mysql.social_media.dto.feed.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedUpdateRequest {
    private String content;
}
