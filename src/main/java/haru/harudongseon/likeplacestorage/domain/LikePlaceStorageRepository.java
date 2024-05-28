package haru.harudongseon.likeplacestorage.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikePlaceStorageRepository extends JpaRepository<LikePlaceStorage, Long> {

    boolean existsByMemberIdAndName(final Long memberId, final String name);
}
