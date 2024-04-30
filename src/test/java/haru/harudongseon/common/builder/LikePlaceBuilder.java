package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.LikePlaceFixtures.*;

import java.math.BigDecimal;
import java.util.List;

import haru.harudongseon.likeplace.application.dto.LikePlaceAddRequest;
import haru.harudongseon.likeplace.domain.Coordinates;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplace.domain.LikePlaceRepository;
import haru.harudongseon.likeplace.domain.placedetails.ParkingAvailable;
import haru.harudongseon.likeplace.domain.placedetails.PlaceDetails;
import haru.harudongseon.likeplace.domain.placedetails.Reservable;
import haru.harudongseon.likeplace.domain.placedetails.TakeoutAvailable;
import haru.harudongseon.member.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikePlaceBuilder {

    @Autowired
    private LikePlaceRepository likePlaceRepository;

    private Member member;
    private String providerPlaceId;
    private String name;
    private String category;
    private List<String> photoReferences;
    private BigDecimal latitude;
    private BigDecimal longitude;

    private String openingHours;
    private String addressName;
    private String phoneNumber;
    private String website;
    private String url;
    private Reservable reservable;
    private TakeoutAvailable takeoutAvailable;
    private ParkingAvailable parkingAvailable;

    public LikePlaceBuilder defaultLikePlace(final Member member) {
        this.member = member;
        this.providerPlaceId = 기본_외부_공급자_ID;
        this.name = 기본_보관_장소_이름;
        this.category = 기본_보관_장소_카테고리;
        this.photoReferences = 기본_보관_장소_사진_참조;
        this.latitude = 기본_보관_장소_위도;
        this.longitude = 기본_보관_장소_경도;
        this.openingHours = 기본_보관_장소_영업_시간;
        this.addressName = 기본_보관_장소_주소_이름;
        this.phoneNumber = 기본_보관_장소_전화번호;
        this.website = 기본_보관_장소_웹사이트;
        this.url = 기본_보관_장소_URL;
        this.reservable = 기본_보관_장소_예약_가능_여부;
        this.takeoutAvailable = 기본_보관_장소_포장_가능_여부;
        this.parkingAvailable = 기본_보관_장소_주차_가능_여부;

        return this;
    }

    public LikePlaceBuilder member(final Member member) {
        this.member = member;
        return this;
    }

    public LikePlaceBuilder providerPlaceId(final String providerPlaceId) {
        this.providerPlaceId = providerPlaceId;
        return this;
    }

    public LikePlaceBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public LikePlaceBuilder category(final String category) {
        this.category = category;
        return this;
    }

    public LikePlaceBuilder photoReferences(final List<String> photoReferences) {
        this.photoReferences = photoReferences;
        return this;
    }

    public LikePlaceBuilder latitude(final BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public LikePlaceBuilder longitude(final BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

    public LikePlaceBuilder openingHours(final String openingHours) {
        this.openingHours = openingHours;
        return this;
    }

    public LikePlaceBuilder addressName(final String addressName) {
        this.addressName = addressName;
        return this;
    }

    public LikePlaceBuilder phoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public LikePlaceBuilder website(final String website) {
        this.website = website;
        return this;
    }

    public LikePlaceBuilder url(final String url) {
        this.url = url;
        return this;
    }

    public LikePlaceBuilder reservable(final Reservable reservable) {
        this.reservable = reservable;
        return this;
    }

    public LikePlaceBuilder takeoutAvailable(final TakeoutAvailable takeoutAvailable) {
        this.takeoutAvailable = takeoutAvailable;
        return this;
    }

    public LikePlaceBuilder parkingAvailable(final ParkingAvailable parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
        return this;
    }

    public LikePlace build() {
        final Coordinates coordinates = new Coordinates(latitude, longitude);
        final PlaceDetails placeDetails = new PlaceDetails(reservable, takeoutAvailable, parkingAvailable);
        final LikePlace likePlace = new LikePlace(
                member, providerPlaceId, name, category, photoReferences,
                coordinates, openingHours, addressName, phoneNumber,
                website, url, placeDetails
        );
        return likePlaceRepository.save(likePlace);
    }

    public LikePlaceAddRequest buildAddRequest() {
        this.member = null;
        return new LikePlaceAddRequest(
                providerPlaceId, name, category, photoReferences, latitude, longitude,
                openingHours, addressName, phoneNumber, website, url,
                reservable.name(), takeoutAvailable.name(), parkingAvailable.name()
        );
    }
}
