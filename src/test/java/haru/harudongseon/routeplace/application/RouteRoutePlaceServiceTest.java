package haru.harudongseon.routeplace.application;

import static haru.harudongseon.common.fixtures.RoutePlaceFixtures.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.RoutePlaceBuilder;
import haru.harudongseon.routeplace.application.dto.RoutePlaceAddRequest;
import haru.harudongseon.routeplace.domain.RoutePlace;
import haru.harudongseon.routeplace.domain.RoutePlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RouteRoutePlaceServiceTest extends ServiceTest {

    @Autowired
    private RoutePlaceService routePlaceService;

    @Autowired
    private RoutePlaceBuilder routePlaceBuilder;

    @Autowired
    private RoutePlaceRepository routePlaceRepository;

    @Nested
    @DisplayName("장소 추가 시")
    class AddRoutePlace {

        @Test
        @DisplayName("기존에 장소가 없을 때는 장소가 추가된다.")
        void success_not_exist_place_add_place() {
            // given
            final String providerPlaceId = 기본_외부_공급자_ID;
            final RoutePlaceAddRequest request = new RoutePlaceAddRequest(providerPlaceId,
                                                                기본_장소_이름,
                                                                기본_장소_카테고리,
                                                                기본_장소_위도,
                                                                기본_장소_경도,
                                                                기본_장소_주소_이름);
            final long countBeforeAdd = routePlaceRepository.count();
            final RoutePlace expected = 기본_장소_Entity();

            // when
            routePlaceService.addPlace(request);
            final long countAfterAdd = routePlaceRepository.count();
            final RoutePlace actual = routePlaceRepository.findByProviderPlaceId(providerPlaceId).get();

            // then
            assertSoftly(softly -> {
                softly.assertThat(countBeforeAdd).isEqualTo(0);
                softly.assertThat(countAfterAdd).isEqualTo(1);
                softly.assertThat(actual).usingRecursiveComparison()
                        .ignoringFieldsOfTypes(LocalDateTime.class)
                        .ignoringFields("id")
                        .isEqualTo(expected);
            });
        }

        @Test
        @DisplayName("기존에 장소가 있을 때는 기존 장소에 선택 카운트가 1 증가한다.")
        void success_exist_place_select_count_1_increase() {
            // given
            final RoutePlace expected = routePlaceBuilder.defaultPlace().build();
            final String providerPlaceId = 기본_외부_공급자_ID;
            final RoutePlaceAddRequest request = new RoutePlaceAddRequest(providerPlaceId,
                                                                기본_장소_이름,
                                                                기본_장소_카테고리,
                                                                기본_장소_위도,
                                                                기본_장소_경도,
                                                                기본_장소_주소_이름);
            final int beforeSelectCount = expected.getSelectCount();

            // when
            routePlaceService.addPlace(request);
            final RoutePlace actual = routePlaceRepository.findByProviderPlaceId(providerPlaceId).get();

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.getSelectCount()).isEqualTo(beforeSelectCount + 1);
                softly.assertThat(actual).usingRecursiveComparison()
                        .ignoringFields("selectCount")
                        .isEqualTo(expected);
            });
        }
    }
}
