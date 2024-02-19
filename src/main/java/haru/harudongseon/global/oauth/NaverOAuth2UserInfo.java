package haru.harudongseon.global.oauth;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(final Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        final Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("email");
    }

    @Override
    public String getNickname() {
        final Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("nickname");
    }

    @Override
    public String getProfileUrl() {
        final Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("profile_image");
    }

    @Override
    public String getOauthId() {
        final Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("id");
    }

    public LoginType getOauthType() { return LoginType.NAVER; }
}
