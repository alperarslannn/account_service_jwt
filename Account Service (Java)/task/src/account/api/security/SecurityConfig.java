package account.api.security;

import account.domain.UserAccount;
import account.domain.repositories.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UserAccountRepository userAccountRepository;
    private final CustomBCryptPasswordEncoder encoder;


    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler, UserAccountRepository userAccountRepository, CustomBCryptPasswordEncoder encoder) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.userAccountRepository = userAccountRepository;
        this.encoder = encoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<UserAccount> user = userAccountRepository.findByEmailEqualsIgnoreCase(username);
            if (user.isEmpty()) {
                throw new UsernameNotFoundException(username);
            }
            CustomUserDetails customUserDetails = new CustomUserDetails(user.get().getId(), user.get().getEmail(), user.get().getPassword(), user.get().getSalt(), user.get().isLocked(),user.get().getRoles());
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword());
            context.setAuthentication(authentication);

            return customUserDetails;
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService())
                .passwordEncoder(encoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .csrf(AbstractHttpConfigurer::disable) // For Postman
                .headers(headers -> headers.frameOptions().disable()) // For the H2 console
                .authorizeHttpRequests(auth -> auth  // manage access
                                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyRole(Role.ADMINISTRATOR.name(), Role.USER.name(), Role.ACCOUNTANT.name())
                                .requestMatchers(HttpMethod.GET, "/api/empl/payment/**").hasAnyRole(Role.USER.name(), Role.ACCOUNTANT.name())
                                .requestMatchers(HttpMethod.GET, "/api/admin/user/**").hasRole(Role.ADMINISTRATOR.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasRole(Role.ADMINISTRATOR.name())
                                .requestMatchers(HttpMethod.PUT, "/api/admin/user/role/**").hasRole(Role.ADMINISTRATOR.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/user/access/**").hasRole(Role.ADMINISTRATOR.name())
                                .requestMatchers(HttpMethod.PUT, "/api/admin/user/access/**").hasRole(Role.ADMINISTRATOR.name())
                                .requestMatchers(HttpMethod.POST, "/api/acct/payments/**").hasRole(Role.ACCOUNTANT.name())
                                .requestMatchers(HttpMethod.PUT, "/api/acct/payments/**").hasRole(Role.ACCOUNTANT.name())
                                .requestMatchers(HttpMethod.GET, "/api/security/events/**").hasAnyRole(Role.AUDITOR.name())
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/admin/user/**")).hasRole(Role.ADMINISTRATOR.name())
                                .anyRequest().authenticated()
                        // other matchers
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .authenticationManager(authenticationManager);

        return http.build();
    }

}
