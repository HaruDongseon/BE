package haru.harudongseon.routetag.application.dto;

import haru.harudongseon.routetag.domain.RouteTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteTagResponse {

    private Long id;
    private String name;
    private Long selectCount;

    private RouteTagResponse(final Long id, final String name, final Long selectCount) {
        this.id = id;
        this.name = name;
        this.selectCount = selectCount;
    }

    public static RouteTagResponse from(final RouteTag routeTag) {
        final Long id = routeTag.getId();
        final String name = routeTag.getName();
        final Long selectCount = routeTag.getSelectCount();
        return new RouteTagResponse(id, name, selectCount);
    }
}
