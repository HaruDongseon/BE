package haru.harudongseon.member.application;

import java.util.Optional;

import haru.harudongseon.global.jwt.JwtService;
import haru.harudongseon.global.oauth.LoginType;
import haru.harudongseon.global.oauth.OAuth2UserInfo;
import haru.harudongseon.global.oauth.application.OauthClientService;
import haru.harudongseon.member.application.dto.OauthLoginRequest;
import haru.harudongseon.member.application.dto.OauthLoginResponse;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final OauthClientService oauthClientService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    // TODO : Refresh Token는 추후에 생각
    public OauthLoginResponse oauthLogin(final OauthLoginRequest request) {
        final String loginType = request.loginType();
        final String token = request.token();
        final String deviceId = request.deviceId();

        if (loginType.equals("kakao")) {
            final OAuth2UserInfo kakaoUserInfo = oauthClientService.getKakaoUserInfo(token);
            return createResponseByMemberPresent(kakaoUserInfo, deviceId);
        }
        if (loginType.equals("naver")) {
            final OAuth2UserInfo naverUserInfo = oauthClientService.getNaverUserInfo(token);
            return createResponseByMemberPresent(naverUserInfo, deviceId);
        }
        if (loginType.equals("google")) {
            final OAuth2UserInfo googleUserInfo = oauthClientService.getGoogleUserInfo(token);
            return createResponseByMemberPresent(googleUserInfo, deviceId);
        }
        throw new IllegalArgumentException("OAuth 타입이 적절하지 않습니다.");
    }

    private OauthLoginResponse createResponseByMemberPresent(final OAuth2UserInfo oauth2UserInfo, final String deviceId) {
        final String oauthId = oauth2UserInfo.getOauthId();
        final LoginType oauthType = oauth2UserInfo.getOauthType();
        final Optional<Member> optionalMember = memberRepository.findByOauthIdAndLoginType(oauthId, oauthType);

        if (optionalMember.isEmpty()) {
            final String email = oauth2UserInfo.getEmail();
            final String nickname = oauth2UserInfo.getNickname();
            final String profileUrl = oauth2UserInfo.getProfileImageUrl();
            final Member member = new Member(email, nickname, profileUrl, oauthId, deviceId, oauthType);
            final Member savedMember = memberRepository.save(member);

            final String accessToken = jwtService.createAccessToken(savedMember.getId());
            return new OauthLoginResponse(accessToken, true);
        }

        final Member findMember = optionalMember.get();
        final String accessToken = jwtService.createAccessToken(findMember.getId());
        return new OauthLoginResponse(accessToken, false);
    }
}
