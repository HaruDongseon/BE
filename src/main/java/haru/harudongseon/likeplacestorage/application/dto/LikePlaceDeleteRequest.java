package haru.harudongseon.likeplacestorage.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LikePlaceDeleteRequest(

        @NotBlank(message = "장소 보관함 ID는 공백일 수 없습니다.")
        @Schema(description = "장소 보관함 ID", nullable = false, example = "1")
        Long likePlaceStorageId,

        @NotBlank(message = "보관 장소 ID는 공백일 수 없습니다.")
        @Schema(description = "보관 장소 ID", nullable = false, example = "1")
        Long likePlaceId
) {
}
