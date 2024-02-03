package haru.harudongseon.member.domain;

import haru.harudongseon.global.BaseEntity;
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

    private String profileUrl;

    private String oauthId;

    @Enumerated(value = EnumType.STRING)
    private OauthType oauthType;

    public Member(final String email, final String nickname,
                  final String profileUrl, final String oauthId,
                  final OauthType oauthType) {
        this.email = email;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.oauthId = oauthId;
        this.oauthType = oauthType;
    }
}
