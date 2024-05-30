package haru.harudongseon.routestorage.domain;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.route.domain.Route;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class StoredRoute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private RouteStorage routeStorage;
}
