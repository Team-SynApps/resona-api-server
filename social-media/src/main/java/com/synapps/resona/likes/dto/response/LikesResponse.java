package com.synapps.resona.likes.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "likeType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FeedLikesResponse.class, name = "FEED"),
    @JsonSubTypes.Type(value = CommentLikesResponse.class, name = "COMMENT"),
    @JsonSubTypes.Type(value = ReplyLikesResponse.class, name = "REPLY")
})
@Getter
@SuperBuilder
public abstract class LikesResponse {
  private final long likesCount;

  @JsonProperty("isLiked")
  private final boolean isLiked;
}