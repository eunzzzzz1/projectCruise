package com.cruise.project_cruise.security.config;

import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.oauth.config.PrincipalDetails;
import com.cruise.project_cruise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomAuthenticaionSuccessHandler implements AuthenticationSuccessHandler {



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); //현재 로그인한 사용자 정보 가져오기

        if (userDetails instanceof PrincipalDetails) {

            PrincipalDetails principalDetails = (PrincipalDetails) userDetails;
            UserDTO userDTO = principalDetails.getUserDTO();
            System.out.println(userDTO);
            boolean isNewUser = principalDetails.isNewUser();

            if (isNewUser) {
                response.sendRedirect("/nextSocialSignUpForm");
            } else {
                HttpSession session = request.getSession();

                session.setAttribute("lastLoginMethod",userDTO.getProvider());
                System.out.println(session.getAttribute("lastLoginMethod"));

                response.sendRedirect("/");
            }


        }

    }
}






