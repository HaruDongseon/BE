package haru.harudongseon.routestorage.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StoredRouteDeleteRequest(

        @NotNull(message = "동선 보관함 ID는 공백일 수 없습니다.")
        @Schema(description = "동선 보관함 ID", nullable = false, example = "1")
        Long routeStorageId,

        @Size(min = 1, message = "동선 ID 리스트는 공백일 수 없습니다.")
        @Schema(description = "동선 ID 리스트", nullable = false, example = "[1, 2, 3]")
        List<Long> routeIds
) {
}
