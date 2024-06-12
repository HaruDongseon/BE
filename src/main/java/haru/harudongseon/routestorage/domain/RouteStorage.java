package haru.harudongseon.routestorage.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routestorage.exception.RouteStorageException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RouteStorage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    private String name;

    @OneToMany(mappedBy = "routeStorage", cascade = CascadeType.PERSIST)
    private List<StoredRoute> routes = new ArrayList<>();

    public RouteStorage(final Member member, final String name) {
        this.member = member;
        this.name = name;
    }

    public void moveRoutes(final List<RouteStorage> routeStoragesToMove, final List<Route> routesToMove) {
        validateAvailableMove(routeStoragesToMove, routesToMove);

        routeStoragesToMove.forEach(routeStorageToMove -> routeStorageToMove.addRoutes(routesToMove));
        final List<Long> routeIdsToMove = routesToMove.stream()
                .map(Route::getId)
                .toList();
        this.removeRoutes(routeIdsToMove);
    }

    private void validateAvailableMove(final List<RouteStorage> routeStoragesToMove, final List<Route> routesToMove) {
        validateAddRoute(routeStoragesToMove, routesToMove);
        validateRemoveRoute(routesToMove);
    }

    private void validateAddRoute(final List<RouteStorage> routeStoragesToMove, final List<Route> routesToMove) {
        for (RouteStorage routeStorageToMove : routeStoragesToMove) {
            for (Route routeToMove : routesToMove) {
                routeStorageToMove.validateAlreadyExistRoute(routeToMove.getId());
            }
        }
    }

    private void validateRemoveRoute(final List<Route> routesToMove) {
        final List<Long> routeIds = this.routes.stream()
                .map(storedRoute -> storedRoute.getRoute().getId())
                .toList();
        for (Route routeToMove : routesToMove) {
            final Long routeIdToMove = routeToMove.getId();
            if (!routeIds.contains(routeIdToMove)) {
                throw new RouteStorageException.NotExistRouteException();
            }
        }
    }

    public void addRoutes(final List<Route> routes) {
        routes.forEach(route -> this.validateAlreadyExistRoute(route.getId()));
        routes.forEach(this::addRoute);
    }

    private void addRoute(final Route route) {
        final StoredRoute storedRoute = new StoredRoute();
        storedRoute.associate(route, this);
    }

    public void removeRoutes(final List<Long> routeIds) {
        routeIds.forEach(this::removeRoute);
    }

    private void removeRoute(final Long routeId) {
        final StoredRoute routeToRemove = routes.stream()
                .filter(storedRoute -> storedRoute.getRoute().getId().equals(routeId))
                .findFirst()
                .orElseThrow(RouteStorageException.NotExistRouteException::new);

        routes.remove(routeToRemove);
    }

    private void validateAlreadyExistRoute(final Long routeId) {
        final Optional<StoredRoute> optionalRoute = routes.stream()
                .filter(route -> route.isEqualToRouteId(routeId))
                .findFirst();

        if (optionalRoute.isPresent()) {
            throw new RouteStorageException.AlreadyExistRouteException();
        }
    }
}
