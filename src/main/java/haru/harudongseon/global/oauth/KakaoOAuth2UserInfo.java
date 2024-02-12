package haru.harudongseon.global.oauth;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(final Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        final Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getNickname() {
        final Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        final Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return (String) profile.get("nickname");
    }

    @Override
    public String getProfileUrl() {
        final Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        final Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return (String) profile.get("profile_image_url");
    }

    @Override
    public Long getOauthId() {
        return (Long) attributes.get("id");
    }

    public LoginType getOauthType() { return LoginType.KAKAO; }
}
