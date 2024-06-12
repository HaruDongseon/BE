package haru.harudongseon.routestorage.domain;

import static haru.harudongseon.common.fixtures.MemberFixtures.기본_회원_도메인;
import static haru.harudongseon.common.fixtures.PlaceFixtures.기본_장소_도메인;
import static haru.harudongseon.common.fixtures.RouteFixtures.기본_동선_도메인;
import static haru.harudongseon.common.fixtures.RouteStorageFixtures.기본_동선_보관함_이름;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_동선_태그_도메인;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routestorage.exception.RouteStorageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RouteStorageTest {

    @Nested
    @DisplayName("동선 보관함에 있는 동선들 삭제 시")
    class RemoveRoutes {

        @Test
        @DisplayName("삭제에 성공한다.")
        void success() {
            // given
            final Member member = 기본_회원_도메인(1L);
            final RouteStorage routeStorage = new RouteStorage(member, 기본_동선_보관함_이름);
            final Route route1 = 기본_동선_도메인(1L, member, List.of(기본_동선_태그_도메인(1L)), List.of(기본_장소_도메인(1L)));
            final Route route2 = 기본_동선_도메인(2L, member, List.of(기본_동선_태그_도메인(1L)), List.of(기본_장소_도메인(1L)));
            final Route route3 = 기본_동선_도메인(3L, member, List.of(기본_동선_태그_도메인(1L)), List.of(기본_장소_도메인(1L)));
            routeStorage.addRoutes(List.of(route1, route2, route3));

            final List<Long> removeRouteIds = List.of(route1.getId(), route3.getId());

            // when
            routeStorage.removeRoutes(removeRouteIds);
            final List<StoredRoute> routesAfterRemove = routeStorage.getRoutes();

            // then
            assertSoftly(softly -> {
                softly.assertThat(routesAfterRemove.size()).isEqualTo(1);
                softly.assertThat(routesAfterRemove.get(0).getRoute().getId()).isEqualTo(route2.getId());
            });
        }

        @Test
        @DisplayName("삭제할 동선이 동선 보관함에 있는 동선이 아니면 예외가 발생한다.")
        void throws_not_exist_route() {
            // given
            final Member member = 기본_회원_도메인(1L);
            final RouteStorage routeStorage = new RouteStorage(member, 기본_동선_보관함_이름);
            final Route route1 = 기본_동선_도메인(1L, member, List.of(기본_동선_태그_도메인(1L)), List.of(기본_장소_도메인(1L)));
            final Route route2 = 기본_동선_도메인(2L, member, List.of(기본_동선_태그_도메인(1L)), List.of(기본_장소_도메인(1L)));
            final Route route3 = 기본_동선_도메인(3L, member, List.of(기본_동선_태그_도메인(1L)), List.of(기본_장소_도메인(1L)));
            routeStorage.addRoutes(List.of(route1, route2));

            final List<Long> removeRouteIds = List.of(route1.getId(), route3.getId());

            // when & then
            assertThatThrownBy(() -> routeStorage.removeRoutes(removeRouteIds))
                    .isInstanceOf(RouteStorageException.NotExistRouteException.class)
                    .hasMessage("동선 보관함에 해당하는 동선이 존재하지 않습니다.");
        }
    }
}
