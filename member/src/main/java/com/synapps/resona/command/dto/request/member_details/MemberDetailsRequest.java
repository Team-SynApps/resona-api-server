package com.synapps.resona.command.dto.request.member_details;

import com.synapps.resona.command.entity.member_details.MBTI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
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
  private Set<String> hobbies;
}