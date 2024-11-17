package synapps.resona.api.oauth.info.impl;

import synapps.resona.api.oauth.info.OAuth2UserInfo;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        // Apple OAuth2의 사용자 ID는 "sub" 필드에 포함됩니다.
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        // Apple OAuth2에서는 이름 정보가 optional이므로 기본값 처리 필요
        Map<String, String> name = (Map<String, String>) attributes.get("name");
        if (name != null) {
            return name.getOrDefault("firstName", "") + " " + name.getOrDefault("lastName", "");
        }
        return "Unknown";
    }

    @Override
    public String getEmail() {
        // Apple OAuth2의 이메일 정보
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        // Apple은 프로필 이미지를 제공하지 않음
        return null;
    }
}
