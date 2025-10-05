package com.synapps.resona.comment.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyRequest {

  private Long commentId;
  private String languageCode;
  private String content;
  private List<Long> mentionMemberIds;
}