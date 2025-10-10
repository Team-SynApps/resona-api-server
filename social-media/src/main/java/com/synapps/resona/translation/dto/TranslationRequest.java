package com.synapps.resona.translation.dto;

import com.synapps.resona.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequest {
  private String text;
  private String lang;
}