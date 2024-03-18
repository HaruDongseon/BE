package haru.harudongseon.global.jwt;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtService {

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String MEMBER_ID_CLAIM = "memberId";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    public String createAccessToken(final Long memberId) {
        return this.createToken(memberId, ACCESS_TOKEN_SUBJECT, accessExpiration);
    }

    public String createRefreshToken(final Long memberId) {
        return this.createToken(memberId, REFRESH_TOKEN_SUBJECT, refreshExpiration);
    }

    private String createToken(final Long memberId, final String subject, final Long expiration) {
        Date now = new Date();
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(now.getTime() + expiration))
                .withClaim(MEMBER_ID_CLAIM, memberId)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public Long extractMemberId(final String token) {
        return JWT.decode(token).getClaim(MEMBER_ID_CLAIM).asLong();
    }

    public TokenStatus validateToken(final String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        } catch (TokenExpiredException e) {
            return TokenStatus.EXPIRED;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return TokenStatus.NOT_VALIDATED;
        }

        return TokenStatus.VALIDATED;
    }
}
