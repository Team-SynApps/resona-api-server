package com.synapps.resona.fixture;

import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.account.AccountInfo;
import com.synapps.resona.entity.account.AccountStatus;
import com.synapps.resona.entity.account.RoleType;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.entity.profile.CountryCode;
import com.synapps.resona.entity.profile.Profile;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

public class FeedMemberFixture {
  public static Member createMember(String email, String nickname) {
    AccountInfo accountInfo = AccountInfo.of(RoleType.USER, AccountStatus.ACTIVE);
    MemberDetails memberDetails = MemberDetails.empty();
    Profile profile = Profile.of(
        CountryCode.KR, CountryCode.KR, Set.of(Language.KOREAN),
        Collections.emptySet(), nickname, "tag_" + nickname, "", "2000-01-01"
    );
    return Member.of(accountInfo, memberDetails, profile, email, "password", LocalDateTime.now());
  }
}
