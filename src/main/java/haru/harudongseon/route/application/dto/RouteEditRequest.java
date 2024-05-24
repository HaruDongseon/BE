package haru.harudongseon.route.application.dto;

import java.time.LocalDate;
import java.util.List;

import haru.harudongseon.route.domain.Route;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RouteEditRequest(

        @NotNull(message = "동선 날짜는 공백일 수 없습니다.")
        @Schema(description = "동선 날짜", nullable = false, example = "2023-05-24")
        LocalDate date,

        @NotBlank(message = "동선 제목은 공백일 수 없습니다.")
        @Size(max = 15, message = "동선 제목은 15자 이하여야합니다.")
        @Schema(description = "동선 제목", nullable = false, example = "하루동선1")
        String title,

        @Schema(description = "동선 태그", nullable = false, example = "[\"스터디\", \"데이트\"]")
        List<String> tag,

        @NotBlank(message = "동선 이동수단은 공백일 수 없습니다. 미선택인 경우 NONE을 입력하세요.")
        @Schema(description = "동선 이동수단", nullable = false, example = "대중교통/자동차")
        String moveWay,

        @Size(max = 30, message = "동선 장소는 30개 이하여야합니다.")
        @Schema(description = "동선 장소 리스트", nullable = false)
        List<RoutePlaceDto> routePlaces
        ) {

    public Route toEditEntity() {
        return new Route(date, title, moveWay);
    }
}
