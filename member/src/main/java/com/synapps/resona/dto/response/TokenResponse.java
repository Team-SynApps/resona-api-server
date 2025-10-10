package com.synapps.resona.dto.response;

import com.synapps.resona.entity.token.AuthToken;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

  private AuthToken accessToken;
  private AuthToken refreshToken;
  private boolean isRegistered;
}
