package haru.harudongseon.routetag.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import haru.harudongseon.common.builder.RouteTagBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(RouteTagBuilder.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RouteTagRepositoryTest {

    @Autowired
    private RouteTagRepository routeTagRepository;

    @Autowired
    private RouteTagBuilder routeTagBuilder;

    @Nested
    @DisplayName("키워드로 동선 태그 검색 쿼리 실행 시 ")
    class findByKeywordContainingIgnoreCaseAndOrderBySelectCountDesc {

        @Test
        @DisplayName("ILIKE %keyword% 검색(대소문자 무시)가 제대로 된 List가 반환된다.")
        void success_keyword_containing_ignore_case() {
            // given
            final RouteTag defaultRouteTag = routeTagBuilder.defaultRouteTag().build();
            final String keyword = defaultRouteTag.getName();

            final RouteTag firstKeywordRouteTag = routeTagBuilder.defaultRouteTag().name("first " + keyword).build();
            final RouteTag upperCaseKeywordRouteTag = routeTagBuilder.defaultRouteTag().name("first " + keyword.toUpperCase()).build();
            final RouteTag lastKeywordRouteTag = routeTagBuilder.defaultRouteTag().name(keyword + " last").build();

            // when
            final List<RouteTag> result = routeTagRepository.findByKeywordContainingIgnoreCaseAndOrderBySelectCountDesc(keyword);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.contains(defaultRouteTag)).isTrue();
                softly.assertThat(result.contains(firstKeywordRouteTag)).isTrue();
                softly.assertThat(result.contains(upperCaseKeywordRouteTag)).isTrue();
                softly.assertThat(result.contains(lastKeywordRouteTag)).isTrue();
            });
        }

        @Test
        @DisplayName("선택 횟수로 내림차순 정렬된 List가 반환된다.")
        void success_order_by_select_count_desc() {
            // given
            final RouteTag defaultAndFirstRouteTag = routeTagBuilder.defaultRouteTag().build();
            final String keyword = defaultAndFirstRouteTag.getName();
            final Long selectCount = defaultAndFirstRouteTag.getSelectCount();

            final RouteTag thirdRouteTag = routeTagBuilder.name(keyword + " diff1").selectCount(selectCount - 2L).build();
            final RouteTag secondRouteTag = routeTagBuilder.name(keyword + " diff2").selectCount(selectCount - 1L).build();

            // when
            final List<RouteTag> result = routeTagRepository.findByKeywordContainingIgnoreCaseAndOrderBySelectCountDesc(keyword);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.get(0)).isEqualTo(defaultAndFirstRouteTag);
                softly.assertThat(result.get(1)).isEqualTo(secondRouteTag);
                softly.assertThat(result.get(2)).isEqualTo(thirdRouteTag);
            });
        }

        @Test
        @DisplayName("선택 횟수가 같으면, 'keyword' -> 'keyword%' -> '%keyword' 순으로 List가 반환된다.")
        void success_same_select_count_order_by_wild_card() {
            // given
            final RouteTag defaultAndFirstRouteTag = routeTagBuilder.defaultRouteTag().build();
            final String keyword = defaultAndFirstRouteTag.getName();

            final RouteTag thirdKeywordRouteTag = routeTagBuilder.defaultRouteTag().name("forward " + keyword).build();
            final RouteTag secondKeywordRouteTag = routeTagBuilder.defaultRouteTag().name(keyword + " behind").build();

            // when
            final List<RouteTag> result = routeTagRepository.findByKeywordContainingIgnoreCaseAndOrderBySelectCountDesc(keyword);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.get(0)).isEqualTo(defaultAndFirstRouteTag);
                softly.assertThat(result.get(1)).isEqualTo(secondKeywordRouteTag);
                softly.assertThat(result.get(2)).isEqualTo(thirdKeywordRouteTag);
            });
        }
    }
}
