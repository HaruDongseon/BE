package haru.harudongseon.likeplace.application.dto;

import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecentLikePlacesResponse {

    private List<LikePlaceResponse> likePlaces;

    private RecentLikePlacesResponse(final List<LikePlaceResponse> likePlaces) {
        this.likePlaces = likePlaces;
    }

    public static RecentLikePlacesResponse from(final List<LikePlace> likePlaces) {
        final List<LikePlaceResponse> likePlaceResponses = likePlaces.stream()
                .map(LikePlaceResponse::from)
                .toList();
        return new RecentLikePlacesResponse(likePlaceResponses);
    }
}
