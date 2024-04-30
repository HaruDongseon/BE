package haru.harudongseon.likeplace.domain.placedetails;

import java.util.Arrays;

public enum TakeoutAvailable {
    NONE, TRUE, FALSE;

    public static TakeoutAvailable findValue(final String name) {
        return Arrays.stream(TakeoutAvailable.values())
                .filter(takeoutAvailable -> takeoutAvailable.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 TakeoutAvailable이 없습니다."));
    }
}
