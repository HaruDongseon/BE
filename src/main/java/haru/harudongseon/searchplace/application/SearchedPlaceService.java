package haru.harudongseon.searchplace.application;

import java.util.List;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import haru.harudongseon.searchplace.application.dto.RecentSearchedPlacesResponse;
import haru.harudongseon.searchplace.application.dto.SearchedPlaceAddRequest;
import haru.harudongseon.searchplace.domain.SearchedPlace;
import haru.harudongseon.searchplace.domain.SearchedPlaceManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchedPlaceService {

    private final MemberRepository memberRepository;
    private final SearchedPlaceManager searchedPlaceManager;

    public void addSearchedPlace(final Long memberId, final SearchedPlaceAddRequest request) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));
        final String searchKeyword = request.keyword();
        searchedPlaceManager.save(member, searchKeyword);
    }

    public RecentSearchedPlacesResponse findRecentSearchedPlace(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));
        final List<SearchedPlace> recentSearchedPlace = searchedPlaceManager.getRecentSearchedPlace(memberId);
        return RecentSearchedPlacesResponse.from(recentSearchedPlace);
    }

    public void delete(final Long searchedPlaceId, final Long memberId) {
        searchedPlaceManager.deleteById(searchedPlaceId, memberId);
    }

    public void deleteAll() {
        searchedPlaceManager.deleteAll();
    }
}
