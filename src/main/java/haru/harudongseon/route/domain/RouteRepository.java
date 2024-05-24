package haru.harudongseon.route.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByIdAndMemberId(final Long id, final Long memberId);
    boolean existsByIdAndMemberId(final Long id, final Long memberId);

    @Query(
            "select r from Route r " +
            "where r.member.id = :memberId and r.date >= :startDate and r.date <= :endDate " +
            "order by r.date asc"
    )
    List<Route> findByMemberIdAndPeriod(final Long memberId, final LocalDate startDate, final LocalDate endDate);
}
