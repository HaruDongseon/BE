package haru.harudongseon.routeplace.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutePlaceRepository extends JpaRepository<RoutePlace, Long> {

    Optional<RoutePlace> findByProviderPlaceId(final String providerPlaceId);
}
