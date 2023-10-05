package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;


public interface UserService {


    public String selectEmail(String email) throws  Exception;

    public String selectPassWord(String email) throws Exception;
    public void insertNormalUser(UserDTO dto) throws Exception;

    public void insertSocialUser(UserDTO dto) throws Exception;

    public UserDTO selectSocialInfo(String provider_id) throws Exception;

    public void updateUser(UserDTO dto) throws Exception;


}
