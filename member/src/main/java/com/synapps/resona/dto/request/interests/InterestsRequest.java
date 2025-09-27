package com.synapps.resona.dto.request.interests;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestsRequest {

  private Long memberId;
  private List<String> interestedLanguages;
  private List<String> hobbies;
}
