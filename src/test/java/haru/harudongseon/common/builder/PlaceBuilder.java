package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.PlaceFixtures.*;

import java.math.BigDecimal;
import java.util.Set;

import haru.harudongseon.place.domain.Coordinates;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.PlaceRepository;
import haru.harudongseon.place.domain.placedetails.ParkingAvailable;
import haru.harudongseon.place.domain.placedetails.PlaceDetails;
import haru.harudongseon.place.domain.placedetails.Reservable;
import haru.harudongseon.place.domain.placedetails.TakeoutAvailable;
import haru.harudongseon.route.application.dto.RoutePlaceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaceBuilder {

    @Autowired
    private PlaceRepository placeRepository;

    private String providerPlaceId;
    private String name;
    private String category;
    private Set<String> photoReferences;
    private BigDecimal latitude;
    private BigDecimal longitude;

    private String openingHours;
    private String addressName;
    private String phoneNumber;
    private String website;
    private String googleMapsUri;
    private Reservable reservable;
    private TakeoutAvailable takeoutAvailable;
    private ParkingAvailable parkingAvailable;

    public PlaceBuilder defaultPlace() {
        this.providerPlaceId = 기본_외부_공급자_ID;
        this.name = 기본_장소_이름;
        this.category = 기본_장소_카테고리;
        this.photoReferences = 기본_장소_사진_참조;
        this.latitude = 기본_장소_위도;
        this.longitude = 기본_장소_경도;
        this.openingHours = 기본_장소_영업_시간;
        this.addressName = 기본_장소_주소_이름;
        this.phoneNumber = 기본_장소_전화번호;
        this.website = 기본_장소_웹사이트;
        this.googleMapsUri = 기본_장소_구글_맵_URL;
        this.reservable = 기본_장소_예약_가능_여부;
        this.takeoutAvailable = 기본_장소_포장_가능_여부;
        this.parkingAvailable = 기본_장소_주차_가능_여부;

        return this;
    }

    public PlaceBuilder providerPlaceId(final String providerPlaceId) {
        this.providerPlaceId = providerPlaceId;
        return this;
    }

    public PlaceBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public PlaceBuilder category(final String category) {
        this.category = category;
        return this;
    }

    public PlaceBuilder photoReferences(final Set<String> photoReferences) {
        this.photoReferences = photoReferences;
        return this;
    }

    public PlaceBuilder latitude(final BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public PlaceBuilder longitude(final BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

    public PlaceBuilder openingHours(final String openingHours) {
        this.openingHours = openingHours;
        return this;
    }

    public PlaceBuilder addressName(final String addressName) {
        this.addressName = addressName;
        return this;
    }

    public PlaceBuilder phoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public PlaceBuilder website(final String website) {
        this.website = website;
        return this;
    }

    public PlaceBuilder url(final String url) {
        this.googleMapsUri = url;
        return this;
    }

    public PlaceBuilder reservable(final Reservable reservable) {
        this.reservable = reservable;
        return this;
    }

    public PlaceBuilder takeoutAvailable(final TakeoutAvailable takeoutAvailable) {
        this.takeoutAvailable = takeoutAvailable;
        return this;
    }

    public PlaceBuilder parkingAvailable(final ParkingAvailable parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
        return this;
    }

    public Place build() {
        final Coordinates coordinates = new Coordinates(latitude, longitude);
        final PlaceDetails placeDetails = new PlaceDetails(reservable, takeoutAvailable, parkingAvailable);
        final Place place = new Place(
                providerPlaceId, name, category, photoReferences,
                coordinates, openingHours, addressName, phoneNumber,
                website, googleMapsUri, placeDetails
        );
        return placeRepository.save(place);
    }

    public RoutePlaceDto buildRoutePlaceDto() {
        return new RoutePlaceDto(
                providerPlaceId, name, category, photoReferences,
                latitude, longitude, openingHours, addressName,
                phoneNumber, website, googleMapsUri,
                reservable.name(), takeoutAvailable.name(), parkingAvailable.name()
        );
    }
}
