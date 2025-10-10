package com.synapps.resona.dto.request.member_details;

import com.synapps.resona.entity.member_details.MBTI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailsRequest {

  private Integer timezone;

  @NotBlank
  @Size(max = 20)
  private String phoneNumber;

  private MBTI mbti;

  private String aboutMe;

  private String location;
}