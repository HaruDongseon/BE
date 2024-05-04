package haru.harudongseon.likeplace.domain;

import static haru.harudongseon.common.fixtures.LikePlaceFixtures.기본_보관_장소_사진_참조1;
import static haru.harudongseon.common.fixtures.LikePlaceFixtures.기본_보관_장소_사진_참조2;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplace.exception.LikePlaceException;
import haru.harudongseon.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LikePlaceValidatorTest {

    @Autowired
    private LikePlaceValidator likePlaceValidator;

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceBuilder likePlaceBuilder;

    @Test
    @DisplayName("보관 장소에 중복된 사진이 존재하면 예외가 발생한다.")
    void throws_like_place_duplicate_photo() {
        // given
        final Member member = memberBuilder.defaultMember().build();
        final LikePlace duplicatePhotoLikePlace = likePlaceBuilder.defaultLikePlace(member)
                .photoReferences(List.of(기본_보관_장소_사진_참조1, 기본_보관_장소_사진_참조1, 기본_보관_장소_사진_참조2))
                .buildEntity();


        // when & then
        assertThatThrownBy(() -> likePlaceValidator.validatePhotoDuplicate(duplicatePhotoLikePlace))
                .isInstanceOf(LikePlaceException.PhotoDuplicateException.class)
                .hasMessage("보관 장소에 중복되는 사진이 존재합니다.");
    }
}
