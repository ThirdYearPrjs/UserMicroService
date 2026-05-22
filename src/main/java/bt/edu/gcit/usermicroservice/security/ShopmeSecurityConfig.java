package bt.edu.gcit.usermicroservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Arrays;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import bt.edu.gcit.usermicroservice.security.oauth.CustomerOAuth2UserService;
import bt.edu.gcit.usermicroservice.security.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class ShopmeSecurityConfig {
    public ShopmeSecurityConfig() {
        System.out.println("ShopmeSecurityConfig created");
    }

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomerOAuth2UserService oAuth2UserService;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        System.out.println("H2i ");
        return new ProviderManager(Arrays.asList(authenticationProvider())); // Corrected name
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // The service to find users
        authProvider.setPasswordEncoder(passwordEncoder()); // The bean that encodes passwords
        return authProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.GET, "/api/users/checkDuplicateEmail").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/enabled").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/countries/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/countries").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/countries").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/countries").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/states/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/states/{country_id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/states").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/states").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customer/**").permitAll())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler));

        return http.build();
    }
}