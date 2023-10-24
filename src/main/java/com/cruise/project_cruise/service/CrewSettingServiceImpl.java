package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.CrewDTO;
import com.cruise.project_cruise.dto.CrewMemberDTO;
import com.cruise.project_cruise.dto.ScheduleDTO;
import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.mapper.CrewSettingMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CrewSettingServiceImpl implements CrewSettingService {

    @Autowired
    private CrewSettingMapper crewSettingMapper;

    @Override
    public List<ScheduleDTO> getCrewScheList(int crewNum) throws Exception {
        return crewSettingMapper.getCrewScheList(crewNum);
    }

    @Override
    public UserDTO getUser(@Param("email") String userEmail) throws Exception{
        return crewSettingMapper.getUser(userEmail);
    }

    @Override
    public void updateCrewInfo(CrewDTO crewDTO) throws Exception {
        crewSettingMapper.updateCrewInfo(crewDTO);
    }

    @Override
    public void insertCrewSche(ScheduleDTO scheduleDTO) throws Exception {
        crewSettingMapper.insertCrewSche(scheduleDTO);
    }

    @Override
    public void updateCrewSche(ScheduleDTO scheduleDTO) throws Exception {
        crewSettingMapper.updateCrewSche(scheduleDTO);
    }

    @Override
    public void deleteCrewSche(int scheNum) throws Exception {
        crewSettingMapper.deleteCrewSche(scheNum);
    }

    @Override
    public int getScheMaxNum() throws Exception {
        return crewSettingMapper.getScheMaxNum();
    }

    @Override
    public List<Map<String,String>> getCrewMemberList(@Param("crew_num") int crewNum) throws Exception {
        return crewSettingMapper.getCrewMemberList(crewNum);
    }

    @Override
    public void deleteMember(String banEmail, int crewNum) throws Exception {
        crewSettingMapper.deleteMember(banEmail,crewNum);
    }
}
