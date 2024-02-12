package haru.harudongseon.member.domain;

import java.util.Optional;

import haru.harudongseon.global.oauth.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthIdAndLoginType(final Long oauthId, final LoginType loginType);
}
