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
    private ParkingAvailable parkingAvailable;

    public PlaceDetails(final String reservableName, final String takeoutAvailableName,
                        final String parkingAvailableName) {
        this.reservable = Reservable.findValue(reservableName);
        this.takeoutAvailable = TakeoutAvailable.findValue(takeoutAvailableName);
        this.parkingAvailable = ParkingAvailable.findValue(parkingAvailableName);
    }

    public PlaceDetails(final Reservable reservable, final TakeoutAvailable takeoutAvailable,
                        final ParkingAvailable parkingAvailable) {
        this.reservable = reservable;
        this.takeoutAvailable = takeoutAvailable;
        this.parkingAvailable = parkingAvailable;
    }
}
