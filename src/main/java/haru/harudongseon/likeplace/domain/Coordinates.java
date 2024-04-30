package haru.harudongseon.likeplace.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coordinates {

    @Column(precision = 15, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 15, scale = 6)
    private BigDecimal longitude;

    public Coordinates(final BigDecimal latitude, final BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
