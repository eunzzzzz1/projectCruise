package com.cruise.project_cruise.oauth.config;

import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// 시큐리티 설정에서 loginProcessingUrl("/loginProcessing"); 했으면
// /login요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 실행
@Service
public class PrincipalDetailsService implements UserDetailsService {

   @Autowired
    private UserService userService;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println(username); //이메일임 이걸 useremail사용하고 싶으면 security설정바꿔야했던것같음..
        UserDTO userDTO = new UserDTO();



        try {
            userDTO.setEmail(userService.selectEmail(username));
            if (userDTO.getEmail() != null) {
                userDTO.setUser_password(userService.selectPassWord(username));

                System.out.println("PrincipalDetailsService: " + userDTO);

                return new PrincipalDetails(userDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new UsernameNotFoundException("존재하지 않는 이메일입니다.: " + username);
    }
}
