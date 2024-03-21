package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.PlaceFixtures.*;

import java.math.BigDecimal;

import haru.harudongseon.place.application.dto.PlaceAddRequest;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaceBuilder {

    @Autowired
    private PlaceRepository placeRepository;

    private Long providerPlaceId;
    private String name;
    private String category;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String addressName;

    public PlaceBuilder defaultPlace() {
        this.providerPlaceId = 기본_외부_공급자_ID;
        this.name = 기본_장소_이름;
        this.category = 기본_장소_카테고리;
        this.latitude = 기본_장소_위도;
        this.longitude = 기본_장소_경도;
        this.addressName = 기본_장소_주소_이름;

        return this;
    }

    public PlaceBuilder providerPlaceId(final Long providerPlaceId) {
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

    public PlaceBuilder latitude(final BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public PlaceBuilder longitude(final BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

    public PlaceBuilder addressName(final String addressName) {
        this.addressName = addressName;
        return this;
    }

    public Place build() {
        final Place place = new Place(providerPlaceId, name, category, latitude, longitude, addressName);
        return placeRepository.save(place);
    }

    public PlaceAddRequest buildPlaceAddRequest(final Place place) {
        return new PlaceAddRequest(
                place.getProviderPlaceId(),
                place.getName(),
                place.getCategory(),
                place.getCoordinates().getLatitude(),
                place.getCoordinates().getLongitude(),
                place.getAddressName()
        );
    }
}
