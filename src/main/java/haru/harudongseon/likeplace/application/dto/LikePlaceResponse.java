package haru.harudongseon.likeplace.application.dto;

import java.math.BigDecimal;
import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikePlaceResponse {

    @Schema(description = "보관 장소 ID", example = "1")
    private Long id;

    @Schema(description = "외부 공급자 Place ID", example = "ChIJ07n0DkZ8ezUR7wp5kpXtCYQ")
    private String providerPlaceId;

    @Schema(description = "보관 장소 이름", example = "스타벅스 부평점")
    private String name;

    @Schema(description = "보관 장소 카테고리", example = "커피숍/커피 전문점")
    private String category;

    @Schema(description = "구글 장소 이미지 요청 Reference 리스트", example = "[\"AxxB\", \"AVVx\", \"AccB\"]")
    private List<String> photoReferences;

    @Schema(description = "보관 장소 위도(10진수 9자, 소수점 6자 이내)", example = "127.058970")
    private BigDecimal latitude;

    @Schema(description = "보관 장소 경도(10진수 9자, 소수점 6자 이내)", example = "37.506051")
    private BigDecimal longitude;

    @Schema(description = "장소 영업 시간", example = "월요일: 오전 7:00 ~ 오후 11:00, 화요일: 오전 7:00 ~ 오후 11:00, 수요일: 오전 7:00 ~ 오후 11:00, 목요일: 오전 7:00 ~ 오후 11:00, 금요일: 오전 7:00 ~ 오후 11:00, 토요일: 오전 7:00 ~ 오후 11:00, 일요일: 오전 9:00 ~ 오후 11:00")
    private String openingHours;

    @Schema(description = "보관 장소 주소 이름", example = "인천광역시 부평구 경원대로 1397")
    private String addressName;

    @Schema(description = "보관 장소 전화번호", example = "1522-3232")
    private String phoneNumber;

    @Schema(description = "보관 장소 웹사이트", example = "http://www.starbucks.co.kr/")
    private String website;

    @Schema(description = "보관 장소 구글 검색 URL", example = "https://maps.google.com/?cid=9514396914460199663")
    private String googleMapsUri;

    @Schema(description = "보관 장소 예약 가능 여부", example = "FALSE")
    private String reservable;

    @Schema(description = "보관 장소 포장 가능 여부", example = "TRUE")
    private String takeoutAvailable;

    @Schema(description = "보관 장소 주차 예약 가능 여부", example = "FALSE")
    private String parkingAvailable;

    private LikePlaceResponse(final Long id, final String providerPlaceId,
                              final String name, final String category,
                              final List<String> photoReferences, final BigDecimal latitude,
                              final BigDecimal longitude, final String openingHours,
                              final String addressName, final String phoneNumber,
                              final String website, final String googleMapsUri,
                              final String reservable, final String takeoutAvailable,
                              final String parkingAvailable) {
        this.id = id;
        this.providerPlaceId = providerPlaceId;
        this.name = name;
        this.category = category;
        this.photoReferences = photoReferences;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingHours = openingHours;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.googleMapsUri = googleMapsUri;
        this.reservable = reservable;
        this.takeoutAvailable = takeoutAvailable;
        this.parkingAvailable = parkingAvailable;
    }

    public static LikePlaceResponse from(final LikePlace likePlace) {
        final Long id = likePlace.getId();
        final String providerPlaceId = likePlace.getProviderPlaceId();
        final String name = likePlace.getName();
        final String category = likePlace.getCategory();
        final List<String> photoReferences = likePlace.getPhotoReferences();
        final BigDecimal latitude = likePlace.getCoordinates().getLatitude();
        final BigDecimal longitude = likePlace.getCoordinates().getLongitude();
        final String openingHours = likePlace.getOpeningHours();
        final String addressName = likePlace.getAddressName();
        final String phoneNumber = likePlace.getPhoneNumber();
        final String website = likePlace.getWebsite();
        final String googleMapsUri = likePlace.getGoogleMapsUri();
        final String reservable = likePlace.getPlaceDetails().getReservable().name();
        final String takeoutAvailable = likePlace.getPlaceDetails().getTakeoutAvailable().name();
        final String parkingAvailable = likePlace.getPlaceDetails().getParkingAvailable().name();

        return new LikePlaceResponse(
                id, providerPlaceId, name, category, photoReferences,
                latitude, longitude, openingHours, addressName,
                phoneNumber, website, googleMapsUri,
                reservable, takeoutAvailable, parkingAvailable
        );
    }
}
