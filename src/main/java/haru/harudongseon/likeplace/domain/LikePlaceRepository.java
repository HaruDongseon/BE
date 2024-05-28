package haru.harudongseon.likeplace.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikePlaceRepository extends JpaRepository<LikePlace, Long> {

    @Query(
            "select lp from LikePlace lp " +
            "where lp.member.id = :memberId " +
            "order by lp.createdAt desc " +
            "limit 3"
    )
    List<LikePlace> findRecentThreeByMemberId(final Long memberId);

    @Query(
            "select lp from LikePlace lp " +
                    "where lp.member.id = :memberId " +
                    "order by lp.createdAt desc"
    )
    List<LikePlace> findAllByMemberId(final Long memberId);

    @Query(
            "SELECT lp FROM LikePlace lp " +
                    "WHERE lp.name ILIKE concat('%', :keyword, '%') and lp.member.id = :memberId " +
                    "ORDER BY " +
                    "CASE WHEN lp.name = :keyword THEN 0" +
                    "WHEN lp.name ILIKE concat(:keyword, '%') THEN 1" +
                    "WHEN lp.name ILIKE concat('%', :keyword) THEN 2" +
                    "ELSE 3 END"
    )
    List<LikePlace> searchByKeywordAndMemberId(@Param("keyword") final String keyword, final Long memberId);
}
