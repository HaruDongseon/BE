package haru.harudongseon.likeplacestorage.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikePlaceStorage extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @OneToMany(mappedBy = "likePlaceStorage", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<StoredLikePlace> likePlaces = new ArrayList<>();

    public LikePlaceStorage(final String name, final Member member) {
        this.name = name;
        this.member = member;
    }

    public LikePlaceStorage(final String name, final Member member,
                            final List<StoredLikePlace> likePlaces) {
        this.name = name;
        this.member = member;
        this.likePlaces = likePlaces;
    }

    public void moveLikePlaces(final List<LikePlaceStorage> likePlaceStoragesToMove, final List<LikePlace> likePlacesToMove) {
        validateAvailableMove(likePlaceStoragesToMove, likePlacesToMove);

        likePlaceStoragesToMove.forEach(likePlaceStorageToMove -> likePlaceStorageToMove.addLikePlaces(likePlacesToMove));
        final List<Long> likePlaceIdsToMove = likePlacesToMove.stream()
                .map(LikePlace::getId)
                .toList();
        this.removeLikePlaces(likePlaceIdsToMove);
    }

    private void validateAvailableMove(final List<LikePlaceStorage> likePlaceStoragesToMove, final List<LikePlace> likePlacesToMove) {
        validateAddLikePlace(likePlaceStoragesToMove, likePlacesToMove);
        validateRemoveLikePlace(likePlacesToMove);
    }

    private void validateAddLikePlace(final List<LikePlaceStorage> likePlaceStoragesToMove, final List<LikePlace> likePlacesToMove) {
        for (LikePlaceStorage likePlaceStorageToMove : likePlaceStoragesToMove) {
            for (LikePlace likePlaceToMove : likePlacesToMove) {
                likePlaceStorageToMove.validateAlreadyExistLikePlace(likePlaceToMove.getId());
            }
        }
    }

    private void validateRemoveLikePlace(final List<LikePlace> likePlacesToMove) {
        final List<Long> likePlaceIds = this.likePlaces.stream()
                .map(storedLikePlace -> storedLikePlace.getLikePlace().getId())
                .toList();
        for (LikePlace likePlaceToMove : likePlacesToMove) {
            final Long likePlaceIdToMove = likePlaceToMove.getId();
            if (!likePlaceIds.contains(likePlaceIdToMove)) {
                throw new LikePlaceStorageException.NotExistLikePlaceException();
            }
        }
    }

    public void addLikePlaces(final List<LikePlace> likePlaces) {
        likePlaces.forEach(likePlace -> this.validateAlreadyExistLikePlace(likePlace.getId()));
        likePlaces.forEach(this::addLikePlace);
    }

    private void addLikePlace(final LikePlace likePlace) {
        final StoredLikePlace storedLikePlace = new StoredLikePlace();
        storedLikePlace.associate(likePlace, this);
    }

    public void removeLikePlaces(final List<Long> likePlaceIdsToRemove) {
        final List<Long> likePlaceIds = this.likePlaces.stream()
                .map(storedLikePlace -> storedLikePlace.getLikePlace().getId())
                .toList();

        for (Long likePlaceIdToRemove : likePlaceIdsToRemove) {
            if (!likePlaceIds.contains(likePlaceIdToRemove)) {
                throw new LikePlaceStorageException.NotExistLikePlaceException();
            }
        }

        likePlaceIdsToRemove.forEach(this::removeLikePlace);
    }

    private void removeLikePlace(final Long likePlaceId) {
        final StoredLikePlace likePlaceToRemove = likePlaces.stream()
                .filter(storedLikePlace -> storedLikePlace.getLikePlace().getId().equals(likePlaceId))
                .findFirst()
                .orElseThrow(LikePlaceStorageException.NotExistLikePlaceException::new);

        likePlaces.remove(likePlaceToRemove);
    }

    private void validateAlreadyExistLikePlace(final Long likePlaceId) {
        final Optional<StoredLikePlace> optionalLikePlace = likePlaces.stream()
                .filter(likePlace -> likePlace.isEqualToLikePlaceId(likePlaceId))
                .findFirst();

        if (optionalLikePlace.isPresent()) {
            throw new LikePlaceStorageException.AlreadyExistLikePlaceException();
        }
    }
}
