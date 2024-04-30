package haru.harudongseon.likeplace.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LikePlaceRepository extends JpaRepository<LikePlace, Long> {

    @Query(
            "select lp from LikePlace lp " +
            "where lp.member.id = :memberId " +
            "order by lp.createdAt desc " +
            "limit 3"
    )
    List<LikePlace> findRecentThreeByMemberId(final Long memberId);
}
