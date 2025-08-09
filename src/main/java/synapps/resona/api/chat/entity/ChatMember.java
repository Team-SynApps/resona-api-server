package synapps.resona.api.chat.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.profile.Profile;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "members")
public class ChatMember {

  @Id
  private Long id;

  private String nickname;

  private String profileImageUrl;

  private ChatMember(Long id, String nickname, String profileImageUrl) {
    this.id = id;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
  }

  public static ChatMember of(Long memberId, String nickname, String profileImageUrl) {
    return new ChatMember(memberId, nickname, profileImageUrl);
  }

  public static ChatMember from(Long memberId, Profile profile) {
    return new ChatMember(memberId, profile.getNickname(), profile.getProfileImageUrl());
  }

  public void updateProfile(String nickname, String profileImageUrl) {
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
  }
}