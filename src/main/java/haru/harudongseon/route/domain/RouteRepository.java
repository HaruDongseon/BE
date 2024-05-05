package haru.harudongseon.route.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByIdAndMemberId(final Long id, final Long memberId);
}
