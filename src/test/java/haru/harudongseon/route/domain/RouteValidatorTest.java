package haru.harudongseon.route.domain;

import static haru.harudongseon.common.fixtures.PlaceFixtures.기본_장소1_사진_참조1;
import static haru.harudongseon.common.fixtures.PlaceFixtures.기본_장소1_사진_참조2;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_태그1_이름;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_태그2_이름;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.RouteBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.route.exception.RouteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RouteValidatorTest {

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private RouteBuilder routeBuilder;

    @Test
    @DisplayName("동선에 중복된 태그가 존재하면 예외가 발생한다.")
    void throws_duplicate_tag() {
        // given
        final List<String> duplicateTagNames = List.of(기본_태그1_이름, 기본_태그1_이름, 기본_태그2_이름);

        // when & then
        assertThatThrownBy(() -> routeValidator.validateDuplicateTag(duplicateTagNames))
                .isInstanceOf(RouteException.DuplicateTagException.class)
                .hasMessage("동선에 중복된 태그가 존재합니다.");
    }

    @Test
    @DisplayName("동선 장소에 중복된 장소 사진이 존재하면 예외가 발생한다.")
    void throws_duplicate_route_place_photo_references() {
        // given
        final List<String> duplicatePlacePhotoReferences = List.of(기본_장소1_사진_참조1, 기본_장소1_사진_참조1, 기본_장소1_사진_참조2);

        // when & then
        assertThatThrownBy(() -> routeValidator.validateDuplicatePlacePhotoReference(duplicatePlacePhotoReferences))
                .isInstanceOf(RouteException.DuplicateRoutePlacePhotoException.class)
                .hasMessage("동선 장소에 중복된 사진이 존재합니다.");
    }

    @Nested
    @DisplayName("같은 날짜에 동선이 이미 존재하는지 체크 시")
    class ValidateSameDate {

        @Test
        @DisplayName("같은 날짜에 회원의 동선이 이미 존재한다면 예외가 발생한다.")
        void throws_duplicate_same_date() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Route route = routeBuilder.defaultRoute(member).build();

            final LocalDate date = route.getDate();

            // when & then
            assertThatThrownBy(() -> routeValidator.validateAlreadyExistSameDate(date, member.getId()))
                    .isInstanceOf(RouteException.DuplicateSameDateException.class)
                    .hasMessage("회원의 동선이 해당 날짜에 이미 존재합니다.");
        }
    }

}
