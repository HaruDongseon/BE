package haru.harudongseon.searchplace.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchedPlaceRepository extends JpaRepository<SearchedPlace, Long> {

    Optional<SearchedPlace> findByMemberIdAndKeyword(final Long memberId, final String keyword);
}
