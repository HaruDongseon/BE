package haru.harudongseon.route.application;

import static haru.harudongseon.common.fixtures.RouteFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.PlaceBuilder;
import haru.harudongseon.common.builder.RouteBuilder;
import haru.harudongseon.common.builder.RouteTagBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.PlaceRepository;
import haru.harudongseon.route.application.dto.RouteAddRequest;
import haru.harudongseon.route.application.dto.RoutePlaceDto;
import haru.harudongseon.route.application.dto.RouteResponse;
import haru.harudongseon.route.application.dto.RoutesResponse;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routetag.domain.RouteTag;
import haru.harudongseon.routetag.domain.RouteTagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class
RouteServiceTest extends ServiceTest {

    @Autowired
    private RouteService routeService;

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private RouteBuilder routeBuilder;

    @Autowired
    private RouteTagBuilder routeTagBuilder;

    @Autowired
    private RouteTagRepository routeTagRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Nested
    @DisplayName("동선 추가 시")
    class AddRoute {

        @Test
        @DisplayName("추가할 장소는 존재하고 태그가 존재하지 않는다면, 태그를 저장하고 동선 추가에 성공한다.")
        void success_when_not_exist_tag_save_tag() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, List.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

            final Optional<RouteTag> beforeRouteTag1 = routeTagRepository.findByName(기본_동선_태그1);
            final Optional<RouteTag> beforeRouteTag2 = routeTagRepository.findByName(기본_동선_태그2);

            // when
            final Long savedRouteId = routeService.addRoute(member.getId(), routeAddRequest);
            final Optional<RouteTag> afterRouteTag1 = routeTagRepository.findByName(기본_동선_태그1);
            final Optional<RouteTag> afterRouteTag2 = routeTagRepository.findByName(기본_동선_태그2);

            // then
            assertSoftly(softly -> {
                softly.assertThat(beforeRouteTag1.isEmpty()).isTrue();
                softly.assertThat(beforeRouteTag2.isEmpty()).isTrue();
                softly.assertThat(afterRouteTag1.isPresent()).isTrue();
                softly.assertThat(afterRouteTag2.isPresent()).isTrue();
                softly.assertThat(savedRouteId).isNotNull();
            });
        }

        @Test
        @DisplayName("추가할 태그는 존재하고 장소가 존재하지 않는다면, 장소를 추가하고 해당 태그 선택 횟수를 1 증가시킨 후 동선 성공에 성공한다.")
        void success_when_not_exist_place_tag_select_count_1_increase_save_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

            final Optional<Place> beforePlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> beforePlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());
            final Long beforeRouteTag1SelectCount = routeTag1.getSelectCount();
            final Long beforeRouteTag2SelectCount = routeTag2.getSelectCount();

            // when
            final Long savedRouteId = routeService.addRoute(member.getId(), routeAddRequest);
            final Optional<Place> afterPlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> afterPlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());
            final RouteTag afterRouteTag1 = routeTagRepository.findByName(routeTag1.getName()).get();
            final RouteTag afterRouteTag2 = routeTagRepository.findByName(routeTag2.getName()).get();

            // then
            assertSoftly(softly -> {
                softly.assertThat(beforePlace1.isEmpty()).isTrue();
                softly.assertThat(beforePlace2.isEmpty()).isTrue();
                softly.assertThat(afterPlace1.isPresent()).isTrue();
                softly.assertThat(afterPlace2.isPresent()).isTrue();
                softly.assertThat(afterRouteTag1.getSelectCount()).isEqualTo(beforeRouteTag1SelectCount + 1);
                softly.assertThat(afterRouteTag2.getSelectCount()).isEqualTo(beforeRouteTag2SelectCount + 1);
                softly.assertThat(savedRouteId).isNotNull();
            });
        }

        @Test
        @DisplayName("추가할 태그와 장소가 모두 존재하지 않는다면, 태그와 장소를 저장하고 동선 추가에 성공한다.")
        void success_when_not_exist_tag_and_place_save_tag_and_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

            final Optional<Place> beforePlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> beforePlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());
            final Optional<RouteTag> beforeRouteTag1 = routeTagRepository.findByName(기본_동선_태그1);
            final Optional<RouteTag> beforeRouteTag2 = routeTagRepository.findByName(기본_동선_태그2);

            // when
            final Long savedRouteId = routeService.addRoute(member.getId(), routeAddRequest);
            final Optional<RouteTag> afterRouteTag1 = routeTagRepository.findByName(기본_동선_태그1);
            final Optional<RouteTag> afterRouteTag2 = routeTagRepository.findByName(기본_동선_태그2);
            final Optional<Place> afterPlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> afterPlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(beforeRouteTag1.isEmpty()).isTrue();
                softly.assertThat(beforeRouteTag2.isEmpty()).isTrue();
                softly.assertThat(beforePlace1.isEmpty()).isTrue();
                softly.assertThat(beforePlace2.isEmpty()).isTrue();
                softly.assertThat(afterRouteTag1.isPresent()).isTrue();
                softly.assertThat(afterRouteTag2.isPresent()).isTrue();
                softly.assertThat(afterPlace1.isPresent()).isTrue();
                softly.assertThat(afterPlace2.isPresent()).isTrue();
                softly.assertThat(savedRouteId).isNotNull();
            });
        }

        @Test
        @DisplayName("추가할 태그와 장소가 모두 존재한다면, 저장하지 않고 조회하고 해당 태그 선택 횟수를 1 증가시킨 후 동선 추가에 성공한다.")
        void success_when_exist_all_find_tag_and_place_and_tag_increase_1_select_count() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

            final Optional<Place> beforePlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> beforePlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());
            final Optional<RouteTag> beforeRouteTag1 = routeTagRepository.findByName(routeTag1.getName());
            final Optional<RouteTag> beforeRouteTag2 = routeTagRepository.findByName(routeTag2.getName());
            final Long beforeRouteTag1SelectCount = beforeRouteTag1.get().getSelectCount();
            final Long beforeRouteTag2SelectCount = beforeRouteTag2.get().getSelectCount();


            // when
            final Long savedRouteId = routeService.addRoute(member.getId(), routeAddRequest);
            final Optional<RouteTag> afterRouteTag1 = routeTagRepository.findByName(routeTag1.getName());
            final Optional<RouteTag> afterRouteTag2 = routeTagRepository.findByName(routeTag2.getName());
            final Optional<Place> afterPlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> afterPlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(beforeRouteTag1.isPresent()).isTrue();
                softly.assertThat(beforeRouteTag2.isPresent()).isTrue();
                softly.assertThat(beforePlace1.isPresent()).isTrue();
                softly.assertThat(beforePlace2.isPresent()).isTrue();
                softly.assertThat(afterRouteTag1.get().getId()).isEqualTo(beforeRouteTag1.get().getId());
                softly.assertThat(afterRouteTag2.get().getId()).isEqualTo(beforeRouteTag2.get().getId());
                softly.assertThat(afterPlace1.get().getId()).isEqualTo(beforePlace1.get().getId());
                softly.assertThat(afterPlace2.get().getId()).isEqualTo(beforePlace2.get().getId());
                softly.assertThat(afterRouteTag1.get().getSelectCount()).isEqualTo(beforeRouteTag1SelectCount + 1);
                softly.assertThat(afterRouteTag2.get().getSelectCount()).isEqualTo(beforeRouteTag2SelectCount + 1);
                softly.assertThat(savedRouteId).isNotNull();
            });
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

            final Long notExistMemberId = -1L;

            // when & then
            assertThatThrownBy(() -> routeService.addRoute(notExistMemberId, routeAddRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("동선 조회 시")
    class FindRoute {

        @Test
        @DisplayName("동선 조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();


            final Route route = routeBuilder.defaultRoute(member).build();
            route.addTag(routeTag1);
            route.addTag(routeTag2);
            route.addPlace(place1);
            route.addPlace(place2);

            final RouteResponse expected = RouteResponse.from(route);

            // when
            final RouteResponse response = routeService.findRoute(route.getId(), member.getId());

            // then
            assertThat(response).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member_id_and_route_id() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Route route = routeBuilder.defaultRoute(member).build();

            final Long notExistMemberId = -1L;
            final Long notExistRouteId = -1L;

            // when & then
            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> routeService.findRoute(route.getId(), notExistMemberId))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
                softly.assertThatThrownBy(() -> routeService.findRoute(notExistRouteId, member.getId()))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
                softly.assertThatThrownBy(() -> routeService.findRoute(notExistRouteId, notExistMemberId))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
            });
        }
    }

    @Nested
    @DisplayName("동선 기간 조회 시")
    class FindRouteByPeriod{

        @Test
        @DisplayName("월별 날짜 오름차순 조회에 성공한다.")
        void success_monthly_order_by_date_asc() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final Route firstOfMonthRoute = routeBuilder.defaultRoute(member).date(동선_5월_첫날_날짜).build();
            final Route lastOfMonthRoute = routeBuilder.defaultRoute(member).date(동선_5월_마지막날_날짜).build();
            final Route middleOfMonthRoute = routeBuilder.defaultRoute(member).build();

            final LocalDate startDate = LocalDate.of(기본_동선_날짜.getYear(), 기본_동선_날짜.getMonth(), 1);
            final LocalDate endDate = LocalDate.of(기본_동선_날짜.getYear(), 기본_동선_날짜.getMonth(), 31);

            final RoutesResponse expected = RoutesResponse.from(List.of(firstOfMonthRoute, middleOfMonthRoute, lastOfMonthRoute));

            // when
            final RoutesResponse actual = routeService.findRouteByPeriod(member.getId(), startDate, endDate);

            // then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("주간 날짜 오름차순 조회에 성공한다.")
        void success_weekly_order_by_date_asc() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final Route lastOfWeekRoute = routeBuilder.defaultRoute(member).date(동선_5월_둘째주_마지막날_날짜).build();
            final Route firstOfWeekRoute = routeBuilder.defaultRoute(member).date(동선_5월_둘째주_첫날_날짜).build();
            final Route middleOfWeekRoute = routeBuilder.defaultRoute(member).date(동선_5월_둘째주_중간_날짜).build();

            final LocalDate startDate = LocalDate.of(동선_5월_둘째주_첫날_날짜.getYear(), 동선_5월_둘째주_첫날_날짜.getMonth(), 동선_5월_둘째주_첫날_날짜.getDayOfMonth());
            final LocalDate endDate = LocalDate.of(동선_5월_둘째주_마지막날_날짜.getYear(), 동선_5월_둘째주_마지막날_날짜.getMonth(), 동선_5월_둘째주_마지막날_날짜.getDayOfMonth());

            final RoutesResponse expected = RoutesResponse.from(List.of(firstOfWeekRoute, middleOfWeekRoute, lastOfWeekRoute));

            // when
            final RoutesResponse actual = routeService.findRouteByPeriod(member.getId(), startDate, endDate);

            // then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        }
    }
}
