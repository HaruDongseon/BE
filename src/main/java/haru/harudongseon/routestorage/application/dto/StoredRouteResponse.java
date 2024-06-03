package haru.harudongseon.routestorage.application.dto;

import java.time.LocalDate;
import java.util.List;

import haru.harudongseon.route.domain.Route;
import haru.harudongseon.route.domain.SelectedTag;
import haru.harudongseon.routestorage.domain.StoredRoute;
import haru.harudongseon.routetag.domain.RouteTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredRouteResponse {

    @Schema(description = "보관 동선 ID", example = "1")
    private Long id;

    @Schema(description = "보관 동선 이름", example = "부평 데이트")
    private String title;

    @Schema(description = "보관 동선 날짜", example = "2024-04-15")
    private LocalDate date;

    @Schema(description = "보관 동선 태그 이름 리스트", example = "[\"데이트\", \"부평\"]")
    private List<String> tagNames;

    private StoredRouteResponse(final Long id, final String title,
                                final LocalDate date, final List<String> tagNames) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.tagNames = tagNames;
    }

    public static StoredRouteResponse from(final StoredRoute storedRoute) {
        final Route route = storedRoute.getRoute();
        final Long id = route.getId();
        final String title = route.getTitle();
        final LocalDate date = route.getDate();
        final List<String> tagNames = route.getTags().stream()
                .map(SelectedTag::getRouteTag)
                .map(RouteTag::getName)
                .toList();

        return new StoredRouteResponse(id, title, date, tagNames);
    }
}
