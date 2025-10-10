package com.synapps.resona.comment.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

  private Long feedId;
  private String content;
  private String languageCode;
  private List<Long> mentionMemberIds;
}
