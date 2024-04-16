package haru.harudongseon.route.domain.routeplace;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutePlaceRepository extends JpaRepository<RoutePlace, Long> {

    Optional<RoutePlace> findByProviderPlaceId(final String providerPlaceId);
}
