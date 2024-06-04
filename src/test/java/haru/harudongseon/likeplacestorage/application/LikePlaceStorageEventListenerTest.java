package haru.harudongseon.likeplacestorage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.LikePlaceStorageBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplace.application.LikePlaceService;
import haru.harudongseon.likeplace.application.event.LikePlaceDeleteEvent;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplace.domain.LikePlaceRepository;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.StoredLikePlaceRepository;
import haru.harudongseon.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 실제 이벤트 호출과 리슨 로직을 테스트하는 통합 테스트
 * 이벤트 리슨 쪽에서 호출되는 쪽을 의존해서 통합 테스트 진행
 */
class LikePlaceStorageEventListenerTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceBuilder likePlaceBuilder;

    @Autowired
    private LikePlaceRepository likePlaceRepository;

    @Autowired
    private LikePlaceStorageBuilder likePlaceStorageBuilder;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private LikePlaceService likePlaceService;

    @SpyBean
    private LikePlaceStorageEventListener likePlaceStorageEventListener;

    @MockBean
    private StoredLikePlaceRepository storedLikePlaceRepository;


    @Test
    @DisplayName("리슨에 성공해서 보관 장소 삭제(호출 로직)과 장소 보관함의 보관 장소 삭제(리슨 로직)이 성공한다.")
    void success_publish_and_listen_logic() {
        // given
        final Member member = memberBuilder.defaultMember().build();
        final LikePlace likePlace = likePlaceBuilder.defaultLikePlace(member).build();
        final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace));

        final LikePlaceDeleteEvent likePlaceDeleteEvent = new LikePlaceDeleteEvent(likePlace.getId());

        // when
        likePlaceService.deleteLikePlace(likePlace.getId());

        // then
        verify(likePlaceStorageEventListener, times(1)).deleteLikePlace(likePlaceDeleteEvent);
        assertThat(storedLikePlaceRepository.findAllByLikePlaceId(likePlace.getId())).isEmpty();
        assertThat(likePlaceRepository.findById(likePlace.getId())).isEmpty();
    }

    @Test
    @DisplayName("장소 보관함의 보관 장소 삭제(리슨 로직)에서 예외가 발생하면 보관 장소 삭제(호출 로직)에 예외가 전파된다.")
    void rollback_publish_logic() {
        // given
        final Member member = memberBuilder.defaultMember().build();
        final LikePlace likePlace = likePlaceBuilder.defaultLikePlace(member).build();
        final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace));

        given(storedLikePlaceRepository.findAllByLikePlaceId(likePlace.getId())).willThrow(new RuntimeException());

        // when & then
        assertThatThrownBy(() -> likePlaceService.deleteLikePlace(likePlace.getId()))
                .isInstanceOf(RuntimeException.class);
        assertThat(likePlaceRepository.findById(likePlace.getId())).isPresent();
    }
}
