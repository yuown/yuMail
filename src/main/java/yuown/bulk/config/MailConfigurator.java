package yuown.bulk.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import yuown.bulk.pool.SendMailTask;

@Configuration
@EnableAsync
public class MailConfigurator implements AsyncConfigurer {

	@PostConstruct
	public void init() {
	}

	@Bean
	public JavaMailSenderImpl javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		return javaMailSender;
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(20);
		pool.setMaxPoolSize(25);
		pool.setQueueCapacity(20);
		pool.initialize();
		return pool;
	}

	@Bean
	public SendMailTask asyncMailer() {
		return new SendMailTask();
	}

	@Override
	public Executor getAsyncExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {
			@Override
			public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
				arg0.printStackTrace();
			}
		};
	}
}
