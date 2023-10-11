package com.cruise.project_cruise.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CrewMemberDTO {

    private int crew_num;
    private String cmem_email;
    private String captain_YN;
    private Date joindate;
    private int totalpay;
    private int mustpaycount;
    private int realpaycount;
}
