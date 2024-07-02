package haru.harudongseon.route.application;

import static haru.harudongseon.common.fixtures.RouteFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.PlaceBuilder;
import haru.harudongseon.common.builder.RouteBuilder;
import haru.harudongseon.common.builder.RouteTagBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.route.application.dto.*;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.route.domain.RouteRepository;
import haru.harudongseon.route.domain.SelectedTag;
import haru.harudongseon.routetag.domain.RouteTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * 캐싱 테스트 : Service 계층에서 @Cacheable이 사용되어서 Service 계층을 직접 Mocking하는 것은 불가능
 * 그래서 Service 계층의 캐시 메소드는 실제 객체로 동작하게 하고, 그 안의 Repository가 1번만 호출되는지 검증하여 캐싱 판단
 */
public class RouteServiceCacheTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private RouteBuilder routeBuilder;

    @Autowired
    private RouteTagBuilder routeTagBuilder;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RouteService routeService;

    @SpyBean
    private RouteRepository routeRepository;

    @Nested
    @DisplayName("기간 조회 캐싱 관련 테스트")
    class FindRoutePeriodCacheTest {

        Member member = memberBuilder.defaultMember().build();
        Route route = routeBuilder.defaultRoute(member).build();
        LocalDate date = route.getDate();
        LocalDate startDate = route.getDate().withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
        String cacheName = "routes";
        String cacheKey = member.getId() + ":" + YearMonth.of(date.getYear(), date.getMonth());

        @BeforeEach
        void setUp() {
            cacheManager.getCache(cacheName).clear();
            final List<Route> routes = List.of(route);
            given(routeRepository.findByMemberIdAndPeriod(member.getId(), startDate, endDate)).willReturn(routes);
        }

        /**
         * 캐시 키 : memberId:Year-Month
         * ex : 10:2024-06
         * 테스트 해야하는 상황
         * 1. 첫 조회 요청 시에는 데이터를 캐시에서 가져오지 않고, 가져온 후 캐시에 저장한다.
         * 2. 첫 조회 이후에는 캐시에 데이터가 저장되고, 캐시에서 데이터를 가져온다. (메소드 1번만 호출, 이후에는 프록시 객체에서 결과 반환)
         * 3. 동선 생성, 편집, 삭제 시에 캐시가 무효화된다.
         * 4.
         */


        @Test
        @DisplayName("첫 동선 기간 조회 시에는 데이터를 캐시에서 가져오지 않고, 가져온 후 캐시에 저장한다.")
        void not_get_data_from_cache_when_first_find_route_period_and_after_get_save_cache() {
            // given
            boolean isBeforeCacheExist = true;
            if (cacheManager.getCache(cacheName).get(cacheKey) == null) {
                isBeforeCacheExist = false;
            }

            // when
            final RoutesResponse response = routeService.findRouteByPeriod(member.getId(), startDate, endDate);

            final Cache afterCache = cacheManager.getCache(cacheName);
            final Object afterCacheValue = afterCache.get(cacheKey).get();

            // then
            assertThat(isBeforeCacheExist).isFalse();
            assertSoftly(softly -> {
                then(routeRepository).should(times(1)).findByMemberIdAndPeriod(member.getId(), startDate, endDate);
                softly.assertThat(afterCacheValue).isNotNull();
                softly.assertThat(afterCacheValue).usingRecursiveComparison().isEqualTo(response);
            });
        }

        @Test
        @DisplayName("첫 동선 기간 조회 이후 조회 시에는 캐시에 데이터가 저장되고, 캐시에서 데이터를 가져온다. (메소드 1번만 호출)")
        void save_cache_and_find_from_cache_after_first_find_route_period() {
            // when
            final RoutesResponse response = routeService.findRouteByPeriod(member.getId(), startDate, endDate);

            IntStream.range(0, 10)
                    .forEach((i) -> routeService.findRouteByPeriod(member.getId(), startDate, endDate));
            final Cache afterCache = cacheManager.getCache(cacheName);
            final Object afterCacheValue = afterCache.get(cacheKey).get();

            // then
            assertSoftly(softly -> {
                softly.assertThat(afterCacheValue).usingRecursiveComparison().isEqualTo(response);
                then(routeRepository).should(times(1)).findByMemberIdAndPeriod(member.getId(), startDate, endDate);
            });
        }

        @Test
        @DisplayName("동선 생성 시 캐시가 무효화된다.")
        void invalidate_cache_when_route_add() {
            // given
            routeService.findRouteByPeriod(member.getId(), startDate, endDate);

            // when
            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            defaultPlace2Builder.build();

            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(date.plusDays(1), 기본_동선_제목, List.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

            routeService.addRoute(member.getId(), routeAddRequest);

            boolean isCacheExist = true;
            if (cacheManager.getCache(cacheName).get(cacheKey) == null) {
                isCacheExist = false;
            }

            // then
            assertThat(isCacheExist).isFalse();
        }

        @Test
        @DisplayName("동선 편집 날짜의 연월이 같은(캐시 키가 같은) 동선 편집 시 캐시가 무효화된다.")
        void invalidate_cache_when_same_year_month_route_edit() {
            // given
            routeService.findRouteByPeriod(member.getId(), startDate, endDate);

            final LocalDate newDate = route.getDate();
            System.out.println("newDate = " + newDate);
            final String newTitle = "NEW " + route.getTitle();
            final List<String> newTagNames = route.getTags().stream()
                    .map(SelectedTag::getRouteTag)
                    .map(RouteTag::getName)
                    .map(name -> "NEW " + name)
                    .toList();
            final String newMoveWays = "자전거/" + route.getMoveWays();

            final RouteEditRequest request =
                    new RouteEditRequest(newDate, newTitle, newTagNames, newMoveWays, Collections.emptyList());

            // when
            routeService.editRoute(member.getId(), route.getId(), request);

            boolean isCacheExist = true;
            if (cacheManager.getCache(cacheName).get(cacheKey) == null) {
                isCacheExist = false;
            }

            // then
            assertThat(isCacheExist).isFalse();
        }

        @Test
        @DisplayName("동선 편집 날짜의 연월이 다른(캐시 키가 같은) 동선 편집 시 기존 연월 캐시와 편집 날짜의 연월 캐시도 무효화된다.")
        void invalidate_cache_when_different_year_month_route_edit() {
            // given
            final LocalDate newDate = route.getDate().plusMonths(1);
            LocalDate newStartDate = newDate.withDayOfMonth(1);
            LocalDate newEndDate = newDate.withDayOfMonth(newDate.lengthOfMonth());
            String newCacheKey = member.getId() + ":" + YearMonth.of(newDate.getYear(), newDate.getMonth());

            routeService.findRouteByPeriod(member.getId(), startDate, endDate);
            routeService.findRouteByPeriod(member.getId(), newStartDate, newEndDate);

            System.out.println("newDate = " + newDate);
            final String newTitle = "NEW " + route.getTitle();
            final List<String> newTagNames = route.getTags().stream()
                    .map(SelectedTag::getRouteTag)
                    .map(RouteTag::getName)
                    .map(name -> "NEW " + name)
                    .toList();
            final String newMoveWays = "자전거/" + route.getMoveWays();

            final RouteEditRequest request =
                    new RouteEditRequest(newDate, newTitle, newTagNames, newMoveWays, Collections.emptyList());

            // when
            routeService.editRoute(member.getId(), route.getId(), request);

            boolean isCache1Exist = true;
            boolean isCache2Exist = true;
            if (cacheManager.getCache(cacheName).get(cacheKey) == null) {
                isCache1Exist = false;
            }
            if (cacheManager.getCache(cacheName).get(newCacheKey) == null) {
                isCache2Exist = false;
            }

            // then
            assertThat(isCache1Exist).isFalse();
            assertThat(isCache2Exist).isFalse();
        }

        @Test
        @DisplayName("동선 삭제 시 캐시가 무효화된다.")
        void invalidate_cache_when_route_delete() {
            // given
            routeService.findRouteByPeriod(member.getId(), startDate, endDate);
            final RouteDeleteRequest request = new RouteDeleteRequest(route.getDate(), route.getId());

            // when
            routeService.deleteRoute(member.getId(), request);

            boolean isCacheExist = true;
            if (cacheManager.getCache(cacheName).get(cacheKey) == null) {
                isCacheExist = false;
            }

            // then
            assertThat(isCacheExist).isFalse();
        }
    }
}
