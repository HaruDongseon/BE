package haru.harudongseon.routestorage.application;

import static haru.harudongseon.common.fixtures.RouteStorageFixtures.기본_동선_보관함_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.RouteBuilder;
import haru.harudongseon.common.builder.RouteStorageBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routestorage.application.dto.RouteDeleteRequest;
import haru.harudongseon.routestorage.application.dto.RouteStorageAddRequest;
import haru.harudongseon.routestorage.application.dto.RouteStoragesResponse;
import haru.harudongseon.routestorage.domain.RouteStorage;
import haru.harudongseon.routestorage.exception.RouteStorageException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RouteStorageServiceTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private RouteBuilder routeBuilder;

    @Autowired
    private RouteStorageBuilder routeStorageBuilder;

    @Autowired
    private RouteStorageService routeStorageService;

    @Nested
    @DisplayName("동선 보관함 추가 시")
    class AddRouteStorage {

        @Test
        @DisplayName("추가에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final RouteStorageAddRequest request = new RouteStorageAddRequest(기본_동선_보관함_이름);

            // when
            final Long savedRouteStorageId = routeStorageService.addRouteStorage(member.getId(), request);

            // then
            assertThat(savedRouteStorageId).isNotNull();
        }

        @Test
        @DisplayName("중복되는 동선 보관함 이름이 존재하면 예외가 발생한다.")
        void throws_duplicate_name() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(Collections.emptyList());
            final RouteStorageAddRequest duplicateRequest = new RouteStorageAddRequest(기본_동선_보관함_이름);

            // when & then
            assertThatThrownBy(() -> routeStorageService.addRouteStorage(member.getId(), duplicateRequest))
                    .isInstanceOf(RouteStorageException.DuplicateException.class)
                    .hasMessage("중복된 이름을 가진 회원의 동선 보관함이 이미 존재합니다.");
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;
            final RouteStorageAddRequest duplicateRequest = new RouteStorageAddRequest(기본_동선_보관함_이름);

            // when & then
            assertThatThrownBy(() -> routeStorageService.addRouteStorage(notExistMemberId, duplicateRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버가 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("동선 보관함에 있는 동선 삭제 시")
    class DeleteRoute {

        @Test
        @DisplayName("삭제에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route route3 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2, route3)));

            final RouteDeleteRequest request = new RouteDeleteRequest(routeStorage.getId(), List.of(route1.getId(), route3.getId()));

            // when & then
            assertDoesNotThrow(() -> routeStorageService.deleteRouteStorage(member.getId(), request));
        }

        @Test
        @DisplayName("동선 보관함 ID에 해당하는 동선 보관함이 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_route_storage() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route route3 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2, route3)));

            final Long notExistRouteStorageId = -1L;
            final RouteDeleteRequest request = new RouteDeleteRequest(notExistRouteStorageId, List.of(route1.getId(), route3.getId()));

            // when & then
            assertThatThrownBy(() -> routeStorageService.deleteRouteStorage(member.getId(), request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 동선 보관함이 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("동선 보관함 이름 조회 시")
    class FindRouteStorageNames{

        @Test
        @DisplayName("조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage1 = routeStorageBuilder.defaultRouteStorage(member).name("데이트").build(new ArrayList<>(List.of(route1)));
            final RouteStorage routeStorage2 = routeStorageBuilder.defaultRouteStorage(member).name("보드게임 동아리").build(new ArrayList<>(List.of(route2)));

            final RouteStoragesResponse expected = RouteStoragesResponse.from(List.of(routeStorage1, routeStorage2));

            // when
            final RouteStoragesResponse actual = routeStorageService.findRouteStorageNames(member.getId());

            // then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;

            // when & then
            assertThatThrownBy(() -> routeStorageService.findRouteStorageNames(notExistMemberId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버가 존재하지 않습니다.");
        }
    }
}
