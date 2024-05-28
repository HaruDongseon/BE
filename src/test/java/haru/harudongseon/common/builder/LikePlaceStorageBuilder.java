package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikePlaceStorageBuilder {

    @Autowired
    private LikePlaceStorageRepository likePlaceStorageRepository;

    private String name;
    private Long memberId;
    private List<LikePlace> likePlaces = new ArrayList<>();

    public LikePlaceStorageBuilder defaultLikePlaceStorage(final Long memberId) {
        this.name = 기본_장소_보관함_이름;
        this.memberId = memberId;

        return this;
    }

    public LikePlaceStorageBuilder name(final String name) {
        this.name = name;

        return this;
    }

    public LikePlaceStorageBuilder memberId(final Long memberId) {
        this.memberId = memberId;

        return this;
    }

    public LikePlaceStorageBuilder likePlaces(final List<LikePlace> likePlaces) {
        this.likePlaces = likePlaces;

        return this;
    }

    public LikePlaceStorage build() {
        final LikePlaceStorage likePlaceStorage = new LikePlaceStorage(name, memberId, likePlaces);
        return likePlaceStorageRepository.save(likePlaceStorage);
    }
}
