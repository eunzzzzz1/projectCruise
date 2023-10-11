package com.cruise.project_cruise.security.config;

//1.코드받기(인증),2.엑세스토큰받기(권한),3.사용자프로필 정보 가져오기 4.그 정보를 토대로 회원가입진행

import com.cruise.project_cruise.oauth.config.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity  //Spring Security 설정 활성화
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true) //secured 어노테이션 활성화 이걸로 권한있는사람만 들어가게 할수있음
public class WebSecurityConfigure {

    //Bean 적어주면 해당 메서드의 리턴되는 오브젝터를 IoC로 등록해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;



    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/**","/*").permitAll()
                .anyRequest().permitAll()
                .and()
                .formLogin() //formLogin()으로 인증 처리하겠다 선언
                .loginPage("/login")
                .loginProcessingUrl("/loginProcessing")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .invalidateHttpSession(false) //로그아웃하면 security session과 httpsession 같이 삭제되는거 방지
                .permitAll()
                .and()
                .oauth2Login()
                .loginPage("/signup") //구글 로그인이 완료된 뒤의 후처리가 필요함. Tip.(액세스토큰+사용자프로필정보를 받음)
                .successHandler(new CustomAuthenticaionSuccessHandler()) //defaultsuccessurl-- login으로 와서 로그인 완료하면 /가지만 다른 url로 온경우에는 그 Url로 보내
                .userInfoEndpoint()
                .userService(principalOauth2UserService);


        return http.build();
    }
}
