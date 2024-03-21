package haru.harudongseon.place.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

public record PlaceAddRequest(
        @NotBlank(message = "외부 제공자의 장소 ID는 공백일 수 없습니다.")
        Long providerPlaceId,

        @NotBlank(message = "장소 이름은 공백일 수 없습니다.")
        String name,

        @NotBlank(message = "카테고리는 공백일 수 없습니다.")
        String category,

        @NotBlank(message = "위도는 공백일 수 없습니다.")
        @Digits(message = "위도는 10진수 9자, 소수점 6자 이내 여야합니다.", integer = 9, fraction = 6)
        BigDecimal latitude,

        @NotBlank(message = "경도는 공백일 수 없습니다.")
        @Digits(message = "경도는 10진수 9자, 소수점 6자 이내 여야합니다.", integer = 9, fraction = 6)
        BigDecimal longitude,

        @NotBlank(message = "주소 이름은 공백일 수 없습니다.")
        String addressName
) {
}
