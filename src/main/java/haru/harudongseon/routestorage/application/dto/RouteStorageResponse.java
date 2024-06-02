package haru.harudongseon.routestorage.application.dto;

import haru.harudongseon.routestorage.domain.RouteStorage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStorageResponse {

    @Schema(description = "동선 보관함 ID", example = "1")
    private Long id;

    @Schema(description = "동선 보관함 이름", example = "카페")
    private String name;

    private RouteStorageResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static RouteStorageResponse from(final RouteStorage routeStorage) {
        final Long id = routeStorage.getId();
        final String name = routeStorage.getName();

        return new RouteStorageResponse(id, name);
    }
}
