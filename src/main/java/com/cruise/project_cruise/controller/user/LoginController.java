package com.cruise.project_cruise.controller.user;

import com.cruise.project_cruise.oauth.config.PrincipalDetails;
import com.cruise.project_cruise.service.LoginService;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/login")
public class LoginController {

    LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("")
    public String myLogin() throws Exception{
        return "login/loginForm";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails: " + principalDetails.getUserDTO());
        return "user";
    }


    /*
    @GetMapping("/test")                                                 //AuthenticationPrincipal 로 세션에 접근가능
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {

        System.out.println("/test/login==========================");

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        System.out.println("authentication: " + principalDetails.getUserDTO());
        System.out.println("userDetails: "+ userDetails.getUserDTO());

        return "세션정보 확인";
    }


    @GetMapping("/test/oauth")
    public @ResponseBody String testOAuthLogin(Authentication authentication) {

        System.out.println("/test/login==========================");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("authentication: " + oAuth2User.getAttributes());


        return "OAuth 세션정보 확인";
    }


    @GetMapping("/oauth2/code/{registrationId}")
    public void googleLogin(@RequestParam String code, @PathVariable String registrationId){
        loginService.socialLogin(code,registrationId);
    }

     */
}
