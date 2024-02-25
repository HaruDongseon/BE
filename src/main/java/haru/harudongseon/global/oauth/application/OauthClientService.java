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

    @Value("${oauth2.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    private final RestTemplate restTemplate;


    public OauthClientService(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public OAuth2UserInfo getKakaoUserInfo(final String oauthToken) {
        final HttpEntity<String> request = generateRequest(oauthToken);
        final ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUri, HttpMethod.GET, request, Map.class);

        return generateOAuth2UserInfo(response, LoginType.KAKAO);
    }

    public OAuth2UserInfo getGoogleUserInfo(final String oauthToken) {
        final HttpEntity<String> request = generateRequest(oauthToken);
        final ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoUri, HttpMethod.GET, request, Map.class);

        return generateOAuth2UserInfo(response, LoginType.GOOGLE);
    }

    public OAuth2UserInfo getNaverUserInfo(final String oauthToken) {
        final HttpEntity<String> request = generateRequest(oauthToken);
        final ResponseEntity<Map> response = restTemplate.exchange(naverUserInfoUri, HttpMethod.GET, request, Map.class);

        return generateOAuth2UserInfo(response, LoginType.NAVER);
    }

    private HttpEntity<String> generateRequest(final String oauthToken) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, JWT_PREFIX + oauthToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        return request;
    }

    private OAuth2UserInfo generateOAuth2UserInfo(final ResponseEntity<Map> response, final LoginType naver) {
        final Map<String, Object> attributes = response.getBody();
        final OAuthAttributes oAuthAttributes = OAuthAttributes.of(naver, attributes);
        return oAuthAttributes.getOAuth2UserInfo();
    }
}
