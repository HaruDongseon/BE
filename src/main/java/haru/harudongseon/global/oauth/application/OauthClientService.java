package haru.harudongseon.global.oauth.application;

import java.util.Map;

import haru.harudongseon.global.oauth.LoginType;
import haru.harudongseon.global.oauth.OAuth2UserInfo;
import haru.harudongseon.global.oauth.OAuthAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OauthClientService {

    private static final String JWT_PREFIX = "Bearer ";

    @Value("${oauth2.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${oauth2.provider.google.user-info-uri}")
    private String googleUserInfoUri;

    private final RestTemplate restTemplate;


    public OauthClientService(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public OAuth2UserInfo getKakaoUserInfo(final String oauthToken) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, JWT_PREFIX + oauthToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        final ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUri, HttpMethod.GET, request, Map.class);

        final Map<String, Object> attributes = response.getBody();

        final OAuthAttributes oAuthAttributes = OAuthAttributes.of(LoginType.KAKAO, attributes);
        return oAuthAttributes.getOAuth2UserInfo();
    }

    public OAuth2UserInfo getGoogleUserInfo(final String oauthToken) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, JWT_PREFIX + oauthToken);
        final HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        final ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoUri, HttpMethod.GET, request, Map.class);

        final Map<String, Object> attributes = response.getBody();
        final OAuthAttributes oAuthAttributes = OAuthAttributes.of(LoginType.GOOGLE, attributes);
        return oAuthAttributes.getOAuth2UserInfo();
    }
}
