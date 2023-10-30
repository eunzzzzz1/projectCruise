package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.CrewDTO;
import com.cruise.project_cruise.dto.MyAccountDTO;
import com.cruise.project_cruise.dto.TemplateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CrewDetailService {
    // Mapper Interface를 그대로 가져와주기.
    public boolean isMember(@Param("crewNum") int crewNum, @Param("userEmail") String userEmail) throws Exception;
    public boolean isCaptain(@Param("crewNum") int crewNum, @Param("userEmail") String userEmail) throws Exception;
    public List<MyAccountDTO> getUserAccountList(@Param("userEmail") String userEmail) throws Exception;
    public void deleteCrewMember(String cmemEmail, int crewNum) throws Exception;
    public CrewDTO getCrewData(int crewNum) throws Exception;
    public String getCaptainName(String captainEmail) throws Exception;
    public int getAccountBalance(String crewAccount) throws Exception;
    public Map<String,Object> getCrewUserInfo(@Param("crewNum")int crewNum, @Param("userEmail") String userEmail) throws Exception;
    public void updateCrewMemberPayment(@Param("crewNum")int crewNum, @Param("userEmail") String userEmail,
                                        @Param("payment") int payment, @Param("payCount") int payCount) throws Exception;
    public void updateCrewMustPayCount(@Param("crewNum")int crewNum) throws Exception;
}
