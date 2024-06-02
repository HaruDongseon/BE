package haru.harudongseon.routestorage.application.dto;

import java.util.List;

import haru.harudongseon.routestorage.domain.RouteStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStoragesResponse {

    List<RouteStorageResponse> routeStorages;

    private RouteStoragesResponse(final List<RouteStorageResponse> routeStorages) {
        this.routeStorages = routeStorages;
    }

    public static RouteStoragesResponse from(final List<RouteStorage> routeStorages) {
        final List<RouteStorageResponse> routeStorageResponses = routeStorages.stream()
                .map(RouteStorageResponse::from)
                .toList();

        return new RouteStoragesResponse(routeStorageResponses);
    }
}
