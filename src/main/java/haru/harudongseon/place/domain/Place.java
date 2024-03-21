package haru.harudongseon.place.domain;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Place {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long providerPlaceId;

    private String name;

    private String category;

    @Embedded
    private Coordinates coordinates;

    private String addressName;
    private int selectCount;

    public Place(final Long providerPlaceId, final String name,
                 final String category, final BigDecimal latitude,
                 final BigDecimal longitude, final String addressName) {
        this.providerPlaceId = providerPlaceId;
        this.name = name;
        this.category = category;
        this.coordinates = new Coordinates(latitude, longitude);
        this.addressName = addressName;
        this.selectCount = 0;
    }

    public void select() {
        this.selectCount++;
    }
}
