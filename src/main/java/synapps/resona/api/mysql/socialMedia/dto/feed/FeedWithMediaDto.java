package synapps.resona.api.mysql.socialMedia.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedWithMediaDto {
    private Long feedId;
    private String content;
    private int likeCount;
    private List<FeedMediaDto> images;
}
