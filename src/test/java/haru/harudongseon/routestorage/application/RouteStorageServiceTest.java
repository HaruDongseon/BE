package haru.harudongseon.routestorage.application;

import static haru.harudongseon.common.fixtures.RouteStorageFixtures.기본_동선_보관함_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.RouteStorageBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.routestorage.application.dto.RouteStorageAddRequest;
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
}
