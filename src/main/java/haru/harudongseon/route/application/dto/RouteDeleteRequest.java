package haru.harudongseon.route.application.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RouteDeleteRequest(
        @NotNull(message = "삭제할 동선 날짜는 공백일 수 없습니다.")
        @Schema(description = "삭제할 동선 날짜", nullable = false, example = "2023-05-24")
        LocalDate date,

        @NotNull(message = "삭제할 동선 ID는 공백일 수 없습니다.")
        @Schema(description = "동선 제목", nullable = false, example = "하루동선1")
        Long routeId
) {
}
