package com.synapps.resona.query.dto;

import com.synapps.resona.command.entity.account.AccountStatus;
import com.synapps.resona.command.entity.account.RoleType;
import com.synapps.resona.command.entity.member_details.MBTI;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import com.synapps.resona.entity.Language;
import com.synapps.resona.query.entity.MemberDocument;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDetailQueryDto {

    private final AccountInfo accountInfo;
    private final MemberDetails memberDetails;
    private final Profile profile;

    public static MemberDetailQueryDto from(MemberDocument memberDocument) {
        return MemberDetailQueryDto.builder()
            .accountInfo(AccountInfo.from(memberDocument.getAccountInfo()))
            .memberDetails(MemberDetails.from(memberDocument.getMemberDetails()))
            .profile(Profile.from(memberDocument.getProfile()))
            .build();
    }

    @Getter
    @Builder
    public static class AccountInfo {
        private final RoleType roleType;
        private final AccountStatus status;

        public static AccountInfo from(MemberDocument.AccountInfoEmbed embed) {
            return AccountInfo.builder()
                .roleType(embed.getRoleType())
                .status(embed.getStatus())
                .build();
        }
    }

    @Getter
    @Builder
    public static class MemberDetails {
        private final Integer timezone;
        private final String phoneNumber;
        private final MBTI mbti;
        private final String aboutMe;
        private final String location;
        private final List<String> hobbies;

        public static MemberDetails from(MemberDocument.MemberDetailsEmbed embed) {
            return MemberDetails.builder()
                .timezone(embed.getTimezone())
                .phoneNumber(embed.getPhoneNumber())
                .mbti(embed.getMbti())
                .aboutMe(embed.getAboutMe())
                .location(embed.getLocation())
                .hobbies(embed.getHobbies())
                .build();
        }
    }

    @Getter
    @Builder
    public static class Profile {
        private final String tag;
        private final String nickname;
        private final CountryCode nationality;
        private final CountryCode countryOfResidence;
        private final Set<Language> nativeLanguages;
        private final Set<Language> interestingLanguages;
        private final String profileImageUrl;
        private final String backgroundImageUrl;
        private final Integer age;
        private final Gender gender;
        private final String comment;

        public static Profile from(MemberDocument.ProfileEmbed embed) {
            return Profile.builder()
                .tag(embed.getTag())
                .nickname(embed.getNickname())
                .nationality(embed.getNationality())
                .countryOfResidence(embed.getCountryOfResidence())
                .nativeLanguages(embed.getNativeLanguages())
                .interestingLanguages(embed.getInterestingLanguages())
                .profileImageUrl(embed.getProfileImageUrl())
                .backgroundImageUrl(embed.getBackgroundImageUrl())
                .age(embed.getAge())
                .gender(embed.getGender())
                .comment(embed.getComment())
                .build();
        }
    }
}
