package haru.harudongseon.routestorage.domain;

import java.util.List;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.route.domain.Route;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class StoredRoute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private RouteStorage routeStorage;

    public void associate(final Route route, final RouteStorage routeStorage) {
        this.route = route;
        this.routeStorage = routeStorage;
        routeStorage.getRoutes().add(this);
    }

    public void unstored() {
        final List<StoredRoute> routes = routeStorage.getRoutes();
        routes.remove(this);
    }

    public boolean isEqualToRouteId(final Long routeId) {
        return route.getId().equals(routeId);
    }
}
