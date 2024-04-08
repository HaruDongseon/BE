package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.RoutePlaceFixtures.*;

import java.math.BigDecimal;

import haru.harudongseon.routeplace.domain.RoutePlace;
import haru.harudongseon.routeplace.domain.RoutePlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoutePlaceBuilder {

    @Autowired
    private RoutePlaceRepository routePlaceRepository;

    private String providerPlaceId;
    private String name;
    private String category;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String addressName;

    public RoutePlaceBuilder defaultPlace() {
        this.providerPlaceId = 기본_외부_공급자_ID;
        this.name = 기본_장소_이름;
        this.category = 기본_장소_카테고리;
        this.latitude = 기본_장소_위도;
        this.longitude = 기본_장소_경도;
        this.addressName = 기본_장소_주소_이름;

        return this;
    }

    public RoutePlaceBuilder providerPlaceId(final String providerPlaceId) {
        this.providerPlaceId = providerPlaceId;
        return this;
    }

    public RoutePlaceBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public RoutePlaceBuilder category(final String category) {
        this.category = category;
        return this;
    }

    public RoutePlaceBuilder latitude(final BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public RoutePlaceBuilder longitude(final BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

    public RoutePlaceBuilder addressName(final String addressName) {
        this.addressName = addressName;
        return this;
    }

    public RoutePlace build() {
        final RoutePlace routePlace = new RoutePlace(providerPlaceId, name, category, latitude, longitude, addressName);
        return routePlaceRepository.save(routePlace);
    }
}
