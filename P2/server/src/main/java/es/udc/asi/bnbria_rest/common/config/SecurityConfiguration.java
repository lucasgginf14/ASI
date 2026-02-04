package es.udc.asi.bnbria_rest.common.config;

import es.udc.asi.bnbria_rest.common.security.JWTConfigurer;
import es.udc.asi.bnbria_rest.common.security.MyAccessDeniedHandler;
import es.udc.asi.bnbria_rest.common.security.MyUnauthorizedEntryPoint;
import es.udc.asi.bnbria_rest.common.security.MyUserDetailsService;
import es.udc.asi.bnbria_rest.common.security.TokenProvider;
import es.udc.asi.bnbria_rest.user.persistence.entity.UserAuthority;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

  @Autowired
  private Properties properties;

  @Autowired
  private MyUnauthorizedEntryPoint myUnauthorizedEntryPoint;

  @Autowired
  private MyAccessDeniedHandler myAccessDeniedHandler;

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private MyUserDetailsService myUserDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .csrf((csrf) -> csrf.disable())
      .exceptionHandling((exceptionHandling) -> exceptionHandling
        .authenticationEntryPoint(myUnauthorizedEntryPoint)
        .accessDeniedHandler(myAccessDeniedHandler)
      )
      .headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
      .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests((authorize) -> authorize
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/account/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/app-stats").hasAnyAuthority(UserAuthority.ADMIN.toString())
        .requestMatchers( "/api/users/admin/**").hasAnyAuthority(UserAuthority.ADMIN.toString())
        .requestMatchers( "/api/users/**").authenticated()
        .requestMatchers( "/api/user-reviews/admin/**").hasAnyAuthority(UserAuthority.ADMIN.toString())
        .requestMatchers( "/api/user-reviews/**").authenticated()
        .requestMatchers( "/api/properties/admin/**").hasAnyAuthority(UserAuthority.ADMIN.toString())
        .requestMatchers( "/api/properties/my-properties/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/properties/**").permitAll()
        .requestMatchers( "/api/property-reviews/admin/**").hasAnyAuthority(UserAuthority.ADMIN.toString())
        .requestMatchers( "/api/property-reviews/**").authenticated()
        .requestMatchers("/api/messages/**").authenticated()
        .requestMatchers("/api/bookings/admin/**").hasAnyAuthority(UserAuthority.ADMIN.toString())
        .requestMatchers("/api/bookings/**").authenticated()
        .requestMatchers("/**").authenticated())
      .with(securityConfigurerAdapter(), Customizer.withDefaults());
    // @formatter:on
    return http.build();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins(properties.getClientHost());
      }
    };
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
    throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Autowired
  public void configureAuth(AuthenticationManagerBuilder auth) {
    try {
      auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder);
    } catch (Exception e) {
      throw new BeanInitializationException("SecurityConfiguration.configureAuth failed", e);
    }
  }

  private JWTConfigurer securityConfigurerAdapter() {
    return new JWTConfigurer(tokenProvider);
  }
}
