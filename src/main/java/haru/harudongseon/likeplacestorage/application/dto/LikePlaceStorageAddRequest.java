package haru.harudongseon.likeplacestorage.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LikePlaceStorageAddRequest(

        @NotBlank(message = "장소 보관함 이름은 공백일 수 없습니다.")
        @Size(max = 15, message = "장소 보관함 이름은 15자 이하여야 합니다.")
        @Schema(description = "장소 보관함 이름", nullable = false, example = "카페")
        String name
) {
}
