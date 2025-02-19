package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class MemberDetailInfoDto {
    // Account Info
    private String roleType;
    private String accountStatus;
    private String lastAccessedAt;
    private String providerType;

    // Personal Info
    private String nationality;
    private String countryOfResidence;
    private String phoneNumber;
    private Integer timezone;
    private String birth;
    private Integer age;
    private String gender;
    private String location;

    // Profile
    private String nickname;
    private String tag;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String mbti;
    private String aboutMe;
    private String comment;
}
