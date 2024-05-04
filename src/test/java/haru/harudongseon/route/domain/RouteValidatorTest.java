package haru.harudongseon.route.domain;

import static haru.harudongseon.common.fixtures.PlaceFixtures.기본_장소1_사진_참조1;
import static haru.harudongseon.common.fixtures.PlaceFixtures.기본_장소1_사진_참조2;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_태그1_이름;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_태그2_이름;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.PlaceBuilder;
import haru.harudongseon.common.builder.RouteBuilder;
import haru.harudongseon.common.builder.RouteTagBuilder;
import haru.harudongseon.route.exception.RouteException;
import org.junit.jupiter.api.DisplayName;
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
    private RouteTagBuilder routeTagBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

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
}
