package synapps.resona.api.mysql.socialMedia.dto.feedComplaint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.feedComplaint.Complains;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedComplaintRequest {
    private Complains complains;
    private boolean isBlocked;
}
