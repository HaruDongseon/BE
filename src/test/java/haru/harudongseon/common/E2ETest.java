package haru.harudongseon.common;

import haru.harudongseon.global.jwt.JwtService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class E2ETest {

    protected static final String JWT_PREFIX = "Bearer ";

    @Autowired
    private H2TruncateUtils h2TruncateUtils;

    @Autowired
    protected JwtService jwtService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
        h2TruncateUtils.truncateAll();
    }

}
