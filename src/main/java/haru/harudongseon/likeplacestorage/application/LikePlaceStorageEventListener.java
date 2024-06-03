package haru.harudongseon.likeplacestorage.application;


import java.util.List;

import haru.harudongseon.likeplace.application.event.LikePlaceDeleteEvent;
import haru.harudongseon.likeplacestorage.domain.StoredLikePlace;
import haru.harudongseon.likeplacestorage.domain.StoredLikePlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Transactional
@RequiredArgsConstructor
public class LikePlaceStorageEventListener {

    private final StoredLikePlaceRepository storedLikePlaceRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public void deleteLikePlace(final LikePlaceDeleteEvent likePlaceDeleteEvent) {
        final Long likePlaceId = likePlaceDeleteEvent.likePlaceId();
        final List<StoredLikePlace> storedLikePlaces = storedLikePlaceRepository.findAllByLikePlaceId(likePlaceId);
        storedLikePlaces.forEach(StoredLikePlace::unstored);
    }

    @Recover
    public void recoverDeleteLikePlace(final LikePlaceDeleteEvent likePlaceDeleteEvent) {
        final Long likePlaceId = likePlaceDeleteEvent.likePlaceId();

    }
}
