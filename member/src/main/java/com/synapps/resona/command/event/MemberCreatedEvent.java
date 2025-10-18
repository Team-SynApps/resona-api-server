package com.synapps.resona.command.event;

import com.synapps.resona.command.entity.account.AccountStatus;
import com.synapps.resona.command.entity.account.ProviderType;
import com.synapps.resona.command.entity.account.RoleType;
import com.synapps.resona.command.entity.member_details.MBTI;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import com.synapps.resona.entity.Language;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MemberCreatedEvent(
    Long memberId,
    String email,
    AccountInfo accountInfo,
    MemberDetailsInfo memberDetailsInfo,
    ProfileInfo profileInfo,
    List<ProviderType> providers,
    LocalDateTime lastAccessedAt
) {

    public record AccountInfo(RoleType roleType, AccountStatus status) {}

    public record MemberDetailsInfo(Integer timezone, String phoneNumber, MBTI mbti, String aboutMe,
                                    String location, List<String> hobbies) {}

    public record ProfileInfo(String tag, String nickname, CountryCode nationality,
                              CountryCode countryOfResidence, Set<Language> nativeLanguages,
                              Set<Language> interestingLanguages, String profileImageUrl,
                              String backgroundImageUrl, Integer age, Gender gender,
                              String comment) {}
}