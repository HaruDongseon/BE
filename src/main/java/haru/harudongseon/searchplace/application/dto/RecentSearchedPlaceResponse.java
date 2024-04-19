package haru.harudongseon.searchplace.application.dto;

import haru.harudongseon.searchplace.domain.SearchedPlace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecentSearchedPlaceResponse {

    @Schema(description = "검색 장소 ID", example = "1")
    private Long id;

    @Schema(description = "검색 장소 키워드", example = "스타벅스")
    private String keyword;

    private RecentSearchedPlaceResponse(final Long id, final String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public static RecentSearchedPlaceResponse from(final SearchedPlace searchedPlace) {
        final Long id = searchedPlace.getId();
        final String keyword = searchedPlace.getKeyword();
        return new RecentSearchedPlaceResponse(id, keyword);
    }
}
