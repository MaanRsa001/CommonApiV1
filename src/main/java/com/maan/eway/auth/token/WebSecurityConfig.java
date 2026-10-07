package com.maan.eway.auth.token;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableMethodSecurity
@Order(1000)
public class WebSecurityConfig  {

	
	@Autowired
    private UserDetailsService userDetailsService;
    
	@Autowired
	public BCryptPasswordEncoder bCryptPasswordEncoder;
//    @Bean
//	public BCryptPasswordEncoder bCryptPasswordEncoder() {
//    	return new BCryptPasswordEncoder();
//    };

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
    
    @Value("${ussd.auth.username}")
	private String ussdAuthUserName;
	
	@Value("${ussd.auth.password}")
	private String ussdAuthPwd;
	
//	@Value("${yara.auth.username}")
//	private String yaraAuthUserName;
//	
//	@Value("${yara.auth.password}")
//	private String yaraAuthPwd;

     @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }
    
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
              .passwordEncoder(bCryptPasswordEncoder);
    }

   	@Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }
   	
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/", "/resources/**", "/styles/**", "/static/**", "/jasper/**", "/public/**", "/webui/**", "/h2-console/**"
        	    , "/configuration/**", "/swagger-ui/**", "/swagger-resources/**", "/api-docs", "/api-docs/**","/fonts/**", "/v3/api-docs/**"
                , "/*.html", "/**/*.html" ,"/*.jpg","/**/*.css","/**/*.js","/**/*.png","/**/*.jpg", "/**/*.gif", "/**/*.svg", "/**/*.ico", "/**/*.ttf","/**/*.woff","/**/*.otf");
    }
    
           
    
	private static final String[] NOAUTH_MATCHER = { "/authentication/login", "/api/policydata**",
			"/authentication/tokenregenrate", "/authentication/logout", "/api/changepassword", "/api/forgotpassword",
			"/authentication/byipaddress", "/basicauth/**", "/embedded/create/**", "/post/notification/ack/mail",
			"/authentication/doauth", "/selcom/v1/checkout/webhook", "/post/notification/pushnotification",
			"/claim/get/**", "/claim/get/policydetails", "/claim//product/{companyId}", "/claim/get/coverdetails",
			"/claim/viewQuoteDetails", "crm/authentication/validateToken", "/selcom/v1/checkout/cybersourceWebhook",
			"/pdf/view/Certificate/**", "api/yara/document/**", "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**",
			"/swagger-ui.html","/realpay/webhook/callback","/selcom/v1/checkout/lipilawebhook","/otp/sendNotification",
			"/admin/createregister","/master/noauth/regionstate","/master/noauth/region","/otp/sendSmsNotification","/otp/validate",
			"/policy/verify/**","/selcom/oman/payment-return"};

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
//        Make the below setting as * to allow connection from any hos
		corsConfiguration.setAllowedOrigins(List.of("*"));
		corsConfiguration.setAllowedMethods(List.of("*"));
		corsConfiguration.setAllowedHeaders(List.of("*"));
//        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8086","https://wecoreuat.wecorephoenixgroup.com"));
//        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8086","https://wecoreuat.wecorephoenixgroup.com"));
//        corsConfiguration.setAllowedHeaders(List.of("Origin, Authorization, Accept, Content-Type, X-Requested-With, X-CSRF-Token, X-Auth-Token"));
//        corsConfiguration.setAllowedMethods(List.of("GET, POST, PUT, DELETE, OPTIONS, PATCH"));
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

/*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()            //    
                .antMatchers(NOAUTH_MATCHER).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
               
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class);
        
    }
*/
    
   
    @Bean
    @Order(2)
    public SecurityFilterChain jwtAuthFilterChain(HttpSecurity http)throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
        		.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
        		.headers(headers -> headers
        	            .contentSecurityPolicy(csp ->
        	                csp.policyDirectives("frame-ancestors 'self'")
        	            )
        	            .frameOptions(frame -> frame.sameOrigin())
        	        )
        		.authorizeHttpRequests(auth ->
        								auth.requestMatchers(NOAUTH_MATCHER)
        								.permitAll()
        								.anyRequest().authenticated()
        				)                
                .formLogin(
                			formLogin-> formLogin.loginPage("/admin/region/list")
                			.loginPage("/authentication/login").permitAll()
                		)                
                .exceptionHandling(exceptionHandling -> 
                					exceptionHandling.authenticationEntryPoint(unauthorizedHandler)
                					)                
                .sessionManagement(sessionManagement ->
                			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                			);
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class);
        
        return http.build();
        
    }
    
    @Bean
    @Order(1)
    public SecurityFilterChain basicAuthFilterChain(HttpSecurity http)throws Exception {
    	return http.securityMatcher((HttpServletRequest request) -> {
    		return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)).map(h -> {
    			return h.toLowerCase().startsWith("basic ");
    		}).orElse(false);
    	}).httpBasic(Customizer.withDefaults())
    			.userDetailsService(basicAuthUser())
    			.csrf(AbstractHttpConfigurer::disable)
    			.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
    			.authorizeHttpRequests(auth -> auth.requestMatchers("/embedded/create/schedule/**","/embedded/create/policy/schedule/**")
    					.permitAll()
    					.requestMatchers("/api/generatesequence","/api/updatebycustrefno","/basicauth/**","/embedded/create/**","post/notification/ack/mail")
    					.hasRole("USER")
    					.requestMatchers("/api/ussd").hasRole("USSD_USER")
    					.requestMatchers("/api/yara").hasRole("YARA_USER")
    					).httpBasic(b-> b.authenticationEntryPoint(basicAuthenticationPoint))
    			 .formLogin(
             			formLogin-> formLogin.loginPage("/admin/region/list")
             			.loginPage("/authentication/login").permitAll()
             		) 
    			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).build();


    }
    @Bean
	public UserDetailsService  basicAuthUser() throws Exception {
    	//AuthenticationManagerBuilder auth
		//PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		//auth.inMemoryAuthentication().withUser("ewayapi").password(bCryptPasswordEncoder.encode("ewayapi123#")).roles("USER");
    	  UserDetails user = User.builder()
                  .username("ewayapi")
                 .password( bCryptPasswordEncoder.encode("ewayapi123#"))
                //  .password("ewayapi123#")
                  .roles("USER")
                  .build();
    	  
    	  UserDetails ussdUser = User.builder()
    	            .username(ussdAuthUserName)
    	            .password(bCryptPasswordEncoder.encode(ussdAuthPwd))
    	            .roles("USSD_USER") 
    	            .build();
    	  
//    	  UserDetails yaraUser = User.builder()
//  	            .username(yaraAuthUserName)
//  	            .password(bCryptPasswordEncoder().encode(yaraAuthPwd))
//  	            .roles("YARA_USER") 
//  	            .build();
        
          return new InMemoryUserDetailsManager(user,ussdUser);
	}
    
    @Autowired
	private BasicAuthenticationPoint basicAuthenticationPoint;

}
