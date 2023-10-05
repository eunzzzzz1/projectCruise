package com.cruise.project_cruise.oauth.config;

// 시큐리티가 /login 주소 요청이오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행 완료가되면 시큐리티 session을만들어줌 (같은공간인데 security만의 세션공간 Security ContextHolder 키값에 넣음)
// 시큐리티가 가지고있는 세션에들어갈수있는 오브젝트 타입은 정해져있음 =>Authentication 타입객체
// Authentication 안에 User 정보가 있어야됨.
// User오브젝트타입 => UserDetails 타입 객체

// Security Session => Authentication => UserDetails

import com.cruise.project_cruise.dto.UserDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private UserDTO userDTO;
    private boolean isNewUser;
    private Map<String,Object> attributes;


    //일반 로그인 생성자
    public PrincipalDetails(UserDTO userDTO){
        this.userDTO = userDTO;
    }

    //OAuth 로그인 생성자
    public PrincipalDetails(UserDTO userDTO,boolean isNewUser,Map<String,Object> attributes){
        this.userDTO = userDTO;
        this.isNewUser = isNewUser;
        this.attributes = attributes;
    }


    public UserDTO getUserDTO(){
        return userDTO;
    }

    public boolean isNewUser(){
        return isNewUser;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {

        return null;
       // return (String) attributes.get("sub");
    }

    //해당 User의 권한을 리턴하는 곳 우리는 권한이 따로 없어서 사용 안할듯
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return userDTO.getUser_password();
    }

    @Override
    public String getUsername() {
        return userDTO.getUser_name();
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    //Ex) 우리 사이트 1년동안 회원이 로그인을 안하면!! 휴면 계정으로하거나 할떄 사용가능
    //현재시간 - 로그인시간 => 1년을 초과하면 false로 변경
    @Override
    public boolean isEnabled() {
        return true;
    }


}
