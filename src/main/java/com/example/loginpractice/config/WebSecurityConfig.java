package com.example.loginpractice.config;

//import com.example.loginpractice.jwt.JwtFilter;
import com.example.loginpractice.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity  //SpringSecurity 사용 위한 어노테이션, 기본적으로는 csrf 활성화
@RequiredArgsConstructor
public class WebSecurityConfig {

//    private final JwtProvider jwtProvider;

//    @Bean
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable);
//
//        // CSRF(사용자의 권한을 가지고 특정 동작을 수행하도록 유도하는 공격) 비활성화,
//        // REST API 서버는 stateless하게 개발하기 때문에 사용자 정보를 Session에 저장 안함
//        // jwt 토큰을 Cookie에 저장하지 않는다면, CSRF에 어느정도는 안전.
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception {
        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
//                        .requestMatchers("/api/authenticate").permitAll()
//                        .requestMatchers("/api/signup").permitAll()
                                .requestMatchers("/user/**").permitAll()
                                //.requestMatchers("/api/signup").permitAll()
                                .anyRequest().authenticated()
                );
//                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable()
//                .and().authorizeRequests()
//                .antMatchers("/home").permitAll()
//                .antMatchers("/mypage").authenticated()
//                .anyRequest().authenticated()
//        return http.build();
//    }

}
