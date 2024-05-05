package haru.harudongseon.route.application.dto;

import java.math.BigDecimal;
import java.util.List;

import haru.harudongseon.place.domain.Place;
import haru.harudongseon.route.domain.RoutePlace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoutePlaceResponse {

    @Schema(description = "장소 ID", example = "1")
    private Long id;

    @Schema(description = "외부 공급자 Place ID", example = "ChIJ07n0DkZ8ezUR7wp5kpXtCYQ")
    private String providerPlaceId;

    @Schema(description = "장소 이름", example = "스타벅스 부평점")
    private String name;

    @Schema(description = "장소 카테고리", example = "커피숍/커피 전문점")
    private String category;

    @Schema(description = "구글 장소 이미지 요청 Reference 리스트", example = "[\"AxxB\", \"AVVx\", \"AccB\"]")
    private List<String> photoReferences;

    @Schema(description = "장소 위도(10진수 9자, 소수점 6자 이내)", example = "127.058970")
    private BigDecimal latitude;

    @Schema(description = "장소 경도(10진수 9자, 소수점 6자 이내)", example = "37.506051")
    private BigDecimal longitude;

    @Schema(description = "장소 영업 시간", example = "월요일: 오전 7:00 ~ 오후 11:00, 화요일: 오전 7:00 ~ 오후 11:00, 수요일: 오전 7:00 ~ 오후 11:00, 목요일: 오전 7:00 ~ 오후 11:00, 금요일: 오전 7:00 ~ 오후 11:00, 토요일: 오전 7:00 ~ 오후 11:00, 일요일: 오전 9:00 ~ 오후 11:00")
    private String openingHours;

    @Schema(description = "장소 주소 이름", example = "인천광역시 부평구 경원대로 1397")
    private String addressName;

    @Schema(description = "장소 전화번호", example = "1522-3232")
    private String phoneNumber;

    @Schema(description = "장소 웹사이트", example = "http://www.starbucks.co.kr/")
    private String website;

    @Schema(description = "장소 구글 검색 URL", example = "https://maps.google.com/?cid=9514396914460199663")
    private String googleMapsUri;

    @Schema(description = "장소 예약 가능 여부", example = "FALSE")
    private String reservable;

    @Schema(description = "장소 포장 가능 여부", example = "TRUE")
    private String takeoutAvailable;

    @Schema(description = "장소 주차 예약 가능 여부", example = "FALSE")
    private String parkingAvailable;

    private RoutePlaceResponse(final Long id, final String providerPlaceId,
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

    public static RoutePlaceResponse from(final RoutePlace routePlace) {
        final Place place = routePlace.getPlace();

        final Long id = place.getId();
        final String providerPlaceId = place.getProviderPlaceId();
        final String name = place.getName();
        final String category = place.getCategory();
        final List<String> photoReferences = place.getPhotoReferences();
        final BigDecimal latitude = place.getCoordinates().getLatitude();
        final BigDecimal longitude = place.getCoordinates().getLongitude();
        final String openingHours = place.getOpeningHours();
        final String addressName = place.getAddressName();
        final String phoneNumber = place.getPhoneNumber();
        final String website = place.getWebsite();
        final String googleMapsUri = place.getGoogleMapsUri();
        final String reservable = place.getPlaceDetails().getReservable().name();
        final String takeoutAvailable = place.getPlaceDetails().getTakeoutAvailable().name();
        final String parkingAvailable = place.getPlaceDetails().getParkingAvailable().name();

        return new RoutePlaceResponse(
                id, providerPlaceId, name, category, photoReferences,
                latitude, longitude, openingHours, addressName,
                phoneNumber, website, googleMapsUri,
                reservable, takeoutAvailable, parkingAvailable
        );
    }
}
