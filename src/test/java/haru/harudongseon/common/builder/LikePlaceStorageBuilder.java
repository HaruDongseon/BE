package haru.harudongseon.common.builder;

import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorageRepository;
import haru.harudongseon.likeplacestorage.domain.StoredLikePlace;
import haru.harudongseon.member.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikePlaceStorageBuilder {

    @Autowired
    private LikePlaceStorageRepository likePlaceStorageRepository;

    private String name;
    private Member member;
    private List<StoredLikePlace> likePlaces = new ArrayList<>();

    public LikePlaceStorageBuilder defaultLikePlaceStorage(final Member member) {
        this.name = 기본_장소_보관함_이름;
        this.member = member;

        return this;
    }

    public LikePlaceStorageBuilder name(final String name) {
        this.name = name;

        return this;
    }

    public LikePlaceStorageBuilder member(final Member member) {
        this.member = member;

        return this;
    }

    public LikePlaceStorageBuilder likePlaces(final List<StoredLikePlace> likePlaces) {
        this.likePlaces = likePlaces;

        return this;
    }

    public LikePlaceStorage build() {
        final LikePlaceStorage likePlaceStorage = new LikePlaceStorage(name, member, likePlaces);
        return likePlaceStorageRepository.save(likePlaceStorage);
    }
}
