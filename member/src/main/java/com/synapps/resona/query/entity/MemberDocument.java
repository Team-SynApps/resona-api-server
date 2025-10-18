package com.synapps.resona.query.entity;

import com.synapps.resona.command.entity.account.AccountStatus;
import com.synapps.resona.command.entity.account.ProviderType;
import com.synapps.resona.command.entity.account.RoleType;
import com.synapps.resona.command.entity.member_details.MBTI;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import com.synapps.resona.entity.BaseDocument;
import com.synapps.resona.entity.Language;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@SuperBuilder
@Document(collection = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDocument extends BaseDocument {

    @Id
    @Field("_id")
    private Long id;

    private String email;

    private AccountInfoEmbed accountInfo;
    private MemberDetailsEmbed memberDetails;
    private ProfileEmbed profile;

    private List<ProviderType> providers;

    private LocalDateTime lastAccessedAt;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AccountInfoEmbed {
        private RoleType roleType;
        private AccountStatus status;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberDetailsEmbed {
        private Integer timezone;
        private String phoneNumber;
        private MBTI mbti;
        private String aboutMe;
        private String location;
        private List<String> hobbies;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProfileEmbed {
        private String tag;
        private String nickname;
        private CountryCode nationality;
        private CountryCode countryOfResidence;
        private Set<Language> nativeLanguages;
        private Set<Language> interestingLanguages;
        private String profileImageUrl;
        private String backgroundImageUrl;
        private Integer age;
        private Gender gender;
        private String comment;
    }
}
