package com.example.demo.security;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.demo.repository.UserService;
import com.example.demo.security.oauth.CustomOAuth2User;
import com.example.demo.security.oauth.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	
	 	@Autowired
		private CustomOAuth2UserService oauth2UserService;

		@Autowired
		private UserService userService;
	
	
//	@Autowired
//    private DataSource dataSource;
     
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
     
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
     
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
         
        return authProvider;
    }
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	.antMatchers("/oauth2/**").permitAll()
            .antMatchers("/").permitAll() //.hasAnyAuthority("USER","ADMIN")
            .antMatchers("/users").permitAll() //.hasAnyAuthority("USER","ADMIN","")
            .anyRequest().permitAll()
            .and()
            .formLogin()
            	.permitAll()
            	//.loginPage("/login")
            	.usernameParameter("email")
            	.passwordParameter("pass")
                .defaultSuccessUrl("/users")
                .permitAll()
            .and()
         // configure OAuth2
			.oauth2Login()
			.loginPage("/login")
			.userInfoEndpoint()
				.userService(oauth2UserService) // goi sang // oauth2userservice
			.and()
			
			//cập nhật thông tin người dùng trong cơ sở dữ liệu -
			//vì vậy hãy thêm mã sau để định cấu hình trình xử lý thành công xác thực
			.successHandler(new AuthenticationSuccessHandler() { //xu ly goi thanh cong

				@Override
				public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
						Authentication authentication) throws IOException, ServletException {
					CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

					//goi user service de tao tk sau khi dang nhap thanh cong bang fb
					userService.processOAuthPostLogin(oauthUser.getEmail());

					response.sendRedirect("/users");
				}
			})
			
			.and()			
            .logout().logoutSuccessUrl("/").permitAll()            
            .and()
            .exceptionHandling().accessDeniedPage("/403");
    }
     
   

}
