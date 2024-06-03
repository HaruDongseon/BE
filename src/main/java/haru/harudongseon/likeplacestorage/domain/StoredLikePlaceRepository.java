package haru.harudongseon.likeplacestorage.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredLikePlaceRepository extends JpaRepository<StoredLikePlace, Long> {

    List<StoredLikePlace> findAllByLikePlaceId(final Long likePlaceId);
}
