package haru.harudongseon.global.oauth;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getEmail();
    public abstract String getNickname();
    public abstract String getProfileUrl();
    public abstract Long getOauthId();
    public abstract LoginType getOauthType();
}
