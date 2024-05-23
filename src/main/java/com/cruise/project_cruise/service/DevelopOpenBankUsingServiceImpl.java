package com.cruise.project_cruise.service;

import com.cruise.project_cruise.dto.TemplateDTO;
import com.cruise.project_cruise.dto.develop.OpenBankUsingDTO;
import com.cruise.project_cruise.exception.TransferLackOfBalanceException;
import com.cruise.project_cruise.exception.TransferMoneyZeroException;
import com.cruise.project_cruise.exception.TransferNoDataException;
import com.cruise.project_cruise.mapper.DevelopOpenBankUsingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class DevelopOpenBankUsingServiceImpl implements DevelopOpenBankUsingService {

    @Autowired
    private DevelopOpenBankUsingMapper developOpenBankUsingMapper;

    @Override
    public List<OpenBankUsingDTO> getUsingList(String selectedAccount) throws Exception {
        return developOpenBankUsingMapper.getUsingList(selectedAccount);
    }

    @Override
    public List<OpenBankUsingDTO> searchInquiryForDate(String selectedAccount, String startDate, String endDate) throws Exception {
        return developOpenBankUsingMapper.searchInquiryForDate(selectedAccount,startDate,endDate);
    }

    @Override
    public List<OpenBankUsingDTO> searchInquiryForContent(String selectedAccount, String content) throws Exception {
        return developOpenBankUsingMapper.searchInquiryForContent(selectedAccount,content);
    }

    @Override
    public List<OpenBankUsingDTO> searchInquiryForDateAndContent(String selectedAccount, String startDate, String endDate, String content) throws Exception {
        return developOpenBankUsingMapper.searchInquiryForDateAndContent(selectedAccount,startDate,endDate,content);
    }

    @Override
    public Map<String,Integer> searchSumForDateAndContent(String selectedAccount, String startDate, String endDate, String content) throws Exception {
        return developOpenBankUsingMapper.searchSumForDateAndContent(selectedAccount,startDate,endDate,content);
    }

    @Override
    public void insertUsing(OpenBankUsingDTO openBankUsingDTO) throws Exception {
        developOpenBankUsingMapper.insertUsing(openBankUsingDTO);
    }

    @Override
    public int getUsingMaxNum() throws Exception {
        return developOpenBankUsingMapper.getUsingMaxNum();
    }

    @Override
    public int getBalance(int openUseNum, String selectedAccount) {
        return developOpenBankUsingMapper.getBalance(openUseNum, selectedAccount);
    }

    @Override
    public int getAccountMaxNum(String selectedAccount) throws Exception {
        return developOpenBankUsingMapper.getAccountMaxNum(selectedAccount);
    }

    @Override
    public String getLastDate(String selectedAccount, int selectedNum) throws Exception {
        return developOpenBankUsingMapper.getLastDate(selectedAccount, selectedNum);
    }

    @Override
    public void updateUsing(OpenBankUsingDTO openBankUsingDTO) throws Exception {
        developOpenBankUsingMapper.updateUsing(openBankUsingDTO);
    }

    @Override
    public void deleteUsing(int openUseNum) throws Exception {
        developOpenBankUsingMapper.deleteUsing(openUseNum);
    }

    @Override
    public void updateAccountTableBalance(int openBalance, String openAccount) throws Exception {
        developOpenBankUsingMapper.updateAccountTableBalance(openBalance,openAccount);
    }

    @Override
    @Transactional(rollbackFor = {TransferNoDataException.class, TransferLackOfBalanceException.class, Exception.class})
    public void transferProcess(String withdrawAccount,
                               String depositAccount,
                               String transferDate,
                               String transferMoney,
                               String transferContent) throws Exception {

        // 필요한 데이터가 없을 경우 예외처리
        TransferNoDataException noDataError = new TransferNoDataException("NO_DATA");
        if (transferDate==null || transferDate.equals("")) {
            System.out.println("[OpenBanking] Rollback - 거래일자 없음");
            throw noDataError;
        } else if (transferMoney==null || transferMoney.equals("")) {
            System.out.println("[OpenBanking] Rollback - 거래금액 없음");
            throw noDataError;
        } else if (transferContent==null || transferContent.equals("")) {
            System.out.println("[OpenBanking] Rollback - 거래내용 없음");
            throw noDataError;
        }

        int transferMoneyInt = Integer.parseInt(transferMoney);

        // green 출금 DTO 세팅하기
        OpenBankUsingDTO withdrawDTO = new OpenBankUsingDTO();
        int openMaxNum = getUsingMaxNum() +1;

        // 출금 내역 작성하기
        withdrawDTO.setOpenuse_num(openMaxNum);
        withdrawDTO.setOpen_account(withdrawAccount);
        withdrawDTO.setOpenuse_date(transferDate);
        withdrawDTO.setOpenuse_assort("O");
        withdrawDTO.setOpenuse_outmoney(transferMoneyInt);
        withdrawDTO.setOpenuse_inmoney(0);
        withdrawDTO.setOpenuse_content(transferContent);

        // 잔액 작성
        // 마지막 잔액 가지고오기
        int withdrawBalance = 0;
        int withdrawMaxNum = getAccountMaxNum(withdrawAccount);
        if(withdrawMaxNum!=0) {
            withdrawBalance = getBalance(withdrawMaxNum, withdrawAccount);
        }
        // 잔액부족 검사

        // 잔액 세팅하기
        int updateWithdrawBal = withdrawBalance-transferMoneyInt;
        withdrawDTO.setOpenuse_balance(updateWithdrawBal);


        // green 입금 DTO 세팅하기
        OpenBankUsingDTO depositDTO = new OpenBankUsingDTO();

        // 입금 내역 작성하기
        depositDTO.setOpenuse_num(openMaxNum +1); // 출금내역에서 +1 해줬으니까...
        depositDTO.setOpen_account(depositAccount);
        depositDTO.setOpenuse_date(transferDate);
        depositDTO.setOpenuse_assort("I");
        depositDTO.setOpenuse_outmoney(0);
        depositDTO.setOpenuse_inmoney(transferMoneyInt);
        depositDTO.setOpenuse_content(transferContent);

        // 잔액 작성
        // 마지막 잔액 가지고오기
        int depositBalance = 0;
        int depositMaxNum = getAccountMaxNum(depositAccount);
        if(depositMaxNum!=0) {
            depositBalance = getBalance(depositMaxNum, depositAccount);
        }

        // 잔액 세팅하기
        int updateDepositBal = depositBalance+transferMoneyInt;

        depositDTO.setOpenuse_balance(updateDepositBal);

        // 입출금 진행하기 (DB에 각각 insert)
        insertUsing(withdrawDTO);
        updateAccountTableBalance(updateWithdrawBal,withdrawAccount);
        System.out.println("[OpenBanking] Insert - " + withdrawAccount + " / " + withdrawDTO.getOpenuse_assort() + " / " + transferMoney + "원 / " + transferContent);
        insertUsing(depositDTO);
        updateAccountTableBalance(updateDepositBal, depositAccount);
        System.out.println("[OpenBanking] Insert - " + depositAccount + " / " + depositDTO.getOpenuse_assort() + " / " + transferMoney + "원 / " + transferContent);


        // 예외처리 (Rollback)
        TransferMoneyZeroException moneyZeroException = new TransferMoneyZeroException("TRANSFER_MONEY_ZERO");
        if (transferMoneyInt==0
                || withdrawDTO.getOpenuse_outmoney()==0 || depositDTO.getOpenuse_inmoney()==0) {
            System.out.println("[OpenBanking] Rollback - 거래금액이 0임");
            throw moneyZeroException;
        }

        // 2. 잔액이 부족할 경우
        TransferLackOfBalanceException lackOfBalanceError = new TransferLackOfBalanceException("LACK_OF_BALANCE");
        if(withdrawBalance-transferMoneyInt<0 || updateWithdrawBal<0 || withdrawDTO.getOpenuse_balance()<0) {
            // 잔액부족 오류
            System.out.println("[OpenBanking] Rollback "+ withdrawAccount + " 출금 오류 - 잔액 부족");
            throw lackOfBalanceError;
        }

    }
}
