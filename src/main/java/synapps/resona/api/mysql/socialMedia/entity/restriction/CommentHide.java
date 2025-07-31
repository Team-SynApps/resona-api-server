package synapps.resona.api.mysql.socialMedia.entity.restriction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("COMMENT")
public class CommentHide extends Hide {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  private CommentHide(Member member, Comment comment) {
    this.setMember(member);
    this.comment = comment;
  }

  /**
   * CommentHide 엔티티를 생성하는 정적 팩토리 메서드.
   * @param member 숨김 처리한 사용자
   * @param comment 숨김 대상 댓글
   * @return 생성된 CommentHide 객체
   */
  public static CommentHide of(Member member, Comment comment) {
    return new CommentHide(member, comment);
  }
}