package haru.harudongseon.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyProfileResponse(
        @Schema(description = "회원 이메일", example = "seongha@gmail.com")
        String email,
        @Schema(description = "회원 닉네임", example = "seongha")
        String nickname,
        @Schema(description = "회원 프로필 이미지 URL", example = "https://lh3.googleusercontent.com/a/xxx")
        String profileImageUrl
) {
}
