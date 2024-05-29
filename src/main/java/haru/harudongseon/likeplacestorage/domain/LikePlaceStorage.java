package haru.harudongseon.likeplacestorage.domain;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.member.domain.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @OneToMany(mappedBy = "likePlaceStorage")
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

    public StoredLikePlace addLikePlace(final LikePlace likePlace) {
        final StoredLikePlace storedLikePlace = new StoredLikePlace();
        storedLikePlace.associate(likePlace, this);
        return storedLikePlace;
    }
}
