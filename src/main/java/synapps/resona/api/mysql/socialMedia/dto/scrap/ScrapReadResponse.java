package synapps.resona.api.mysql.socialMedia.dto.scrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.feed.Scrap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapReadResponse {
    private Long scrapId;
    private Long feedId;
    private String createdAt;

    public static ScrapReadResponse from(Scrap scrap) {
        return ScrapReadResponse.builder()
                .scrapId(scrap.getId())
                .feedId(scrap.getFeed().getId())
                .createdAt(scrap.getCreatedAt().toString())
                .build();
    }
}
