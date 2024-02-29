package haru.harudongseon.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OauthLoginResponse(
        @Schema(description = "자체 Access Token", nullable = false, example = "xxx.xxx.xxx")
        String accessToken,
        @Schema(description = "회원가입(첫 로그인) / 로그인 판단 플래그", nullable = false, example = "true")
        Boolean isNewMember
) {
}
