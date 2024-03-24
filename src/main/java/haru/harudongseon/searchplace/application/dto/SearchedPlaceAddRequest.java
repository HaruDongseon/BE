package haru.harudongseon.searchplace.application.dto;

import jakarta.validation.constraints.NotBlank;

public record SearchedPlaceAddRequest(
        @NotBlank(message = "검색어는 공백일 수 없습니다.")
        String keyword
) {
}
