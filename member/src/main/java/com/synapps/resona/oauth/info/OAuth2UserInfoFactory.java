package com.synapps.resona.oauth.info;

import com.synapps.resona.entity.account.ProviderType;
import com.synapps.resona.oauth.info.impl.FacebookOAuth2UserInfo;
import com.synapps.resona.oauth.info.impl.GoogleOAuth2UserInfo;
import java.util.Map;

public class OAuth2UserInfoFactory {

  public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType,
      Map<String, Object> attributes) {
    return switch (providerType) {
      case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
      case FACEBOOK -> new FacebookOAuth2UserInfo(attributes);
      default -> throw new IllegalArgumentException("Invalid Provider Type.");
    };
  }
}
