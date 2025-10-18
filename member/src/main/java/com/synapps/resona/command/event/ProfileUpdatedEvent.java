package com.synapps.resona.command.event;

import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import com.synapps.resona.entity.Language;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileUpdatedEvent {

    private final Long memberId;
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
}
