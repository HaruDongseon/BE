package haru.harudongseon.likeplacestorage.domain;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
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

    @OneToMany(mappedBy = "likePlaceStorage")
    private List<LikePlace> likePlaces = new ArrayList<>();

    public LikePlaceStorage(final String name, final List<LikePlace> likePlaces) {
        this.name = name;
        this.likePlaces = likePlaces;
    }
}
