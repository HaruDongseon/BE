package haru.harudongseon.routestorage.domain;

import java.util.ArrayList;
import java.util.List;

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

    public StoredRoute addRoute(final Route route) {
        final StoredRoute storedRoute = new StoredRoute();
        storedRoute.associate(route, this);
        return storedRoute;
    }

    public void removeRoutes(final Long memberId, final List<Long> routeIds) {
        validateOwner(memberId);
        routeIds.forEach(this::removeRoute);
    }

    private void validateOwner(final Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new RouteStorageException.NotOwnerException();
        }
    }

    private void removeRoute(final Long routeId) {
        final StoredRoute routeToRemove = routes.stream()
                .filter(storedRoute -> storedRoute.getRoute().getId().equals(routeId))
                .findFirst()
                .orElseThrow(RouteStorageException.NotExistRouteException::new);

        routes.remove(routeToRemove);
    }
}
