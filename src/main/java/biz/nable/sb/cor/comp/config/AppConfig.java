package biz.nable.sb.cor.comp.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import biz.nable.sb.cor.common.filter.RequestResponseLoggingInterceptor;
import biz.nable.sb.cor.comp.bean.RetrieveFinacleBean;
import biz.nable.sb.cor.comp.component.SOAPConnector;

@Configuration
public class AppConfig {

	@Value("${custom.iib.finacle.integration.url}")
	private String iibBaseUrl;

	@Value("${finacle.data.object.app-code}")
	private String appCode;
	@Value("${finacle.data.object.cdci-code}")
	private String cdciCode;
	@Value("${finacle.data.object.controller}")
	private String controller;
	@Value("${finacle.data.object.currency}")
	private String currency;
	@Value("${finacle.data.object.sol-id}")
	private String solId;

	@Value("${async.executor.core-pool-size}")
	private Integer corePoolSize;
	@Value("${async.executor.max-pool-size}")
	private Integer maxPoolSize;
	@Value("${async.executor.queue-capacity}")
	private Integer queueCapacity;

	@Bean
	public RestTemplate restTemplate() {

		RestTemplate restTemplate = new RestTemplate(
				new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new RequestResponseLoggingInterceptor());
		restTemplate.setInterceptors(interceptors);

		return restTemplate;
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:messages");
		return messageSource;
	}

	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(true);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// this is the package name specified in the <generatePackage> specified in
		// pom.xml
		marshaller.setContextPath("biz.nable.sb.cor.comp.soap.schemas.iib");
		return marshaller;
	}

	@Bean
	public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
		SOAPConnector client = new SOAPConnector();
		client.setDefaultUri(iibBaseUrl);
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

	@Bean
	public RetrieveFinacleBean fillFinacleDataObject() {
		RetrieveFinacleBean finacleDTO = new RetrieveFinacleBean();

		finacleDTO.setAPPCode(appCode);
		finacleDTO.setCDCICode(cdciCode);
		finacleDTO.setController(controller);
		finacleDTO.setCurrencyCode(currency);
		finacleDTO.setSolID(solId);
		return finacleDTO;
	}

	@Bean(name = "asyncAccountSyncExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix("AccountSyncThread-");
		executor.initialize();
		return executor;
	}
}
