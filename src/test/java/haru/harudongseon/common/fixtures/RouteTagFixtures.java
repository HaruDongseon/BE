package haru.harudongseon.common.fixtures;

import haru.harudongseon.routetag.domain.RouteTag;

public class RouteTagFixtures {

    public static final String 기본_태그1_이름 = "study";
    public static final String 기본_태그2_이름 = "date";
    public static final Long 기본_태그_선택_횟수 = 1L;

    public static RouteTag 기본_동선_태그1_엔티티() {
        return new RouteTag(기본_태그1_이름);
    }

    public static RouteTag 기본_동선_태그2_엔티티() {
        return new RouteTag(기본_태그2_이름);
    }
}
