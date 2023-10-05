package com.cruise.project_cruise.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String email;
    private String email_verified;
    private String user_password;
    private String user_name;
    private String provider;
    private String provider_id;
    private String tel;
    private String pay_password;
    private String address1;
    private String address2;

}
