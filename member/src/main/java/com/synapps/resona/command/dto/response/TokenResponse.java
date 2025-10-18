package com.synapps.resona.command.dto.response;

import com.synapps.resona.command.entity.token.AuthToken;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

  private AuthToken accessToken;
  private AuthToken refreshToken;
  private boolean isRegistered;
}
