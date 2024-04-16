package haru.harudongseon.route.domain;

import haru.harudongseon.routetag.domain.RouteTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SelectedTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private RouteTag routeTag;

    public SelectedTag(final Route route, final RouteTag routeTag) {
        this.route = route;
        this.routeTag = routeTag;
    }
}
