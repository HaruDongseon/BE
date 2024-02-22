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

    // TODO : 로그인 시 생성한 Access Token 어떻게 내려줄지 프론트와 논의 (우선 String 그대로 Access 토큰 반환)
    // TODO : Refresh Token도 추후에 생각
    public OauthLoginResponse oauthLogin(final OauthLoginRequest request) {
        final String loginType = request.loginType();
        final String token = request.token();
        final String deviceId = request.deviceId();

        if (loginType.equals("kakao")) {
            final OAuth2UserInfo kakaoUserInfo = oauthClientService.getKakaoUserInfo(token);
            return new OauthLoginResponse(createAccessTokenByMemberPresent(kakaoUserInfo, deviceId));
        }
        if (loginType.equals("google")) {
            final OAuth2UserInfo googleUserInfo = oauthClientService.getGoogleUserInfo(token);
            return new OauthLoginResponse(createAccessTokenByMemberPresent(googleUserInfo, deviceId));
        }
        if (loginType.equals("naver")) {
            final OAuth2UserInfo naverUserInfo = oauthClientService.getNaverUserInfo(token);
            return new OauthLoginResponse(createAccessTokenByMemberPresent(naverUserInfo, deviceId));
        }

        return null;
    }

    private String createAccessTokenByMemberPresent(final OAuth2UserInfo oauth2UserInfo, final String deviceId) {
        final String oauthId = oauth2UserInfo.getOauthId();
        final LoginType oauthType = oauth2UserInfo.getOauthType();
        final Optional<Member> optionalMember = memberRepository.findByOauthIdAndLoginType(oauthId, oauthType);

        if (optionalMember.isEmpty()) {
            final String email = oauth2UserInfo.getEmail();
            final String nickname = oauth2UserInfo.getNickname();
            final String profileUrl = oauth2UserInfo.getProfileUrl();
            final Member member = new Member(email, nickname, profileUrl, oauthId, deviceId, oauthType);
            final Member savedMember = memberRepository.save(member);

            return jwtService.createAccessToken(savedMember.getId());
        }

        final Member findMember = optionalMember.get();
        return jwtService.createAccessToken(findMember.getId());
    }
}
