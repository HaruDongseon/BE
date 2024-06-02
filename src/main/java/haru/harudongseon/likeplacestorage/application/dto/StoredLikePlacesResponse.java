package haru.harudongseon.likeplacestorage.application.dto;

import java.util.List;

import haru.harudongseon.likeplacestorage.domain.StoredLikePlace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredLikePlacesResponse {

    private List<StoredLikePlaceResponse> storedLikePlaces;

    private StoredLikePlacesResponse(final List<StoredLikePlaceResponse> storedLikePlaces) {
        this.storedLikePlaces = storedLikePlaces;
    }

    public static StoredLikePlacesResponse from(final List<StoredLikePlace> storedLikePlaces) {
        final List<StoredLikePlaceResponse> storedLikePlaceResponses = storedLikePlaces.stream()
                .map(StoredLikePlaceResponse::from)
                .toList();

        return new StoredLikePlacesResponse(storedLikePlaceResponses);
    }
}
