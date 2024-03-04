package haru.harudongseon.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record OauthLoginRequest(
        @NotBlank(message = "로그인 타입은 공백일 수 없습니다.")
        @Schema(description = "OAuth 타입", nullable = false, example = "google / naver / kakao")
        String loginType,
        @NotBlank(message = "OAuth 인증 토큰은 공백일 수 없습니다.")
        @Schema(description = "OAuth 인증 코드/토큰", nullable = false, example = "xxx.xxx.xxx")
        String token,
        @NotBlank(message = "Device ID는 공백일 수 없습니다.")
        @Schema(description = "기기 디바이스 ID", nullable = false, example = "aaaa12")
        String deviceId
) {
}
