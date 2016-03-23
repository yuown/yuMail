package yuown.bulk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import yuown.bulk.service.ConfigurationService;

import java.util.Properties;

import javax.annotation.PostConstruct;

@Configuration
public class MailConfigurator {

    @Autowired
    private ConfigurationService configurationService;

    private String host;

    private Integer port;

    @PostConstruct
    public void init() {
        host = configurationService.getByName("mail.smtp.host").getStrValue();
        port = configurationService.getByName("mail.smtp.port").getValue();
    }

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(configurationService.getByName("mail.user.name").getStrValue());
        javaMailSender.setPassword(configurationService.getByName("mail.user.pass").getStrValue());

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", configurationService.getByName("mail.auth.required").getBoolValue().toString());
        properties.setProperty("mail.smtp.starttls.enable", configurationService.getByName("mail.smtp.starttls.enable").getBoolValue().toString());
        properties.setProperty("mail.smtp.user", configurationService.getByName("mail.user.name").getStrValue());
        properties.setProperty("mail.smtp.password", configurationService.getByName("mail.user.pass").getStrValue());
        properties.setProperty("mail.debug", "true");
        return properties;
    }

}
