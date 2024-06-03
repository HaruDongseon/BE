package haru.harudongseon.routestorage.application.dto;

import java.util.List;

import haru.harudongseon.routestorage.domain.StoredRoute;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredRoutesResponse {

    private List<StoredRouteResponse> storedRoutes;

    private StoredRoutesResponse(final List<StoredRouteResponse> storedRoutes) {
        this.storedRoutes = storedRoutes;
    }

    public static StoredRoutesResponse from(final List<StoredRoute> storedRoutes) {
        final List<StoredRouteResponse> storedRouteResponses = storedRoutes.stream()
                .map(StoredRouteResponse::from)
                .toList();

        return new StoredRoutesResponse(storedRouteResponses);
    }
}
