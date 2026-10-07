package com.maan.eway.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {
	
	private List<SecurityScheme> apiKey() {

		List<SecurityScheme> list = new ArrayList<SecurityScheme>();
		list.add(new ApiKey("JWT", "Authorization", "header"));
		list.add(new ApiKey("BasicAuth", "Authorization", "header"));

		return list;
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.maan"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.forCodeGeneration(true)
				.securityContexts(Arrays.asList(securityContext()))
				.securitySchemes(apiKey());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"My REST API", "Some custom description of API.",
				"1.0", "Terms of service",
				new Contact("Maan Sarovar", "www.maansarovar.com", "maanrsa001@gmail.com"),
				"License of API",
				"API license URL", 
				Collections.emptyList());
	}
}
