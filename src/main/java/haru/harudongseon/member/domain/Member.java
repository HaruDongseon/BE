package haru.harudongseon.member.domain;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.global.oauth.LoginType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String profileImageUrl;

    private String oauthId;

    private String deviceId;

    @Enumerated(value = EnumType.STRING)
    private LoginType loginType;

    public Member(final String email, final String nickname,
                  final String profileImageUrl, final String oauthId,
                  final String deviceId, final LoginType loginType) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.oauthId = oauthId;
        this.deviceId = deviceId;
        this.loginType = loginType;
    }

    public void editProfile(final String nickname, final String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
