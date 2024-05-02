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
