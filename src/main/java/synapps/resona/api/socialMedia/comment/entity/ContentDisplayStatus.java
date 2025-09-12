package synapps.resona.api.socialMedia.comment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentDisplayStatus {

  NORMAL("Normal"),
  DELETED("Content deleted."),
  HIDDEN("Content hidden"),
  BLOCKED("Content Blocked");

  private final String description;
}
