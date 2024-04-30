package haru.harudongseon.likeplace.application.dto;

import java.math.BigDecimal;
import java.util.Set;

import haru.harudongseon.likeplace.domain.Coordinates;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplace.domain.placedetails.PlaceDetails;
import haru.harudongseon.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LikePlaceAddRequest(

        @NotBlank(message = "외부 공급자의 PlaceId는 공백일 수 없습니다.")
        @Schema(description = "외부 공급자 Place ID", nullable = false, example = "ChIJ07n0DkZ8ezUR7wp5kpXtCYQ")
        String providerPlaceId,

        @NotBlank(message = "이름은 공백일 수 없습니다.")
        @Schema(description = "보관 장소 이름", nullable = false, example = "스타벅스 부평점")
        String name,

        @NotBlank(message = "카테고리는 공백일 수 없습니다.")
        @Schema(description = "보관 장소 카테고리", nullable = false, example = "커피숍/커피 전문점")
        String category,

        @Schema(description = "구글 장소 이미지 요청 Reference 리스트", nullable = false, example = "[AxxB, AVVx, AccB]")
        Set<String> photoReferences,

        @NotNull(message = "위도는 공백일 수 없습니다. 올바른 형식 또는 값을 입력해주세요.")
        @Digits(message = "위도는 10진수 9자, 소수점 6자 이내 여야합니다.", integer = 9, fraction = 6)
        @Schema(description = "보관 장소 위도(10진수 9자, 소수점 6자 이내)", nullable = false, example = "127.058970")
        BigDecimal latitude,

        @NotNull(message = "경도는 공백일 수 없습니다. 올바른 형식 또는 값을 입력해주세요.")
        @Digits(message = "경도는 10진수 9자, 소수점 6자 이내 여야합니다.", integer = 9, fraction = 6)
        @Schema(description = "보관 장소 경도(10진수 9자, 소수점 6자 이내)", nullable = false, example = "37.506051")
        BigDecimal longitude,

        @NotBlank(message = "영업 시간은 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.")
        @Schema(description = "장소 영업 시간", nullable = false, example = "월요일: 오전 7:00 ~ 오후 11:00, 화요일: 오전 7:00 ~ 오후 11:00, 수요일: 오전 7:00 ~ 오후 11:00, 목요일: 오전 7:00 ~ 오후 11:00, 금요일: 오전 7:00 ~ 오후 11:00, 토요일: 오전 7:00 ~ 오후 11:00, 일요일: 오전 9:00 ~ 오후 11:00")
        String openingHours,

        @NotBlank(message = "주소 이름은 공백일 수 없습니다.")
        @Schema(description = "보관 장소 주소 이름", nullable = false, example = "인천광역시 부평구 경원대로 1397")
        String addressName,

        @NotBlank(message = "전화번호는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.")
        @Schema(description = "보관 장소 전화번호", nullable = false, example = "1522-3232")
        String phoneNumber,

        @NotBlank(message = "웹사이트는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.")
        @Schema(description = "보관 장소 웹사이트", nullable = false, example = "http://www.starbucks.co.kr/")
        String website,

        @NotBlank(message = "장소 구글 검색 URL은 공백일 수 없습니다.")
        @Schema(description = "보관 장소 구글 검색 URL", nullable = false, example = "https://maps.google.com/?cid=9514396914460199663")
        String googleMapsUri,

        @NotBlank(message = "예약 가능 여부는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.")
        @Schema(description = "보관 장소 예약 가능 여부", nullable = false, example = "false")
        String reservable,

        @NotBlank(message = "포장 가능 여부는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.")
        @Schema(description = "보관 장소 포장 가능 여부", nullable = false, example = "true")
        String takeoutAvailable,

        @NotBlank(message = "주차 가능 여부는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.")
        @Schema(description = "보관 장소 주차 예약 가능 여부", nullable = false, example = "false")
        String parkingAvailable
) {

    public LikePlace toEntity(final Member member) {
        final Coordinates coordinates = new Coordinates(latitude, longitude);
        final PlaceDetails placeDetails = new PlaceDetails(reservable, takeoutAvailable, parkingAvailable);
        return new LikePlace(member, providerPlaceId, name, category, photoReferences, coordinates, openingHours, addressName, phoneNumber, website, googleMapsUri, placeDetails);
    }
}
