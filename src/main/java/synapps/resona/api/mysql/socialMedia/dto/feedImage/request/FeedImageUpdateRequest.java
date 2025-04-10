package synapps.resona.api.mysql.socialMedia.dto.feedImage.request;

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
