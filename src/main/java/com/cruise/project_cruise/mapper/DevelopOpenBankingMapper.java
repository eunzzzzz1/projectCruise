package com.cruise.project_cruise.mapper;

import com.cruise.project_cruise.dto.TemplateDTO;
import com.cruise.project_cruise.dto.develop.OpenBankDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DevelopOpenBankingMapper {
    public List<OpenBankDTO> getAccountList() throws Exception;
    public void insertAccount(OpenBankDTO openBankDTO) throws Exception;
    public void updateAccount(OpenBankDTO openBankDTO) throws Exception;
    public void deleteAccount(@Param("account") String account) throws Exception;
}
