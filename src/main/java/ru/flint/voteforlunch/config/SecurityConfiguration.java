package ru.flint.voteforlunch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.flint.voteforlunch.model.Role;
import ru.flint.voteforlunch.model.User;
import ru.flint.voteforlunch.repository.UserRepository;
import ru.flint.voteforlunch.web.security.AuthorizedUser;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {
    private final UserRepository userRepository;

    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            log.debug("Authenticating '{}'", email);
            Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(email);
            return new AuthorizedUser(optionalUser.orElseThrow(
                    () -> new UsernameNotFoundException("User '" + email + "' not found")));
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/api/version1.0/votes/**", "/api/version1.0/users/profile/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/version1.0/menus/**", "/api/version1.0/restaurants/**").authenticated()
                .requestMatchers("/api/**").hasRole(Role.ADMIN.name())
                .anyRequest().authenticated() // this setting is for H2 console only
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
                .headers().frameOptions().disable(); // this setting is for H2 console only
        return http.build();
    }
}
