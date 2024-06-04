package haru.harudongseon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HaruDongseonApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaruDongseonApplication.class, args);
    }

}
