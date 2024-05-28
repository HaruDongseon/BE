package haru.harudongseon.likeplace.application.dto;

import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikePlacesResponse {

    private List<LikePlaceResponse> likePlaces;

    private LikePlacesResponse(final List<LikePlaceResponse> likePlaces) {
        this.likePlaces = likePlaces;
    }

    public static LikePlacesResponse from(final List<LikePlace> likePlaces) {
        final List<LikePlaceResponse> responses = likePlaces.stream()
                .map(LikePlaceResponse::from)
                .toList();

        return new LikePlacesResponse(responses);
    }
}
