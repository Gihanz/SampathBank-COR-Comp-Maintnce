/*
*Copyright (c) 2019 N*Able (pvt) Ltd.
*/
package biz.nable.sb.cor.comp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
**
 */
@ComponentScan(basePackages = { "biz.nable.sb.cor", "biz.nable.sb.cor.common" })
@SpringBootApplication
@EntityScan(basePackages = { "biz.nable.sb.cor", "biz.nable.sb.cor.common.db.entity" })
@EnableJpaRepositories(basePackages = { "biz.nable.sb.cor", "biz.nable.sb.cor.common.db.repository" })
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAutoConfiguration
public class ApplicationStarter {// extends SpringBootServletInitializer {

//	public ApplicationStarter() {
//		super();
//		setRegisterErrorPageFilter(false);
//	}
//
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return configureApplication(builder);
//	}

	public static void main(String[] args) {
//		configureApplication(new SpringApplicationBuilder()).run(args);
		SpringApplication.run(ApplicationStarter.class, args);
	}

//	private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
//		return builder.sources(ApplicationStarter.class).bannerMode(Banner.Mode.OFF);
//	}

}
