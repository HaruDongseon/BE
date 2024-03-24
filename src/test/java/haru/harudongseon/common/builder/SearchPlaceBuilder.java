package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.SearchPlaceFixtures.기본_검색_장소_키워드;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.searchplace.domain.SearchedPlace;
import haru.harudongseon.searchplace.domain.SearchedPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchPlaceBuilder {

    @Autowired
    private SearchedPlaceRepository searchedPlaceRepository;

    private Member member;
    private String keyword;

    public SearchPlaceBuilder defaultSearchPlace(final Member member) {
        this.member = member;
        this.keyword = 기본_검색_장소_키워드;

        return this;
    }

    public SearchPlaceBuilder member(final Member member) {
        this.member = member;
        return this;
    }

    public SearchPlaceBuilder keyword(final String keyword) {
        this.keyword = keyword;
        return this;
    }

    public SearchedPlace build() {
        final SearchedPlace searchedPlace = new SearchedPlace(this.member, this.keyword);
        return searchedPlaceRepository.save(searchedPlace);
    }
}
