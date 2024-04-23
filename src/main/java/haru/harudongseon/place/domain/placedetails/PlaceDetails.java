package haru.harudongseon.place.domain.placedetails;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlaceDetails {

    @Enumerated(value = EnumType.STRING)
    private Reservable reservable;

    @Enumerated(value = EnumType.STRING)
    private TakeoutAvailable takeoutAvailable;

    @Enumerated(value = EnumType.STRING)
    private DeliveryAvailable deliveryAvailable;

    public PlaceDetails(final Reservable reservable, final TakeoutAvailable takeoutAvailable,
                        final DeliveryAvailable deliveryAvailable) {
        this.reservable = reservable;
        this.takeoutAvailable = takeoutAvailable;
        this.deliveryAvailable = deliveryAvailable;
    }
}
