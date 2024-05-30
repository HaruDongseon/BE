package haru.harudongseon.routestorage.domain;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RouteStorage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    private String name;

    @OneToMany(mappedBy = "routeStorage", cascade = CascadeType.PERSIST)
    private List<StoredRoute> routes = new ArrayList<>();

    public RouteStorage(final Member member, final String name) {
        this.member = member;
        this.name = name;
    }
}
