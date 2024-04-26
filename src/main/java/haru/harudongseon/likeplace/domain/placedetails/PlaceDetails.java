package haru.harudongseon.likeplace.domain.placedetails;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlaceDetails {

    private Reservable reservable;
    private TakeoutAvailable takeoutAvailable;
    private DeliveryAvailable deliveryAvailable;

    public PlaceDetails(final String reservableName, final String takeoutAvailableName,
                        final String deliveryAvailableName) {
        this.reservable = Reservable.findValue(reservableName);
        this.takeoutAvailable = TakeoutAvailable.findValue(takeoutAvailableName);
        this.deliveryAvailable = DeliveryAvailable.findValue(deliveryAvailableName);
    }

    public PlaceDetails(final Reservable reservable, final TakeoutAvailable takeoutAvailable,
                        final DeliveryAvailable deliveryAvailable) {
        this.reservable = reservable;
        this.takeoutAvailable = takeoutAvailable;
        this.deliveryAvailable = deliveryAvailable;
    }
}
