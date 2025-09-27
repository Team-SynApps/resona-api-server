package com.synapps.resona.query.dto.likes.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CommentLikesResponse extends LikesResponse {
  private final Long commentId;

  public static CommentLikesResponse of(Long commentId, long likesCount, boolean isLiked) {
    return CommentLikesResponse.builder()
        .commentId(commentId)
        .likesCount(likesCount)
        .isLiked(isLiked)
        .build();
  }
}