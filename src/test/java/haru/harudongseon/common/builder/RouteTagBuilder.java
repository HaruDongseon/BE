package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_태그_선택_횟수;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_태그_이름;

import haru.harudongseon.routetag.domain.RouteTag;
import haru.harudongseon.routetag.domain.RouteTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RouteTagBuilder {

    private final RouteTagRepository routeTagRepository;

    private String name;
    private Long selectCount;

    @Autowired
    public RouteTagBuilder(final RouteTagRepository routeTagRepository) {
        this.routeTagRepository = routeTagRepository;
    }

    public RouteTagBuilder defaultRouteTag() {
        this.name = 기본_태그_이름;
        this.selectCount = 기본_태그_선택_횟수;

        return this;
    }

    public RouteTagBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public RouteTagBuilder selectCount(final Long selectCount) {
        this.selectCount = selectCount;
        return this;
    }

    public RouteTag build() {
        final RouteTag routeTag = new RouteTag(name, selectCount);
        return routeTagRepository.save(routeTag);
    }
}
