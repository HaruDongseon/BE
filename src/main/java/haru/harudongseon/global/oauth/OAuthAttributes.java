package haru.harudongseon.global.oauth;

import java.util.Map;

public class OAuthAttributes {

    private OAuth2UserInfo oAuth2UserInfo;

    private OAuthAttributes(final OAuth2UserInfo oAuth2UserInfo) {
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(LoginType oauthType, Map<String, Object> attributes) {
        if (oauthType == LoginType.NAVER) {
            final NaverOAuth2UserInfo naverOAuth2UserInfo = new NaverOAuth2UserInfo(attributes);
            return new OAuthAttributes(naverOAuth2UserInfo);
        }
        if (oauthType == LoginType.KAKAO) {
            final KakaoOAuth2UserInfo kakaoOAuth2UserInfo = new KakaoOAuth2UserInfo(attributes);
            return new OAuthAttributes(kakaoOAuth2UserInfo);
        }
        if (oauthType == LoginType.GOOGLE) {
            final GoogleOAuth2UserInfo googleOAuth2UserInfo = new GoogleOAuth2UserInfo(attributes);
            return new OAuthAttributes(googleOAuth2UserInfo);
        }
        throw new RuntimeException("Fail Authorization By Wrong Oauth Type");
    }

    public OAuth2UserInfo getOAuth2UserInfo() {
        return this.oAuth2UserInfo;
    }
}
