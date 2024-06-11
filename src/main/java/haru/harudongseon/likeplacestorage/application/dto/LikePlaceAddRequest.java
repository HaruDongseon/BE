package haru.harudongseon.likeplacestorage.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record LikePlaceAddRequest(

        @Size(min = 1, message = "보관 장소 ID 리스트는 공백일 수 없습니다.")
        @Schema(description = "보관 장소 ID 리스트", nullable = false, example = "[1, 2, 3]")
        List<Long> likePlaceIds
) {
}
