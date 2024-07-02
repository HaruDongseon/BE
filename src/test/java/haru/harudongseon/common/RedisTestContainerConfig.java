package haru.harudongseon.common;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;

@Configuration
public class RedisTestContainerConfig {

    private static final String REDIS_IMAGE = "redis:7.2.5-alpine";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);
        REDIS_CONTAINER.start();

        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
    }
}
