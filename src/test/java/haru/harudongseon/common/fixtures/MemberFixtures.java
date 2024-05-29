package haru.harudongseon.common.fixtures;

import haru.harudongseon.global.oauth.LoginType;
import haru.harudongseon.member.domain.Member;

public class MemberFixtures {

    /**
     * 기본 멤버 (성하)
     */
    public static final String 기본_이메일 = "seongha@gmail.com";
    public static final String 기본_닉네임 = "SEONGHA";
    public static final String 기본_프로필_이미지_URL = "https://lh3.googleusercontent.com/a/xxx";
    public static final String 기본_OAUTH_ID = "a12345";
    public static final String 기본_DEVICE_ID = "abc1234";
    public static final LoginType 기본_LOGIN_TYPE = LoginType.NAVER;

    public static Member 기본_회원_엔티티() {
        return new Member(기본_이메일, 기본_닉네임, 기본_프로필_이미지_URL, 기본_OAUTH_ID, 기본_DEVICE_ID, 기본_LOGIN_TYPE);
    }

    public static Member 기본_회원_도메인(final Long id) {
        return new Member(id, 기본_이메일, 기본_닉네임, 기본_프로필_이미지_URL, 기본_OAUTH_ID, 기본_DEVICE_ID, 기본_LOGIN_TYPE);
    }
}
