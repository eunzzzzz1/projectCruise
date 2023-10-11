package com.cruise.project_cruise.oauth.config;

import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.oauth.provider.GoogleUserInfo;
import com.cruise.project_cruise.oauth.provider.KakaoUserInfo;
import com.cruise.project_cruise.oauth.provider.NaverUserInfo;
import com.cruise.project_cruise.oauth.provider.OAuth2UserInfo;
import com.cruise.project_cruise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;


    //구글로부터 받은 userRequest 데이터에 대한  후처리되는함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration: "+userRequest.getClientRegistration()); //registrationId로 어떤 Oauth로 로그인했는지 알수있음
        System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());


        OAuth2User oAuth2User = super.loadUser(userRequest);
        //구글로그인 버튼 클릭->구글로그인창->로그인완료->code를 리턴(OAuth-Client라이브러리)->Access Token요청
        //userRequest정보 -> loadUser함수-> 구글로부터 회원프로필 받아줌
        System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());

        String provider=null;
        String providerId=null;
        String email = null;
        String name = null;


        OAuth2UserInfo oAuth2UserInfo = null;

        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){

//             provider = userRequest.getClientRegistration().getRegistrationId(); //google
//             providerId = oAuth2User.getAttribute("sub");
//             email = oAuth2User.getAttribute("email");
//             name = oAuth2User.getAttribute("name");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

            System.out.println("구글 로그인 요청");
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {

            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));

            System.out.println("네이버 로그인 요청");
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            System.out.println(oAuth2User.getAttributes());

            oAuth2UserInfo = new KakaoUserInfo((Map) oAuth2User.getAttributes());


            System.out.println("카카오 로그인 요청");
        }


        //회원가입 강제로 진행

        UserDTO userDTO = new UserDTO();

        userDTO.setEmail(oAuth2UserInfo.getEmail());
        System.out.println(userDTO.getEmail());
        userDTO.setEmail_verified("1");
        userDTO.setProvider(oAuth2UserInfo.getProvider());
        System.out.println(userDTO.getProvider());
        userDTO.setProvider_id(oAuth2UserInfo.getProviderId());
        System.out.println(userDTO.getProvider_id());
        userDTO.setUser_name(oAuth2UserInfo.getName());//여기서 dto에 username이없으면 에러나서 넣어준거 이건 소셜에서 가져온 이름이고
        //실제이름은 따로 받아줄거임

        try {
            assert email != null;
            boolean isNewUser = !oAuth2UserInfo.getEmail().equals(userService.selectEmail(oAuth2UserInfo.getEmail()));
            if(isNewUser){

                //NewUser면 추가정보 받기전 데이터를 데이터베이스에저장
                userService.insertSocialUser(userDTO);
            }

            System.out.println("principalOauth2UserService");
            System.out.println(userDTO);

            //아무튼 dto에 값이 없으면 오류남
            //여기서 return 해주고 나면 .successhandler러로감 로그인성공했다고 인식함 실제로 소셜로그인은 성공한거임
            return new PrincipalDetails(userDTO,isNewUser,oAuth2User.getAttributes());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
