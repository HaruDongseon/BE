package haru.harudongseon.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record MyProfileEditRequest(
        @Schema(description = "수정할 닉네임(한글, 영문, 숫자, 띄어쓰기 포함 3~10자)", nullable = false, example = "SUPER SEONGHA")
        @NotBlank(message = "수정할 닉네임은 공백일 수 없습니다. 변경하지 않으려면 기존 닉네임을 입력하세요.")
        @Pattern(regexp = "^[a-zA-Z가-힣0-9\\\\s]{3,10}$", message = "수정할 닉네임은 한글, 영문, 숫자, 띄어쓰기 포함 3~10자 여야합니다.")
        String nickname,

        @Schema(description = "수정할 프로필 이미지 URL", nullable = false, example = "https://lh3.googleusercontent.com/a/xxx")
        @NotBlank(message = "수정할 프로필 이미지 URL은 공백일 수 없습니다. 변경하지 않으려면 기존 프로필 이미지 URL을 입력하세요.")
        @URL(message = "수정할 프로필 이미지 URL은 URL 형식이어야합니다.")
        String profileImageUrl
) {
}
