package haru.harudongseon.member.application.dto;

import jakarta.validation.constraints.NotBlank;

public record MyProfileEditRequest(
        @NotBlank(message = "수정할 닉네임은 공백일 수 없습니다. 변경하지 않으려면 기존 닉네임을 입력하세요.")
        String nickname,

        @NotBlank(message = "수정할 프로필 이미지 URL은 공백일 수 없습니다. 변경하지 않으려면 기존 프로필 이미지 URL을 입력하세요.")
        String profileImageUrl
) {
}
