package haru.harudongseon.routestorage.application;


import java.util.List;

import haru.harudongseon.route.application.event.RouteDeleteEvent;
import haru.harudongseon.routestorage.domain.StoredRoute;
import haru.harudongseon.routestorage.domain.StoredRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class RouteStorageEventListener {

    private final StoredRouteRepository storedRouteRepository;

    @EventListener
    public void deleteLikePlace(final RouteDeleteEvent routeDeleteEvent) {
        final Long routeId = routeDeleteEvent.routeId();
        final List<StoredRoute> storedRoutes = storedRouteRepository.findAllByRouteId(routeId);
        storedRoutes.forEach(StoredRoute::unstored);
    }
}
