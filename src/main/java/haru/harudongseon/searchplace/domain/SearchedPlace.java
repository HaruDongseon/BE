package haru.harudongseon.searchplace.domain;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SearchedPlace extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    private String keyword;


    public SearchedPlace(final Member member, final String keyword) {
        this.member = member;
        this.keyword = keyword;
    }
}
