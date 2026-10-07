/**
 * @author : Ashok Kumar S 
 * @since  : 07-03-2025
 */
package com.maan.eway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

/**
 * Configuration class for OpenAPI documentation in a Spring Boot application.
 * This class defines API metadata, security schemes, and global security requirements.
 */
@Configuration
public class SwaggerConfig {

	/**
     * Configures the OpenAPI documentation for the application.
     * @return OpenAPI instance with API information and security settings.
     */	
	@Bean
	public OpenAPI configSwaggerOpenApi() {		
                
        // Define API information such as title, version, and description        
        Info info = new Info()
                .title("Eway Common")
                .version("1.0")
                .description("Eway Common API Module");
	        
        // Define the security scheme for authentication (Bearer Token with JWT)              
        SecurityScheme securityScheme = new SecurityScheme()
                .type(Type.HTTP)
                .scheme("bearer")  				
                .bearerFormat("JWT");  			
                
        // Register the security scheme in OpenAPI components
        Components components = new Components()
                .addSecuritySchemes("BearerAuth", securityScheme);
        
        // Apply the security scheme globally, ensuring all endpoints require authentication
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth");  		
        
        
        // Return the configured OpenAPI instance with API metadata and security settings
        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(securityRequirement);        
        }
}
