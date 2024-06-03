package haru.harudongseon.likeplacestorage.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.Executor;

import haru.harudongseon.common.H2TruncateUtils;
import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.LikePlaceStorageBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplace.application.event.LikePlaceDeleteEvent;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.StoredLikePlaceRepository;
import haru.harudongseon.member.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.transaction.TestTransaction;

class LikePlaceStorageEventListenerTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceBuilder likePlaceBuilder;

    @Autowired
    private LikePlaceStorageBuilder likePlaceStorageBuilder;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @SpyBean
    private LikePlaceStorageEventListener likePlaceStorageEventListener;

    @MockBean
    private StoredLikePlaceRepository storedLikePlaceRepository;

    @Autowired
    private H2TruncateUtils h2TruncateUtils;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public Executor executor() {
            return new SyncTaskExecutor();
        }
    }

    @AfterEach
    void tearDown() {
        h2TruncateUtils.truncateAll();
    }

    @Test
    @DisplayName("보관 장소 삭제 이벤트를 받아서 리슨 로직이 실행된다.")
    void success_listen() throws InterruptedException {
        // given
        final Member member = memberBuilder.defaultMember().build();
        final LikePlace likePlace = likePlaceBuilder.defaultLikePlace(member).build();
        final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace));

        final LikePlaceDeleteEvent likePlaceDeleteEvent = new LikePlaceDeleteEvent(likePlace.getId());

        // when
        applicationEventPublisher.publishEvent(likePlaceDeleteEvent);

        TestTransaction.flagForCommit();
        TestTransaction.end();

        // then
        verify(likePlaceStorageEventListener, times(1)).deleteLikePlace(likePlaceDeleteEvent);
    }

    @Test
    @DisplayName("보관 장소 삭제 이벤트를 받아서 리슨 로직이 실행된다2222.")
    void success_liste2n() throws InterruptedException {
        // given
        final Member member = memberBuilder.defaultMember().build();
        final LikePlace likePlace = likePlaceBuilder.defaultLikePlace(member).build();
        final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace));

        final LikePlaceDeleteEvent likePlaceDeleteEvent = new LikePlaceDeleteEvent(likePlace.getId());

        // when
        applicationEventPublisher.publishEvent(likePlaceDeleteEvent);

        TestTransaction.flagForCommit();
        TestTransaction.end();

        // then
        verify(storedLikePlaceRepository, times(1)).findAllByLikePlaceId(any());
    }
}
