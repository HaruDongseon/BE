package haru.harudongseon.likeplacestorage.application;

import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.LikePlaceStorageBuilder;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LikePlaceStorageServiceTest extends ServiceTest {

    @Autowired
    private LikePlaceStorageBuilder likePlaceStorageBuilder;

    @Autowired
    private LikePlaceStorageService likePlaceStorageService;

    @Nested
    @DisplayName("장소 보관함 추가 시")
    class AddLikePlaceStorage {

        @Test
        @DisplayName("추가에 성공한다.")
        void success() {
            // given
            final Long memberId = 1L;
            final LikePlaceStorageAddRequest request = new LikePlaceStorageAddRequest(기본_장소_보관함_이름);

            // when
            final Long savedLikePlaceStorageId = likePlaceStorageService.addLikePlaceStorage(memberId, request);

            // then
            Assertions.assertThat(savedLikePlaceStorageId).isNotNull();
        }

        @Test
        @DisplayName("회원의 중복된 이름인 장소 보관함이 있으면 예외가 발생한다.")
        void throws_duplicate_like_place_storage() {
            // given
            final Long memberId = 1L;
            final LikePlaceStorage existLikePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(memberId).build();

            final LikePlaceStorageAddRequest duplicateNameRequest = new LikePlaceStorageAddRequest(existLikePlaceStorage.getName());

            // when & then
            assertThatThrownBy(() -> likePlaceStorageService.addLikePlaceStorage(memberId, duplicateNameRequest))
                    .isInstanceOf(LikePlaceStorageException.DuplicateException.class)
                    .hasMessage("중복된 이름을 가진 회원의 장소 보관함이 이미 존재합니다.");
        }
    }
}
