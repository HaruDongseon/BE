package haru.harudongseon.routestorage.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredRouteRepository extends JpaRepository<StoredRoute, Long> {

    List<StoredRoute> findAllByRouteId(final Long routeId);
}
