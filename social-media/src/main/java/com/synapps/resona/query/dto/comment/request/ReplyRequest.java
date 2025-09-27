package com.synapps.resona.query.dto.comment.request;

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
}
