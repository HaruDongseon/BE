package haru.harudongseon.global.aop.querycounter;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class QueryCounter {

    private int count;

    public void increaseCount() {
        this.count++;
    }
}
