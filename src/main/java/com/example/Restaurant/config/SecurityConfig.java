package com.example.Restaurant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.AuthorityUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions().sameOrigin())  // برای H2 Console
                .authorizeHttpRequests(auth -> auth
                        // Public URLs
                        .requestMatchers(
                                "/**",
                                "/home",
                                "/register",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/h2-console/**",
                                "/img/**",
                                "/menu/**",        // اجازه دسترسی به همه منوها
                                "/cart/**",        // اجازه دسترسی به سبد خرید
                                "/orders/checkout" // اجازه دسترسی به ثبت سفارش
                        ).permitAll()
                        // H2 Console - فقط برای ADMIN
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
                        // Admin URLs
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Chef & Sous Chef URLs
                        .requestMatchers("/orders/**", "/ingredients/**")
                        .hasAnyRole("CHEF", "SOUS_CHEF")
                        // Waiter URLs
                        .requestMatchers("/waiters/**", "/menu/order/**").hasRole("WAITER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            var roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                            String redirectURL = roles.contains("ROLE_ADMIN") ? "/admin/dashboard" :
                                    roles.contains("ROLE_WAITER") ? "/dashboard/waiter-dashboard" :
                                            roles.contains("ROLE_CHEF") ? "/dashboard/chef-dashboard" :
                                                    "/dashboard/souschef-dashboard";
                            response.sendRedirect(redirectURL);
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}