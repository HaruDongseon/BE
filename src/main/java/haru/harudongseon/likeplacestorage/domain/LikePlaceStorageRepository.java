package haru.harudongseon.likeplacestorage.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikePlaceStorageRepository extends JpaRepository<LikePlaceStorage, Long> {

    boolean existsByMemberIdAndName(final Long memberId, final String name);

    List<LikePlaceStorage> findAllByMemberId(final Long memberId);

    Optional<LikePlaceStorage> findByIdAndMemberId(final Long id, final Long memberId);
}
