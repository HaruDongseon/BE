package haru.harudongseon.route.application.dto;

import java.util.List;

import haru.harudongseon.route.domain.Route;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoutesResponse {

    private List<RouteResponse> routes;

    private RoutesResponse(final List<RouteResponse> routes) {
        this.routes = routes;
    }

    public static RoutesResponse from(final List<Route> routes) {
        final List<RouteResponse> routeResponses = routes.stream()
                .map(RouteResponse::from)
                .toList();

        return new RoutesResponse(routeResponses);
    }
}
