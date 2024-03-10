package haru.harudongseon.member.presentation;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.member.application.LoginService;
import haru.harudongseon.member.application.dto.OauthLoginResponse;
import haru.harudongseon.member.application.dto.OauthLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "OAuth 로그인 API")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "500", description = "로그인 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/oauth-login")
    public ResponseEntity<OauthLoginResponse> memberLogin(@Valid @RequestBody final OauthLoginRequest oauthLoginRequest) {
        final OauthLoginResponse loginResponse = loginService.oauthLogin(oauthLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
