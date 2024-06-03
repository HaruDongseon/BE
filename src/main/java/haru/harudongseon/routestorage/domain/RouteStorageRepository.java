package haru.harudongseon.routestorage.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteStorageRepository extends JpaRepository<RouteStorage, Long> {

    boolean existsByMemberIdAndName(final Long memberId, final String name);

    List<RouteStorage> findAllByMemberId(final Long memberId);

    Optional<RouteStorage> findByIdAndMemberId(final Long id, final Long memberId);
}
