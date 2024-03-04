package haru.harudongseon.global.mvc;

import haru.harudongseon.global.jwt.JwtService;
import haru.harudongseon.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String JWT_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthPrincipal.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        final String authHeaderValue = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String accessToken = authHeaderValue.replace(JWT_PREFIX, "");
        final Long memberId = jwtService.extractMemberId(accessToken);

        return new AuthMemberDto(memberId);
    }
}
