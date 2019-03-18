package com.bcb;

import com.bcb.bean.SystemProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(SystemProperties.class)
public class BcbAdminApplication {
	public static void main(String[] args){
		SpringApplication application = new SpringApplication(BcbAdminApplication.class);
		application.run(args);
	}
}
