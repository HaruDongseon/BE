package haru.harudongseon.searchplace.application;

import java.util.Optional;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import haru.harudongseon.searchplace.application.dto.SearchedPlaceAddRequest;
import haru.harudongseon.searchplace.domain.SearchedPlace;
import haru.harudongseon.searchplace.domain.SearchedPlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchedPlaceService {

    private final MemberRepository memberRepository;
    private final SearchedPlaceRepository searchedPlaceRepository;

    public void addSearchedPlace(final Long memberId, final SearchedPlaceAddRequest request) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));
        final String searchKeyword = request.keyword();
        final SearchedPlace searchedPlace = new SearchedPlace(member, searchKeyword);

        final Optional<SearchedPlace> optionalSearchedPlace =
                searchedPlaceRepository.findByMemberIdAndKeyword(member.getId(), searchKeyword);
        optionalSearchedPlace.ifPresent(searchedPlaceRepository::delete);
        searchedPlaceRepository.save(searchedPlace);
    }
}
