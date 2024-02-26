package haru.harudongseon.member.presentation;

import haru.harudongseon.member.application.LoginService;
import haru.harudongseon.member.application.dto.OauthLoginResponse;
import haru.harudongseon.member.application.dto.OauthLoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/oauth-login")
    public ResponseEntity<OauthLoginResponse> memberLogin(@Valid @RequestBody final OauthLoginRequest oauthLoginRequest) {
        final OauthLoginResponse loginResponse = loginService.oauthLogin(oauthLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
