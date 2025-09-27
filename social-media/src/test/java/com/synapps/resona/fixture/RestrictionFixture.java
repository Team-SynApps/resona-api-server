package com.synapps.resona.fixture;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.domain.entity.restriction.Block;
import com.synapps.resona.domain.entity.restriction.CommentHide;

public class RestrictionFixture {
  public static Block createBlock(Member blocker, Member blocked) {
    return Block.of(blocker, blocked);
  }

  public static CommentHide createCommentHide(Member viewer, Comment comment) {
    return CommentHide.of(viewer, comment);
  }
}
