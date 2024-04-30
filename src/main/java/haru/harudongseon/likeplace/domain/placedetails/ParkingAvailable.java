package haru.harudongseon.likeplace.domain.placedetails;

import java.util.Arrays;

public enum ParkingAvailable {
    NONE, TRUE, FALSE;

    public static ParkingAvailable findValue(final String name) {
        return Arrays.stream(ParkingAvailable.values())
                .filter(parkingAvailable -> parkingAvailable.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 ParkingAvailable이 없습니다."));
    }
}
