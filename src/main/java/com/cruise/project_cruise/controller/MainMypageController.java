package com.cruise.project_cruise.controller;

import com.cruise.project_cruise.dto.CrewDTO;
import com.cruise.project_cruise.dto.CrewMemberDTO;

import com.cruise.project_cruise.dto.MyAccountDTO;

import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.dto.develop.OpenBankDTO;
import com.cruise.project_cruise.service.MypageService;
import com.cruise.project_cruise.token.JwtTokenizer;
import com.cruise.project_cruise.util.CrewBoardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainMypageController {

    @Autowired
    private MypageService mypageService;

    @Autowired
    JwtTokenizer jwtTokenizer;

    @Autowired
    CrewBoardUtil myUtil;

    /*
        로그인 후 바로 연결되는 마이페이지 메인창 메소드
        크루와 계좌 0이면 zero페이지 보여지고
        0 이상이면 all페이지 보여짐
        --
        크루 조회, 계좌 조회, 계좌 등록, 초대 구분
     */
    @RequestMapping(value = "/mypage/mypage_all",method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView all(HttpSession session, HttpServletRequest request, @RequestParam(required = false) String anum,
                            @RequestHeader(value = "Authorization", required = false) String accessToken ,
                            @AuthenticationPrincipal OAuth2User principal, Principal principal2) throws  Exception {



        String email = null;
        Optional<String> emailOptional = jwtTokenizer.extractEmail(accessToken);

        String group = (String) session.getAttribute("group");
        String num = (String) session.getAttribute("num");

        //초대받았을때와 아닐때를 구분 할 수 있음
        if (group == null && num == null) { //초대받지 않았고 로그인을하면 group과 num을하면 지워지므로 세션값으로 다시 비교

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
        }else { //초대받았을때

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
        }

        if (session.getAttribute("email")==null){
            session.setAttribute("email", email);
            email = (String) session.getAttribute("email");
        }else {
            email = (String) session.getAttribute("email");
            session.setAttribute("email",email);
        }




        System.out.println("뒷부분: "+ email);

        List<CrewDTO> crewLists = mypageService.getCrews(email); //크루 정보
        List<CrewMemberDTO> crewNumLists = mypageService.getCrewNums(email); //크루맴버의 크루번호
        List<OpenBankDTO> openAccPwd = mypageService.getOpenAccPWd(email); //가상계좌 비밀번호
        List<MyAccountDTO> accountLists = mypageService.getAccountList(email); //등록된 계좌정보
        List<OpenBankDTO> accountBalLists = mypageService.getAccountBals(email); //가상 계좌의 잔액만
        UserDTO userInfo = mypageService.getUserInfo(email); // 로그인한 사용자 정보.이름

        ModelAndView mav = new ModelAndView();

        //크루, 계좌가 있으면 all 페이지
        if(!crewNumLists.isEmpty() || !accountLists.isEmpty()){
            mav.setViewName("mypage/mypage_all");

            mav.addObject("crewLists",crewLists);
            mav.addObject("userInfo",userInfo);

            //계좌 비밀번호가 있으면
            if(openAccPwd != null){
                mav.addObject("openAccPwd",openAccPwd);
            }

            //가상 계좌 잔액을 내 계좌 잔액dto에 set
            for(int i=0;i<accountBalLists.size();i++){
                int balance = accountBalLists.get(i).getOpen_balance();
                accountLists.get(i).setMyaccount_balance(balance);
            }

            mav.addObject("accountLists",accountLists);

        }else { //크루, 계좌 없으면 zero
            mav.setViewName("mypage/mypageZero");
        }

        //계좌번호 있으면 계좌 등록
        if(anum !=null){

            mypageService.insertAccount(email,anum);
            mav.setViewName("redirect:/mypage/mypage_all");
            return mav;
        }


        return mav; //프론트에서 요청했을때는 이 리턴이 프론트로 가는듯 그래서 화면이 안나오는것 같음
    }
}

