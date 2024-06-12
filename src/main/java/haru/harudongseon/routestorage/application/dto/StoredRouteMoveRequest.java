package haru.harudongseon.routestorage.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record StoredRouteMoveRequest(

        @Size(min = 1, message = "이동할 동선 ID 리스트는 공백일 수 없습니다.")
        @Schema(description = "이동할 동선 ID 리스트", nullable = false, example = "[1, 2, 3]")
        List<Long> routeIds,

        @Size(min = 1, message = "이동할 동선 보관함 ID 리스트는 공백일 수 없습니다.")
        @Schema(description = "이동할 동선 보관함 ID 리스트", nullable = false, example = "[1, 2, 3]")
        List<Long> routeStorageIds
) {
}
