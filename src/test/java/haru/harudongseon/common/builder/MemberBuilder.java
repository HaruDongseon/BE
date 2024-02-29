package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.member.MemberFixtures.*;

import haru.harudongseon.global.oauth.LoginType;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberBuilder {

    @Autowired
    private MemberRepository memberRepository;

    private String email;
    private String nickname;
    private String profileImageUrl;
    private String oauthId;
    private String deviceId;
    private LoginType loginType;

    public MemberBuilder defaultMember() {
        this.email = 성하_이메일;
        this.nickname = 성하_닉네임;
        this.profileImageUrl = 성하_프로필_이미지_URL;
        this.oauthId = 성하_OAUTH_ID;
        this.deviceId = 성하_DEVICE_ID;
        this.loginType = 성하_LOGIN_TYPE;

        return this;
    }

    public MemberBuilder email(final String email) {
        this.email = email;
        return this;
    }

    public MemberBuilder nickname(final String nickname) {
        this.nickname = nickname;
        return this;
    }

    public MemberBuilder profileImageUrl(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public MemberBuilder oauthId(final String oauthId) {
        this.oauthId = oauthId;
        return this;
    }

    public MemberBuilder deviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public MemberBuilder loginType(final LoginType loginType) {
        this.loginType = loginType;
        return this;
    }

    public Member build() {
        final Member member = new Member(email, nickname, profileImageUrl, oauthId, deviceId, loginType);
        return memberRepository.save(member);
    }

}
