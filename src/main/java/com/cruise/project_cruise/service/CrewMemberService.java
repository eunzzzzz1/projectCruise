package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.CrewMemberDTO;

public interface CrewMemberService {

    public void insertCrewMember(CrewMemberDTO dto) throws Exception;
    public String selectCaptain(CrewMemberDTO dto) throws Exception;
}
