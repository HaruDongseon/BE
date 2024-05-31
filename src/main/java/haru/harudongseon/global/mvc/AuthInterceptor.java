package haru.harudongseon.global.mvc;

import haru.harudongseon.global.jwt.JwtService;
import haru.harudongseon.global.jwt.TokenStatus;
import haru.harudongseon.member.domain.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String PREFIX_BEARER = "Bearer ";
    private static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        final String accessToken = extractAccessToken(request);
        validateToken(accessToken);
        final Long memberId = jwtService.extractMemberId(accessToken);
        validateMemberExist(memberId);

        return true;
    }

    private String extractAccessToken(final HttpServletRequest request) {
        final String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith(PREFIX_BEARER)) {
            return accessToken.substring(PREFIX_BEARER.length());
        }
        final String logMessage = "인증 실패(액세스 토큰 추출 실패) - 토큰 : " + accessToken;
        throw new AuthException.UnauthorizedException(logMessage);
    }

    private void validateToken(final String accessToken) {
        final TokenStatus tokenStatus = jwtService.validateToken(accessToken);
        if (tokenStatus != TokenStatus.VALIDATED) {
            final String logMessage = "인증 실패(잘못된 토큰) - 토큰 : " + accessToken;
            throw new AuthException.UnauthorizedException(logMessage);
        }
    }

    private void validateMemberExist(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            final String logMessage = "인증 실패(회원 ID Claim에 해당하는 회원 존재 X) - 잘못된 회원 ID : " + memberId;
            throw new AuthException.UnauthorizedException(logMessage);
        }
    }
}
