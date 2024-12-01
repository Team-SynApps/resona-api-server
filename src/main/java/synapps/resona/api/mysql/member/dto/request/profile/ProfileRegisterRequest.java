package synapps.resona.api.mysql.member.dto.request.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProfileRegisterRequest {

    @NotNull
    private Long memberId; // Member와 매핑할 ID

    @NotBlank
    @Size(max = 15)
    private String nickname;

    private List<String> usingLanguages;

    @Size(max = 512)
    private String profileImageUrl;

    @Size(max = 512)
    private String backgroundImageUrl;

    private String mbti;

    @Size(max = 512)
    private String comment;

    private String aboutMe;
}
