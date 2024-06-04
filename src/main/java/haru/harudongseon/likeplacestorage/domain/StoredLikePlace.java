package haru.harudongseon.likeplacestorage.domain;

import java.util.List;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.likeplace.domain.LikePlace;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class StoredLikePlace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private LikePlace likePlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private LikePlaceStorage likePlaceStorage;

    public void associate(final LikePlace likePlace, final LikePlaceStorage likePlaceStorage) {
        this.likePlace = likePlace;
        this.likePlaceStorage = likePlaceStorage;
        likePlaceStorage.getLikePlaces().add(this);
    }

    public void unstored() {
        final List<StoredLikePlace> likePlaces = likePlaceStorage.getLikePlaces();
        likePlaces.remove(this);
    }
}
