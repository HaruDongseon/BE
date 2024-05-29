package haru.harudongseon.likeplacestorage.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikePlaceStorage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long memberId;

    @OneToMany
    private List<LikePlace> likePlaces = new ArrayList<>();

    public LikePlaceStorage(final String name, final Long memberId) {
        this.name = name;
        this.memberId = memberId;
    }

    public LikePlaceStorage(final String name, final Long memberId,
                            final List<LikePlace> likePlaces) {
        this.name = name;
        this.memberId = memberId;
        this.likePlaces = likePlaces;
    }

    public void removeLikePlace(final Long likePlaceId) {
        final Optional<LikePlace> optionalLikePlace = likePlaces.stream()
                .filter(likePlace -> likePlace.getId().equals(likePlaceId))
                .findFirst();

        if (optionalLikePlace.isEmpty()) {
            throw new LikePlaceStorageException.NotExistLikePlaceException();
        }

        final LikePlace likePlace = optionalLikePlace.get();
        likePlaces.remove(likePlace);
    }
}
