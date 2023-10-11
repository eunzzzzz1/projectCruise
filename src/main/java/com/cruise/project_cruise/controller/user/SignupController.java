package com.cruise.project_cruise.controller.user;


import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class SignupController {

    @Autowired
   private final UserService userService;



    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

   @GetMapping("/signup")
    public String mySignup() throws Exception{
       return "signup/signupForm";
   }



   @PostMapping("/submitSignup")
   public String submitSignup(@RequestParam String username, @RequestParam String password, @RequestParam String name,
                              @RequestParam String tel, @RequestParam String address1, @RequestParam String address2,
                            @RequestParam String email_verified, @RequestParam String provider) throws Exception {

       UserDTO user = new UserDTO();

       user.setEmail(username);
       user.setEmail_verified(email_verified);

       BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

       String rawPassword = password;
       String encPassword = bCryptPasswordEncoder.encode(rawPassword);
       user.setUser_password(encPassword);
       user.setUser_name(name);
       user.setProvider(provider);
       user.setTel(tel);
       user.setAddress1(address1);
       user.setAddress2(address2);

        userService.insertNormalUser(user);

        System.out.println("회원가입성공");
        return "login/loginForm";
   }

    @GetMapping("/nextSocialSignUpForm")
    public String nextSocialSignup () throws Exception {



       return "signup/nextSocialSignUpForm";
    }

    @PostMapping("/socialSignupProcessing")
    public String socialSignupProcessing (HttpServletRequest request, OAuth2AuthenticationToken authenticationToken, @AuthenticationPrincipal OAuth2User principal, @RequestParam String name, @RequestParam String tel, @RequestParam String address1, @RequestParam String address2) throws Exception{




        UserDTO userDTO = new UserDTO();
        String email = null;
        String clientRegistrationId = null;

        if (authenticationToken != null) {
            clientRegistrationId = authenticationToken.getAuthorizedClientRegistrationId();

        }

        System.out.println("사용자가 " + clientRegistrationId + "로 로그인했습니다.");

        if ("google".equals(clientRegistrationId)) {
            email = principal.getAttribute("email"); // 구글 로그인 진행시
        } else if ("naver".equals(clientRegistrationId)) {
            // 네이버 로그인 처리
            JSONParser jsonParser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            String apiResult = mapper.writeValueAsString(principal.getAttributes().get("response"));
            JSONObject jsonObject = (JSONObject) jsonParser.parse(apiResult);
            email = (String) jsonObject.get("email");
        } else if("kakao".equals(clientRegistrationId)){

            JSONParser jsonParser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            String apiResult = mapper.writeValueAsString(principal.getAttributes().get("kakao_account"));
            JSONObject jsonObject = (JSONObject) jsonParser.parse(apiResult);
            email = (String) jsonObject.get("email");

        }else{
            System.out.println("로그인에 실패하였습니다.");
        }

        userDTO.setEmail(email);
        userDTO.setUser_name(name);
        userDTO.setTel(tel);
        userDTO.setAddress1(address1);
        userDTO.setAddress2(address2);

        userService.updateUser(userDTO);

        HttpSession session = request.getSession();

        session.setAttribute("lastLoginMethod",clientRegistrationId);
        System.out.println(session.getAttribute("lastLoginMethod"));


        return "redirect:/";
    }




    @GetMapping("/showHiddenDiv")
    public String showHiddenDiv() {
        // 숨겨진 div를 보여주는 페이지로 이동 (Thymeleaf 템플릿)
        return "signup/signupForm"; // 보여주려는 Thymeleaf 템플릿의 경로
    }





        @PostMapping("/checkEmail")
        public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) throws Exception {

            Boolean emailExists= false;
            // 데이터베이스에서 이메일 중복 확인
           if(email.equals(userService.selectEmail(email))){
                emailExists = true;
           }

            // 결과를 클라이언트에게 반환
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", emailExists);

            return ResponseEntity.ok(response);
        }


}
