package haru.harudongseon.likeplace.domain.placedetails;

import java.util.Arrays;

public enum DeliveryAvailable {
    NONE, TRUE, FALSE;

    public static DeliveryAvailable findValue(final String name) {
        return Arrays.stream(DeliveryAvailable.values())
                .filter(reservable -> reservable.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 DeliveryAvailable이 없습니다."));
    }
}
