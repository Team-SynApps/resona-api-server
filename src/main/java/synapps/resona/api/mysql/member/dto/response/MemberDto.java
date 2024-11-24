package synapps.resona.api.mysql.member.dto.response;

import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.entity.personal_info.Gender;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    private Long id;
    private String nickname;
    private String phoneNumber;
    private Integer timezone;
    private Integer age;
    private String birth;
    private String comment;
    private Gender gender;
    private Boolean isOnline;
    private String email;
    private String password;
    private String location;
    private Category category;
    private String profileImageUrl;
    private ProviderType providerType;
    private RoleType roleType;
    private String createdAt;
    private String modifiedAt;
    private String lastAccessedAt;

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .timezone(member.getTimezone())
                .age(member.getAge())
                .birth(DateTimeUtil.localDateTimeToStringSimpleFormat(member.getBirth()))
                .comment(member.getComment())
                .gender(member.getGender())
                .isOnline(member.getIsOnline())
                .email(member.getEmail())
                .password(member.getPassword())
                .location(member.getLocation())
                .category(member.getCategory())
                .profileImageUrl(member.getProfileImageUrl())
                .providerType(member.getProviderType())
                .roleType(member.getRoleType())
                .createdAt(DateTimeUtil.localDateTimeToString(member.getCreatedAt()))
                .modifiedAt(DateTimeUtil.localDateTimeToString(member.getModifiedAt()))
                .lastAccessedAt(DateTimeUtil.localDateTimeToString(member.getLastAccessedAt()))
                .build();
    }
}