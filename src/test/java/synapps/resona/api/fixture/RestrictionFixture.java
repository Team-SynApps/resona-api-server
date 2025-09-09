package synapps.resona.api.fixture;

import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.restriction.Block;
import synapps.resona.api.socialMedia.entity.restriction.CommentHide;

public class RestrictionFixture {
  public static Block createBlock(Member blocker, Member blocked) {
    return Block.of(blocker, blocked);
  }

  public static CommentHide createCommentHide(Member viewer, Comment comment) {
    return CommentHide.of(viewer, comment);
  }
}
