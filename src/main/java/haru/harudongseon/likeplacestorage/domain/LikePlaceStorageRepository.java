package haru.harudongseon.likeplacestorage.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikePlaceStorageRepository extends JpaRepository<LikePlaceStorage, Long> {

    boolean existsByMemberIdAndName(final Long memberId, final String name);

    List<LikePlaceStorage> findAllByMemberId(final Long memberId);
}
