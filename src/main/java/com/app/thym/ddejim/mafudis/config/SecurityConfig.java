package com.app.thym.ddejim.mafudis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
public class SecurityConfig {
    @Autowired
    private CustomLoginFailureHandler failureHandler;

    @Autowired
    private CustomLoginSuccessHandler successHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Permitir acceso a recursos estáticos (CSS, JS, etc.)
                        .requestMatchers("/css/**","/img/**", "/js/**", "/webjars/**", "/images/**","/api/**").permitAll()
                        // 2. Permitir acceso a la página de login a todos
                        .requestMatchers("/login").permitAll()
                        // Permitir acceso público a la página del organigrama
                        .requestMatchers("/organigrama7").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/forgot-password", "/reset-password").permitAll()
                        // 3. Todas las demás URLs requieren autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // 4. Especificar la URL de nuestra página de login personalizada
                        .loginPage("/login")
                        // 5. URL a la que el formulario enviará los datos (Spring se encarga de esto)
                        .loginProcessingUrl("/perform_login")
                        // 6. URL a la que se redirige tras un login exitoso
                        .successHandler(successHandler) // <- Usa tu handler de éxito
                        .failureHandler(failureHandler) // <- Usa tu handler de fallo
                        .permitAll()
                )
                .logout(logout -> logout
                        // 8. URL para activar el logout
                        .logoutUrl("/perform_logout")
                        // 9. URL a la que se redirige tras un logout exitoso
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}