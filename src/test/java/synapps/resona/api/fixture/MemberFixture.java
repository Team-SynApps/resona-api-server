package synapps.resona.api.fixture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.member.dto.MemberDto;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.account.AccountStatus;
import synapps.resona.api.member.entity.account.RoleType;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MBTI;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.member.entity.profile.Gender;
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

  public static RegisterRequest createDefaultRegisterRequest() {
    return new RegisterRequest(
        "test@example.com",
        "testTag",
        "validPassword123!",
        CountryCode.KR,
        CountryCode.US,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        9,
        LocalDate.of(1995, 5, 10).format(DateTimeFormatter.ISO_LOCAL_DATE),
        "tester",
        "http://example.com/profile.jpg",
        false // isSocialLogin
    );
  }

  public static RegisterRequest createMeRegisterRequest() {
    return new RegisterRequest(
        "me@resona.com", "mine_tag", "secure123!", CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH), 9, "1998-07-21",
        "나자신", "http://image.me", false
    );
  }

  public static RegisterRequest createTargetRegisterRequest() {
    return new RegisterRequest(
        "target@resona.com", "other_tag", "secure123!", CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH), 7, "1995-01-01",
        "상대방", "http://image.you", false
    );
  }

  public static RegisterRequest createAnotherRegisterRequest() {
    return new RegisterRequest(
        "another@resona.com", "another_tag", "Qwerty12345!@", CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH), 1, "2000-01-01",
        "제3자", "http://img.third", false
    );
  }

  public static Member createTestMember() {
    AccountInfo accountInfo = AccountInfo.of(RoleType.USER, AccountStatus.ACTIVE);
    return Member.of(
        accountInfo, MemberDetails.empty(), Profile.empty(),
        "test1@example.com", "password1234", LocalDateTime.now()
    );
  }

  public static Member createGuestMember() {
    AccountInfo tempAccountInfo = AccountInfo.of(RoleType.GUEST, AccountStatus.TEMPORARY);
    return Member.of(
        tempAccountInfo, MemberDetails.empty(), Profile.empty(),
        "newuser1@example.com", "Newpass1@", LocalDateTime.now()
    );
  }

  public static RegisterRequest createNewUserRegisterRequest() {
    return new RegisterRequest(
        "newuser1@example.com", "newuser_tag", "Newpass1@",
        CountryCode.KR, CountryCode.US, Set.of(Language.KOREAN), Set.of(Language.ENGLISH),
        9, "1990-01-01", "newuser", "http://example.com/profile.jpg", false
    );
  }

  public static RegisterRequest createMongoSyncRegisterRequest() {
    return new RegisterRequest(
        "newuser1@example.com", "newuser_tag", "Newpass1@",
        CountryCode.KR, CountryCode.US, Set.of(Language.KOREAN), Set.of(Language.ENGLISH),
        9, "1990-01-01", "MongoDB동기화테스트", "http://example.com/profile.jpg", false
    );
  }

  public static Member createProfileTestMember(String email) {
    AccountInfo accountInfo = AccountInfo.of(RoleType.GUEST, AccountStatus.TEMPORARY);
    return Member.of(
        accountInfo, MemberDetails.empty(), Profile.empty(),
        email, "secure123!", LocalDateTime.now()
    );
  }

  public static RegisterRequest createProfileTestRegisterRequest(String email) {
    return new RegisterRequest(
        email, "test_tag", "secure123!", CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH), 9, "1998-07-21",
        "테스트닉네임", "http://image.png", false
    );
  }

  public static ProfileRequest createProfileRegisterRequest() {
    return new ProfileRequest(
        "등록된닉네임", CountryCode.KR, CountryCode.KR, Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH), "http://new.profile", "http://new.bg",
        "1998-07-21", Gender.MAN, "등록 테스트용 자기소개입니다."
    );
  }

  public static ProfileRequest createProfileReadRequest() {
    return new ProfileRequest(
        "조회용닉네임", CountryCode.KR, CountryCode.KR, Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH), "http://profile.img", "http://background.img",
        "1998-07-21", Gender.WOMAN, "조회 테스트용 자기소개"
    );
  }

  public static ProfileRequest createProfileUpdateRequest() {
    return new ProfileRequest(
        "수정된닉네임", CountryCode.JP, CountryCode.US, Set.of(Language.JAPANESE),
        Set.of(Language.ENGLISH, Language.FRENCH), "http://updated.img", "http://updated.bg",
        "1995-12-31", Gender.MAN, "수정된 소개입니다."
    );
  }

  public static MemberDto createTestMemberDto(Long id, String email) {
    return MemberDto.of(id, email);
  }

  public static RegisterRequest createDetailsTestRegisterRequest(String email) {
    return new RegisterRequest(
        email, "test_tag", "secure123!", CountryCode.KR, CountryCode.KR,
        new HashSet<>(Set.of(Language.KOREAN)), new HashSet<>(Set.of(Language.ENGLISH)),
        9, "1998-07-21", "테스트닉네임", "http://image.png", false
    );
  }

  public static MemberDetailsRequest createDetailsRegisterRequest() {
    return new MemberDetailsRequest(9, "010-1234-5678", MBTI.ENFJ, "자기소개입니다", "서울 강남구");
  }

  public static MemberDetailsRequest createDetailsReadRequest() {
    return new MemberDetailsRequest(8, "010-2222-3333", MBTI.INFP, "소개글", "부산 해운대구");
  }

  public static MemberDetailsRequest createDetailsInitialRequest() {
    return new MemberDetailsRequest(9, "010-1111-2222", MBTI.ENFJ, "초기 소개", "대전 중구");
  }

  public static MemberDetailsRequest createDetailsUpdateRequest() {
    return new MemberDetailsRequest(7, "010-9999-8888", MBTI.ENTP, "수정된 소개", "제주도 제주시");
  }

  public static MemberDetailsRequest createDetailsDeleteRequest() {
    return new MemberDetailsRequest(9, "010-0000-0000", MBTI.ISFJ, "삭제용 소개", "인천 연수구");
  }

  public static Member createCustomMember(String email, String nickname) {
    AccountInfo accountInfo = AccountInfo.empty();
    MemberDetails memberDetails = MemberDetails.of(0, "01011111111", MBTI.ENFJ, "about me", "location");

    String tag = email.substring(0, email.indexOf('@')) + "_tag";
    Profile profile = Profile.of(
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        nickname,
        tag,
        "http://profile.img/" + nickname,
        "2000-01-01"
    );
    return Member.of(accountInfo, memberDetails, profile, email, "password", LocalDateTime.now());
  }

  public static Member createMemberForRepositoryTest(String email) {
    AccountInfo accountInfo = AccountInfo.empty();
    MemberDetails memberDetails = MemberDetails.of(0, "01011111111", MBTI.ENFJ, "test about me", "test location");
    Profile profile = Profile.of(
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        "닉네임3",
        "tag",
        "http://profile.img/3",
        "2000-01-01"
    );
    return Member.of(accountInfo, memberDetails, profile, email, "password123", LocalDateTime.now());
  }

}
