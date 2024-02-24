package haru.harudongseon.member.presentation;

import haru.harudongseon.member.application.LoginService;
import haru.harudongseon.member.application.dto.OauthLoginResponse;
import haru.harudongseon.member.application.dto.OauthLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    // TODO : 로그인 시 생성한 Access Token 어떻게 내려줄지 프론트와 논의 (우선 String 그대로 Access 토큰 반환)
    @PostMapping("/oauth-login")
    public ResponseEntity<OauthLoginResponse> memberLogin(@RequestBody final OauthLoginRequest oauthLoginRequest) {
        final OauthLoginResponse loginResponse = loginService.oauthLogin(oauthLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
