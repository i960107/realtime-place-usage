package com.example.realtimeusage.config;

import com.example.realtimeusage.service.AdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public void configureGlobal(
            AuthenticationManagerBuilder auth,
            PasswordEncoder passwordEncoder,
            AdminService adminService
    ) throws Exception {
        auth.userDetailsService(adminService)
                .passwordEncoder(passwordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(request -> {
                    // 주로 antMatcher  사용하기.
                    request.antMatchers("/", "/events/**", "/places/**", "/error", "/actuator/**")
                            .permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .formLogin(login -> {
                    login.permitAll() //loginPage, loginProcessingUrl, successfulUrl 다 permitAll
                            .loginPage("/login")
                            .defaultSuccessUrl("/admin/places");
                })
                .logout(logout -> {
                    logout.permitAll()
                            .logoutSuccessUrl("/logout")
                            .logoutSuccessUrl("/");
                });
    }
}
