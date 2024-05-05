package haru.harudongseon.common;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class H2TruncateUtils {

    @Autowired
    private EntityManager em;

    @Transactional
    public void truncateAll() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        final List<String> tableNames = em.getMetamodel().getEntities().stream()
                .map(entityType -> {
                    final String name = entityType.getName();

                    return camelToSnake(name);
                })
                .toList();

        for (String tableName : tableNames) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        List<String> elementCollectionTableNames = em.getMetamodel().getManagedTypes().stream()
                .flatMap(managedType -> managedType.getPluralAttributes().stream())
                .filter(attributes -> attributes.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION)
                .map(attribute -> {
                    final String camelDeclaringType = camelToSnake(attribute.getDeclaringType().toString());
                    final String camelAttributeName = camelToSnake(attribute.getName());
                    return camelDeclaringType + "_" + camelAttributeName;
                })
                .toList();

        for (String elementCollectionTableName : elementCollectionTableNames) {
            em.createNativeQuery("TRUNCATE TABLE " + elementCollectionTableName).executeUpdate();
        }

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String camelToSnake(String str) {
        return str.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }
}
