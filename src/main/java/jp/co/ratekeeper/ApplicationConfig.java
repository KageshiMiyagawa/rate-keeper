package jp.co.ratekeeper;

import java.io.IOException;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class ApplicationConfig {

	@Bean
	PropertiesFactoryBean sysconfigProperty() throws IOException {
		PropertiesFactoryBean bean = new PropertiesFactoryBean();
		bean.setLocation(new ClassPathResource("sysconfig.properties"));
		bean.afterPropertiesSet();
		return bean;
	}

}
