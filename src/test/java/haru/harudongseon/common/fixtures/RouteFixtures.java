package haru.harudongseon.common.fixtures;

import java.time.LocalDate;
import java.util.List;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routetag.domain.RouteTag;

public class RouteFixtures {

    public static final LocalDate 기본_동선_날짜 = LocalDate.of(2023, 5, 28);
    public static final LocalDate 동선_5월_첫날_날짜 = LocalDate.of(2023, 5, 1);
    public static final LocalDate 동선_5월_마지막날_날짜 = LocalDate.of(2023, 5, 31);
    public static final LocalDate 동선_5월_둘째주_첫날_날짜 = LocalDate.of(2023, 5, 5);
    public static final LocalDate 동선_5월_둘째주_중간_날짜 = LocalDate.of(2023, 5, 8);
    public static final LocalDate 동선_5월_둘째주_마지막날_날짜 = LocalDate.of(2023, 5, 11);
    public static final String 기본_동선_제목 = "하루동선1";
    public static final String 기본_동선_이동수단 = "대중교통/자동차";
    public static final String 기본_동선_태그1 = RouteTagFixtures.기본_태그1_이름;
    public static final String 기본_동선_태그2 = RouteTagFixtures.기본_태그2_이름;

    public static Route 기본_동선_도메인(final Long id, final Member member, List<RouteTag> tags, List<Place> places) {
        final Route route = new Route(id, member, 기본_동선_날짜, 기본_동선_제목, 기본_동선_이동수단);
        for (RouteTag tag : tags) {
            route.addTag(tag);
        }
        for (Place place : places) {
            route.addPlace(place);
        }
        return route;
    }

    public static Route 기본_동선_엔티티(final Member member, List<RouteTag> tags, List<Place> places) {
        final Route route = new Route(member, 기본_동선_날짜, 기본_동선_제목, 기본_동선_이동수단);
        for (RouteTag tag : tags) {
            route.addTag(tag);
        }
        for (Place place : places) {
            route.addPlace(place);
        }
        return route;
    }

    public static Route 동선_날짜_입력_엔티티(final Member member, final LocalDate date) {
        final Route route = new Route(member, date, 기본_동선_제목, 기본_동선_이동수단);
        return route;
    }
}
