package haru.harudongseon.routetag.domain;

import haru.harudongseon.global.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RouteTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long selectCount;

    public RouteTag(final String name) {
        this.name = name;
        this.selectCount = 1L;
    }

    public RouteTag(final String name, final Long selectCount) {
        this.name = name;
        this.selectCount = selectCount;
    }
}
