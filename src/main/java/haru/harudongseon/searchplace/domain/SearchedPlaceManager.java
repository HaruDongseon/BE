package haru.harudongseon.searchplace.domain;

import java.util.List;
import java.util.Optional;

import haru.harudongseon.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchedPlaceManager {

    private static final int MAX_SEARCHED_PLACE_COUNT = 5;

    private final SearchedPlaceRepository searchedPlaceRepository;

    public void save(final Member member, final String searchKeyword) {
        final SearchedPlace searchedPlace = new SearchedPlace(member, searchKeyword);
        final Long memberId = member.getId();
        checkSearchedPlaceMax(memberId);

        final Optional<SearchedPlace> optionalSearchedPlace =
                searchedPlaceRepository.findByMemberIdAndKeyword(memberId, searchKeyword);
        optionalSearchedPlace.ifPresent(searchedPlaceRepository::delete);
        searchedPlaceRepository.save(searchedPlace);
    }

    private void checkSearchedPlaceMax(final Long memberId) {
        final List<SearchedPlace> searchedPlaces = searchedPlaceRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
        int searchedPlaceCount = searchedPlaces.size();
        if (searchedPlaceCount == MAX_SEARCHED_PLACE_COUNT) {
            final SearchedPlace oldestSearchedPlace = searchedPlaces.get(searchedPlaceCount - 1);
            searchedPlaceRepository.delete(oldestSearchedPlace);
        }
    }

    public List<SearchedPlace> getRecentSearchedPlace(final Long memberId) {
        return searchedPlaceRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
    }
}
