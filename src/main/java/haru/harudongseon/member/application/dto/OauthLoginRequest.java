package haru.harudongseon.member.application.dto;

import jakarta.validation.constraints.NotBlank;

public record OauthLoginRequest(
        @NotBlank(message = "로그인 타입은 공백일 수 없습니다.")
        String loginType,
        @NotBlank(message = "OAuth 인증 토큰은 공백일 수 없습니다.")
        String token,
        @NotBlank(message = "Device ID는 공백일 수 없습니다.")
        String deviceId
) {
}
