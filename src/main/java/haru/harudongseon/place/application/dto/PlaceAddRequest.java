package haru.harudongseon.place.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceAddRequest(

        @NotNull(message = "외부 제공자의 장소 ID는 공백일 수 없습니다.")
        @Schema(description = "외부 제공자 장소 ID(카카오 장소 ID)", nullable = false, example = "16618597")
        Long providerPlaceId,

        @NotBlank(message = "장소 이름은 공백일 수 없습니다.")
        @Schema(description = "장소 이름", nullable = false, example = "장생당약국")
        String name,

        @NotBlank(message = "카테고리는 공백일 수 없습니다.")
        @Schema(description = "장소 카테고리", nullable = false, example = "약국")
        String category,

        @NotNull(message = "위도는 공백일 수 없습니다. 올바른 형식 또는 값을 입력해주세요.")
        @Digits(message = "위도는 10진수 9자, 소수점 6자 이내 여야합니다.", integer = 9, fraction = 6)
        @Schema(description = "장소 위도(10진수 9자, 소수점 6자 이내)", nullable = false, example = "127.058970")
        BigDecimal latitude,

        @NotNull(message = "경도는 공백일 수 없습니다. 올바른 형식 또는 값을 입력해주세요.")
        @Digits(message = "경도는 10진수 9자, 소수점 6자 이내 여야합니다.", integer = 9, fraction = 6)
        @Schema(description = "장소 경도(10진수 9자, 소수점 6자 이내)", nullable = false, example = "37.506051")
        BigDecimal longitude,

        @NotBlank(message = "주소 이름은 공백일 수 없습니다.")
        @Schema(description = "장소 주소 이름", nullable = false, example = "서울 대치동")
        String addressName
) {
}
