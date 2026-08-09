package com.esun.social_media.config;

import org.springframework.beans.factory.annotation.Autowired; // 新增
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.esun.social_media.filter.JwtAuthenticationFilter; // 新增

import java.util.List;

@Configuration
public class SecurityConfig {

    // 注入我們的警衛
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. 密碼加密工具
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 設定 API 權限規則
    /* 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 關閉 CSRF 防護 (因為我們是開發 RESTful API，通常不使用 Session，所以可以關閉)
            .csrf(AbstractHttpConfigurer::disable)
            // 設定路由授權規則
            .authorizeHttpRequests(auth -> auth
                // 允許所有人 (permitAll) 訪問註冊 API (將原本的單一網址改成多個網址)
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                // 其他任何請求都必須經過身分驗證 (authenticated)
                .anyRequest().authenticated()
            )

            // 告訴系統，把我們的自訂警衛 (jwtAuthenticationFilter) 
            // 安排在預設的帳號密碼過濾器 (UsernamePasswordAuthenticationFilter) 之前執行
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        // 1. 開啟 CORS 支援 (對應下方的 corsConfigurationSource Bean)
        http.cors(Customizer.withDefaults());
        
        // 2. 關閉 CSRF
        http.csrf(AbstractHttpConfigurer::disable);
        
        // 3. 設定路由授權規則
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/register", "/api/users/login").permitAll()
            .anyRequest().authenticated()
        );
        
        // 4. 把我們的警衛加進去
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    // 定義 CORS 允許的規則
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允許 Vue 的預設本機網址
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // 允許的 HTTP 方法
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 允許前端夾帶 Authorization (JWT 手環) 等 Header
        configuration.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 將這套規則套用到所有的 API 路徑 (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
