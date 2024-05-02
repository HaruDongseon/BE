package haru.harudongseon.route.application;

import static haru.harudongseon.common.fixtures.RouteFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Optional;
import java.util.Set;

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
        @DisplayName("추가할 태그가 존재하지 않는다면, 태그를 저장하고 동선 추가에 성공한다.")
        void success_when_not_exist_tag_save_tag() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

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
        @DisplayName("추가할 장소가 존재하지 않는다면, 장소를 추가하고 동선 성공에 성공한다.")
        void success_when_not_exist_place_save_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, routePlaceDtos);

            final Optional<Place> beforePlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> beforePlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());

            // when
            final Long savedRouteId = routeService.addRoute(member.getId(), routeAddRequest);
            final Optional<Place> afterPlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> afterPlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(beforePlace1.isEmpty()).isTrue();
                softly.assertThat(beforePlace2.isEmpty()).isTrue();
                softly.assertThat(afterPlace1.isPresent()).isTrue();
                softly.assertThat(afterPlace2.isPresent()).isTrue();
                softly.assertThat(savedRouteId).isNotNull();
            });
        }

        @Test
        @DisplayName("추가할 태그와 장소가 모두 존재하지 않는다면, 태그와 장소를 저장하고 동선 추가에 성공한다.")
        void success_when_not_exist_tag_and_place_save_tag_and_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

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
        @DisplayName("추가할 태그와 장소가 모두 존재한다면, 저장하지 않고 조회하여 동선 추가에 성공한다.")
        void success_when_exist_all_find_tag_and_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, routePlaceDtos);

            final Optional<Place> beforePlace1 = placeRepository.findByProviderPlaceId(routePlaceDto1.providerPlaceId());
            final Optional<Place> beforePlace2 = placeRepository.findByProviderPlaceId(routePlaceDto2.providerPlaceId());
            final Optional<RouteTag> beforeRouteTag1 = routeTagRepository.findByName(routeTag1.getName());
            final Optional<RouteTag> beforeRouteTag2 = routeTagRepository.findByName(routeTag2.getName());

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
                softly.assertThat(savedRouteId).isNotNull();
            });
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

            final Long notExistMemberId = -1L;

            // when & then
            assertThatThrownBy(() -> routeService.addRoute(notExistMemberId, routeAddRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버를 찾을 수 없습니다.");
        }
    }
}
