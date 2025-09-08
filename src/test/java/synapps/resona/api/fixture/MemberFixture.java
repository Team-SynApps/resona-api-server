package synapps.resona.api.fixture;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.account.AccountStatus;
import synapps.resona.api.member.entity.account.RoleType;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.member.entity.profile.Profile;

public class MemberFixture {
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
