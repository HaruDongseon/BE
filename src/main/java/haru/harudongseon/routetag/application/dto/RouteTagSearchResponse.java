package haru.harudongseon.routetag.application.dto;

import java.util.List;

import haru.harudongseon.routetag.domain.RouteTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteTagSearchResponse {

    private List<RouteTagResponse> routeTags;

    private RouteTagSearchResponse(final List<RouteTagResponse> routeTags) {
        this.routeTags = routeTags;
    }

    public static RouteTagSearchResponse from(List<RouteTag> routeTags) {
        return new RouteTagSearchResponse(
                routeTags.stream()
                        .map(RouteTagResponse::from)
                        .toList()
        );
    }
}
