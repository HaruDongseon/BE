package haru.harudongseon.likeplacestorage.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record StoredLikePlaceMoveRequest(

        @Size(min = 1, message = "이동할 보관 장소 ID 리스트는 공백일 수 없습니다.")
        @Schema(description = "이동할 보관 장소 ID 리스트", nullable = false, example = "[1, 2, 3]")
        List<Long> likePlaceIds,

        @Size(min = 1, message = "이동할 장소 보관함 ID 리스트는 공백일 수 없습니다.")
        @Schema(description = "이동할 장소 보관함 ID 리스트", nullable = false, example = "[1, 2, 3]")
        List<Long> likePlaceStorageIds
) {
}
