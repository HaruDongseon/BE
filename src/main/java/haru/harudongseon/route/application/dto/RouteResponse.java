package haru.harudongseon.route.application.dto;

import java.util.List;

import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routetag.domain.RouteTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RouteResponse {

    @Schema(description = "동선 ID", example = "1")
    private Long id;

    @Schema(description = "동선 날짜", example = "2023-05-24")
    private String date;

    @Schema(description = "동선 제목", example = "하루동선1")
    private String title;

    @Schema(description = "동선 태그 리스트", example = "[\"데이트\", \"맛집\"]")
    private List<String> tag;

    @Schema(description = "동선 이동수단", example = "자동차/대중교통")
    private String moveWays;

    @Schema(description = "동선 장소 리스트")
    private List<RoutePlaceResponse> routePlaces;

    private RouteResponse(final Long id, final String date,
                          final String title, final List<String> tag,
                          final String moveWays, final List<RoutePlaceResponse> routePlaces) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.tag = tag;
        this.moveWays = moveWays;
        this.routePlaces = routePlaces;
    }

    public static RouteResponse from(final Route route) {
        final Long id = route.getId();
        final String date = route.getDate().toString();
        final String title = route.getTitle();
        final String moveWays = route.getMoveWays();

        final List<String> tag = route.getTags().stream()
                .map(selectedTag -> {
                    final RouteTag routeTag = selectedTag.getRouteTag();
                    return routeTag.getName();
                }).toList();

        final List<RoutePlaceResponse> routePlaces = route.getRoutePlaces().stream()
                .map(RoutePlaceResponse::from)
                .toList();

        return new RouteResponse(id, date, title, tag, moveWays, routePlaces);
    }
}
