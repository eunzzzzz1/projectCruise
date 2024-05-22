package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.develop.OpenBankDTO;
import com.cruise.project_cruise.mapper.DevelopOpenBankingMapper;
import com.cruise.project_cruise.mapper.TemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class DevelopOpenBankingServiceImpl implements DevelopOpenBankingService{

    @Autowired
    private DevelopOpenBankingMapper developOpenBankingMapper;

    public List<OpenBankDTO> getAccountList() throws Exception {
        return developOpenBankingMapper.getAccountList();
    }

    @Override
    public void insertAccount(OpenBankDTO openBankDTO) throws Exception {
        developOpenBankingMapper.insertAccount(openBankDTO);
    }

    @Override
    public void updateAccount(OpenBankDTO openBankDTO) throws Exception{
        developOpenBankingMapper.updateAccount(openBankDTO);
    }

    @Override
    public void deleteAccount(String account) throws Exception{
        developOpenBankingMapper.deleteAccount(account);
    }

    @Override
    public Map<String,String> getAccountBalance(String account) throws Exception{
        return developOpenBankingMapper.getAccountBalance(account);
    }

}
