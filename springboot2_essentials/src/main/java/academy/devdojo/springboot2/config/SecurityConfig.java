package academy.devdojo.springboot2.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import academy.devdojo.springboot2.service.DevDojoUserDetailsService;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final DevDojoUserDetailsService devDojoUserDetailsService;

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests((authorize) -> authorize
					.antMatchers("/animes/admin/**").hasRole("ADMIN")
					.antMatchers("/animes/**").hasRole("USER")
					.antMatchers("/actuator/**").permitAll());

		return http.authorizeHttpRequests((authorize) -> {
			try {
				authorize.anyRequest().authenticated().and().formLogin(withDefaults()).httpBasic(withDefaults());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {

		return httpSecurity
				.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(devDojoUserDetailsService)
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}
	
}
