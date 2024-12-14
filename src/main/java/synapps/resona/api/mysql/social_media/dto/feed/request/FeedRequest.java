package synapps.resona.api.mysql.social_media.dto.feed.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.social_media.dto.feed.ContentDeserializer;
import synapps.resona.api.mysql.social_media.dto.location.LocationRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequest {
    @JsonDeserialize(using = ContentDeserializer.class)
    private String content;
    private String category;
    private LocationRequest location;
}
