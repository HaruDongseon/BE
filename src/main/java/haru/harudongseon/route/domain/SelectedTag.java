package haru.harudongseon.route.domain;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.routetag.domain.RouteTag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class SelectedTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private RouteTag routeTag;

    public void associate(final Route route, final RouteTag routeTag) {
        this.route = route;
        this.routeTag = routeTag;
        route.getTags().add(this);
    }

    public void unselected() {
        routeTag.unselected();
    }
}
