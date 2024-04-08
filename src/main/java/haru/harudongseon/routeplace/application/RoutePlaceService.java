package haru.harudongseon.routeplace.application;

import java.util.Optional;

import haru.harudongseon.routeplace.application.dto.RoutePlaceAddRequest;
import haru.harudongseon.routeplace.domain.RoutePlace;
import haru.harudongseon.routeplace.domain.RoutePlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoutePlaceService {

    private final RoutePlaceRepository routePlaceRepository;

    public void addPlace(final RoutePlaceAddRequest request) {
        final Optional<RoutePlace> optionalPlace = routePlaceRepository.findByProviderPlaceId(request.providerPlaceId());
        if (optionalPlace.isPresent()) {
            final RoutePlace routePlace = optionalPlace.get();
            routePlace.select();
        }

        if (optionalPlace.isEmpty()) {
            final RoutePlace routePlace = new RoutePlace(request.providerPlaceId(), request.name(), request.category(),
                    request.latitude(), request.longitude(), request.addressName());
            routePlaceRepository.save(routePlace);
        }
    }
}
