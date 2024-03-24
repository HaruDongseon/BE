package haru.harudongseon.searchplace.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SearchedPlaceAddRequest(
        @NotBlank(message = "검색어는 공백일 수 없습니다.")
        @Schema(description = "검색한 장소 키워드", nullable = false, example = "스타벅스")
        String keyword
) {
}
