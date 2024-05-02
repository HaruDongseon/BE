package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.RouteFixtures.*;

import java.time.LocalDate;
import java.util.Set;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.route.domain.*;
import haru.harudongseon.routetag.domain.RouteTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RouteBuilder {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private SelectedTagRepository selectedTagRepository;

    @Autowired
    private RoutePlaceRepository routePlaceRepository;

    @Autowired
    private RouteTagBuilder routeTagBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    private Member member;
    private LocalDate date;
    private String title;
    private Set<SelectedTag> tags;
    private String moveWays;
    private Set<RoutePlace> routePlaces;

    public RouteBuilder defaultRoute(Member member) {
        this.member = member;
        this.date = 기본_동선_날짜;
        this.title = 기본_동선_제목;
        this.moveWays = 기본_동선_이동수단;

        return this;
    }

    public RouteBuilder member(final Member member) {
        this.member = member;
        return this;
    }

    public RouteBuilder date(final LocalDate date) {
        this.date = date;
        return this;
    }

    public RouteBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public RouteBuilder tags(final Set<SelectedTag> tags) {
        this.tags = tags;
        return this;
    }


    public RouteBuilder moveWays(final String moveWays) {
        this.moveWays = moveWays;
        return this;
    }

    public SelectedTag addTag(final Route route, final RouteTag routeTag) {
        final SelectedTag selectedTag = new SelectedTag();
        selectedTag.associate(route, routeTag);
        return selectedTagRepository.save(selectedTag);
    }

    public RoutePlace addPlace(final Route route, final Place place) {
        final RoutePlace routePlace = new RoutePlace();
        routePlace.associate(route, place);
        return routePlaceRepository.save(routePlace);
    }

    public Route build() {
        final Route route = new Route(member, date, title, moveWays);
        return routeRepository.save(route);
    }
}
