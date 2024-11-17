package synapps.resona.api.oauth.info;

import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.info.impl.AppleOAuth2UserInfo;
import synapps.resona.api.oauth.info.impl.FacebookOAuth2UserInfo;
import synapps.resona.api.oauth.info.impl.GoogleOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        return switch (providerType) {
            case APPLE -> new AppleOAuth2UserInfo(attributes);
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case FACEBOOK -> new FacebookOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("Invalid Provider Type.");
        };
    }
}
