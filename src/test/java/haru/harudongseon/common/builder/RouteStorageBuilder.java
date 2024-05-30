package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.RouteStorageFixtures.기본_동선_보관함_이름;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routestorage.domain.RouteStorage;
import haru.harudongseon.routestorage.domain.RouteStorageRepository;
import haru.harudongseon.routestorage.domain.StoredRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RouteStorageBuilder {

    @Autowired
    private RouteStorageRepository routeStorageRepository;

    private String name;
    private Member member;
    private List<StoredRoute> routes = new ArrayList<>();

    public RouteStorageBuilder defaultRouteStorage(final Member member) {
        this.name = 기본_동선_보관함_이름;
        this.member = member;

        return this;
    }

    public RouteStorageBuilder name(final String name) {
        this.name = name;

        return this;
    }

    public RouteStorageBuilder member(final Member member) {
        this.member = member;

        return this;
    }

    public RouteStorageBuilder routes(final List<StoredRoute> routes) {
        this.routes = routes;

        return this;
    }

    public RouteStorage build(final List<Route> routes) {
        final RouteStorage routeStorage = new RouteStorage(member, name);
        for (Route route : routes) {
            routeStorage.addRoute(route);
        }
        return routeStorageRepository.save(routeStorage);
    }
}
