package haru.harudongseon.route.domain.routeplace;

import java.math.BigDecimal;

import haru.harudongseon.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoutePlace extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String providerPlaceId;

    private String name;

    private String category;

    @Embedded
    private Coordinates coordinates;

    private String addressName;

    public RoutePlace(final String providerPlaceId, final String name,
                      final String category, final BigDecimal latitude,
                      final BigDecimal longitude, final String addressName) {
        this.providerPlaceId = providerPlaceId;
        this.name = name;
        this.category = category;
        this.coordinates = new Coordinates(latitude, longitude);
        this.addressName = addressName;
    }
}
