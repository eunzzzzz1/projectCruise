package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;



    @Override
    public String selectEmail(String email) throws Exception {
        return userMapper.selectEmail(email);
    }

    @Override
    public String selectPassWord(String email) throws Exception {
        return userMapper.selectPassWord(email);
    }

    @Override
    public void insertNormalUser(UserDTO dto) throws Exception {
        userMapper.insertNormalUser(dto);
    }

    @Override
    public void insertSocialUser(UserDTO dto) throws Exception {
        userMapper.insertSocialUser(dto);
    }

    @Override
    public UserDTO selectSocialInfo(String provider_id) throws Exception {
       return userMapper.selectSocialInfo(provider_id);
    }

    @Override
    public void updateUser(UserDTO dto) throws Exception {
        userMapper.updateUser(dto);
    }


}
