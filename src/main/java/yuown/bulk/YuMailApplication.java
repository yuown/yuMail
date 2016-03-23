package yuown.bulk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import yuown.bulk.config.MailConfigurator;

@SpringBootApplication
@Import(MailConfigurator.class)
public class YuMailApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuMailApplication.class, args);
    }
}
