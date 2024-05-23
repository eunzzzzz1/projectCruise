package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.TemplateDTO;
import com.cruise.project_cruise.dto.develop.OpenBankDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface DevelopOpenBankingService {
    public List<OpenBankDTO> getAccountList() throws Exception;
    public String getAccountInfo(String account) throws Exception;
    public void insertAccount(OpenBankDTO openBankDTO) throws Exception;
    public void updateAccount(OpenBankDTO openBankDTO) throws Exception;
    public void deleteAccount(String account) throws Exception;
    public Map<String,String> getAccountBalance(String account) throws Exception;

}
