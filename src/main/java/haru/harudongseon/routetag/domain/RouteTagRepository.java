package haru.harudongseon.routetag.domain;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteTagRepository extends JpaRepository<RouteTag, Long> {

    @Query(
            "SELECT rt FROM RouteTag rt " +
            "WHERE rt.name ILIKE concat('%', :keyword, '%') " +
            "ORDER BY rt.selectCount DESC, " +
            "CASE WHEN rt.name = :keyword THEN 0" +
            "WHEN rt.name ILIKE concat(:keyword, '%') THEN 1" +
            "WHEN rt.name ILIKE concat('%', :keyword) THEN 2" +
            "ELSE 3 END"
    )
    List<RouteTag> findByKeywordContainingIgnoreCaseAndOrderBySelectCountDesc(@Param("keyword") final String keyword);

    boolean existsByName(final String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rt FROM RouteTag rt WHERE rt.name = :name")
    Optional<RouteTag> findByName(final String name);
}
