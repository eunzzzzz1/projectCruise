package com.cruise.project_cruise.controller;

import com.cruise.project_cruise.dto.CrewMemberDTO;
import com.cruise.project_cruise.oauth.config.PrincipalDetails;
import com.cruise.project_cruise.oauth.provider.KakaoUserInfo;
import com.cruise.project_cruise.service.CrewMemberInviteService;
import com.cruise.project_cruise.service.UserService;
import com.cruise.project_cruise.token.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.Console;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class HomeController {

    @Autowired
    UserService userService;
    @Autowired
    CrewMemberInviteService crewMemberInviteService;

    @Autowired
    JwtTokenizer jwtTokenizer;




    @RequestMapping(value = "/",method = {RequestMethod.GET, RequestMethod.POST})
    public String home(HttpSession session, Model model, @RequestParam(name = "group", required = false) String group,
                       @RequestParam(name = "num", required = false) String num,@RequestHeader(value = "Authorization", required = false) String accessToken, @AuthenticationPrincipal OAuth2User principal, Principal principal2) throws Exception {
        //이것 때문에 엄청고생했는데 결국 OAuth2User 에서는 소셜로그인 정보만 담기는거였음
        //근데 일반로그인해서 콘솔에 principal찍어봐도 값은 나오는데 왜 Null이라고 인식하는지...
        //더 자세한 공부가 필요함..
        String email = null;
        Optional<String> emailOptional = jwtTokenizer.extractEmail(accessToken);


        if (group != null && num != null) { // group과 num 값이 파라미터에 있을 때만 세션에 값을 설정


            session.setAttribute("group", group);
            session.setAttribute("num", num);

        }




        //초대받았을때와 아닐때를 구분 할 수 있음

        if (group == null && session.getAttribute("group") == null) { //초대받지 않았고 로그인을하면 group과 num을하면 지워지므로 세션값으로 다시 비교


            if (principal != null || emailOptional.isPresent()) { // 로그인 했을 경우
                System.out.println("초대받지 못한 로그인");
                if (emailOptional.isPresent()) { // 일반 로그인
                    email = emailOptional.get();
                    System.out.println(email);
                    session.setAttribute("email", email);
                } else { // 소셜 로그인
                    Map<String, Object> attributes = principal.getAttributes();

                    if (attributes.get("kakao_account") != null) { // 카카오 로그인
                        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                        email = (String) kakaoAccount.get("email");
                    } else if (attributes.get("response") != null) { // 네이버 로그인
                        Map<String, Object> naverAccount = (Map<String, Object>) attributes.get("response");
                        email = (String) naverAccount.get("email");
                    } else { //구글 로그인
                        email = (String) attributes.get("email");
                    }
                }
            }
        }

/*
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
            } else if ("kakao".equals(clientRegistrationId)) {
                JSONParser jsonParser = new JSONParser();
                ObjectMapper mapper = new ObjectMapper();
                String apiResult = mapper.writeValueAsString(principal.getAttributes().get("kakao_account"));
                JSONObject jsonObject = (JSONObject) jsonParser.parse(apiResult);
                email = (String) jsonObject.get("email");
            } else {
                System.out.println("사용자가 로그인 하지 않았습니다.");
                //System.out.println(principal.getAttributes());
            }
            */
        else if (group == null && session.getAttribute("group") != null) { //초대받았을때

            if (principal != null || emailOptional.isPresent()) { // 로그인 했을 경우
                System.out.println("초대받은 로그인");
                if (emailOptional.isPresent()) { // 일반 로그인
                    System.out.println("일반로그인");
                    email = emailOptional.get();
                    session.setAttribute("email", email);
                } else { // 소셜 로그인
                    Map<String, Object> attributes = principal.getAttributes();
                    System.out.println("소셜로그인");

                    if (attributes.get("kakao_account") != null) { // 카카오 로그인
                        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                        email = (String) kakaoAccount.get("email");
                    } else if (attributes.get("response") != null) { // 네이버 로그인
                        Map<String, Object> naverAccount = (Map<String, Object>) attributes.get("response");
                        email = (String) naverAccount.get("email");
                    } else { //구글 로그인
                        email = (String) attributes.get("email");
                    }
                }
            }


//            model.addAttribute("group", group);
//            model.addAttribute("num", num);
//            model.addAttribute("email", email);

        }

        System.out.println("세션직전 이메일" +email); //일반로그인시에는 여기까지는 오는데 바로아래 세션이 안들어감..이유를 모르겠음 그래서 일반로그인은
                                                    //이메일 받자마자 세션에 등록하는건 가능해서 그렇게 작성하였음
        session.setAttribute("email", email);



        //번호 이름 이메일을 데이터베이스에넣고 세션삭제

        return "main";
    }






        @GetMapping("/accept")
        public String accept(HttpSession session) throws Exception {

            CrewMemberDTO dto = new CrewMemberDTO();

            String email = (String) session.getAttribute("email");
            String crew_numStr = (String) session.getAttribute("num");
            int crew_num = Integer.parseInt(crew_numStr);

            dto.setCrew_num(crew_num);
            dto.setCmem_email(email);

            crewMemberInviteService.insertCrewMember(dto);

            session.removeAttribute("group");
            session.removeAttribute("num");


            return "redirect:/";
        }

        @GetMapping("/reject")
        public String reject(HttpSession session){
            session.removeAttribute("group");
            session.removeAttribute("num");


            return "redirect:/";
        }

        @GetMapping("/showModalWithAjax")
        @ResponseBody
        public String showModalWithAjax(@RequestParam("email") String email, @RequestParam("num") int num) throws Exception {

            CrewMemberDTO dto = new CrewMemberDTO();
            dto.setCrew_num(num);
            dto.setCmem_email(email);
            String captainYN = crewMemberInviteService.selectCaptain(dto);



            if(captainYN == null){
                captainYN="X";
            }

            System.out.println(captainYN);

            if(captainYN.equals("Y") || captainYN.equals("N")){//대장이거나 선원이면 이미 모임에있는것이니 모달창 띄어줄 필요가 없음 근데 대장이나 선원이 아니면 captain값은 null임
                return "error";
            }else{
                return "success";
            }

        }

        @GetMapping("/index")
        public String index(Model model){

        model.addAttribute("group","앙큼불여우");
        model.addAttribute("num",1);


            return "index";
        }
    }
