package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.develop.OpenBankUsingDTO;
import com.cruise.project_cruise.exception.TransferLackOfBalanceException;
import com.cruise.project_cruise.exception.TransferNoDataException;
import com.cruise.project_cruise.mapper.DevelopOpenBankUsingMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface DevelopOpenBankUsingService {

    public List<OpenBankUsingDTO> getUsingList(String selectedAccount) throws Exception;
    public void insertUsing(OpenBankUsingDTO openBankUsingDTO) throws Exception;
    public int getUsingMaxNum() throws Exception;
    public int getBalance(int openUseNum, String selectedAccount);
    public int getAccountMaxNum(String selectedAccount) throws Exception;
    public String getLastDate( String selectedAccount,int selectedNum) throws Exception;
    public void updateUsing(OpenBankUsingDTO openBankUsingDTO) throws Exception;
    public void deleteUsing(int openUseNum) throws Exception;
    public void updateAccountTableBalance(int openBalance, String openAccount) throws Exception;
    public void transferProcess(
            @RequestParam("withdrawAccount") String withdrawAccount,
            @RequestParam("depositAccount") String depositAccount,
            @RequestParam("transferDate") String transferDate,
            @RequestParam("transferMoney") Integer transferMoney,
            @RequestParam("transferContent") String transferContent
    ) throws Exception;

}
