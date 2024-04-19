package haru.harudongseon.searchplace.application.dto;

import java.util.List;

import haru.harudongseon.searchplace.domain.SearchedPlace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecentSearchedPlacesResponse {

    private List<RecentSearchedPlaceResponse> searchedPlaces;

    private RecentSearchedPlacesResponse(List<RecentSearchedPlaceResponse> searchedPlaces) {
        this.searchedPlaces = searchedPlaces;
    }

    public static RecentSearchedPlacesResponse from(List<SearchedPlace> searchedPlaces) {
        final List<RecentSearchedPlaceResponse> responses = searchedPlaces.stream()
                .map(RecentSearchedPlaceResponse::from)
                .toList();

        return new RecentSearchedPlacesResponse(responses);
    }
}
