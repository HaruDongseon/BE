package haru.harudongseon.routestorage.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteStorageRepository extends JpaRepository<RouteStorage, Long> {

    boolean existsByMemberIdAndName(final Long memberId, final String name);
}
