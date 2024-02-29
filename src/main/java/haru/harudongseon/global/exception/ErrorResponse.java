package haru.harudongseon.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "에러 Response", nullable = false, example = "에러 메시지")
        String errorMessage
) {
}
