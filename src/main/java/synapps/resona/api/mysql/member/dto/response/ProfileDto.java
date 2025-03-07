package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Gender;
import synapps.resona.api.mysql.member.entity.profile.Language;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ProfileDto {
    private Long id;
    private Long memberId;
    private String tag;
    private String nickname;
    private String nationality;
    private String countryOfResidence;
    private List<String> nativeLanguages;
    private List<String> interestingLanguages;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String comment;
    private Integer age;
    private String birth;
    private String gender;
}