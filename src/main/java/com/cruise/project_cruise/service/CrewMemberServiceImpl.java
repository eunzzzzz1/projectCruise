package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.CrewMemberDTO;
import com.cruise.project_cruise.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrewMemberServiceImpl implements CrewMemberService {

    @Autowired
    private UserMapper mapper;

    @Override
    public void insertCrewMember(CrewMemberDTO dto) throws Exception {
        mapper.insertCrewMember(dto);
    }

    @Override
    public String selectCaptain(CrewMemberDTO dto) throws Exception {
        return mapper.selectCaptain(dto);
    }
}
