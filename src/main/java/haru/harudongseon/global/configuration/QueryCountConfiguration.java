package haru.harudongseon.global.configuration;

import haru.harudongseon.global.mvc.QueryCountLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Profile({"local", "test", "dev"})
public class QueryCountConfiguration implements WebMvcConfigurer {

    private final QueryCountLoggingInterceptor queryCountLoggingInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(queryCountLoggingInterceptor);
    }
}
