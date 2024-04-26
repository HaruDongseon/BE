package haru.harudongseon.likeplace.domain.placedetails;

import java.util.Arrays;

public enum Reservable {
    NONE, TRUE, FALSE;

    public static Reservable findValue(final String name) {
        return Arrays.stream(Reservable.values())
                .filter(reservable -> reservable.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 Reservable이 없습니다."));
    }
}
