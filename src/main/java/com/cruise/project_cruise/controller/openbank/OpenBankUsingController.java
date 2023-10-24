package com.cruise.project_cruise.controller.openbank;

import com.cruise.project_cruise.dto.develop.OpenBankDTO;
import com.cruise.project_cruise.dto.develop.OpenBankUsingDTO;
import com.cruise.project_cruise.exception.TransferLackOfBalanceException;
import com.cruise.project_cruise.exception.TransferNoDataException;
import com.cruise.project_cruise.service.DevelopOpenBankUsingService;
import com.cruise.project_cruise.service.DevelopOpenBankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping(value="/develop/openbank/using")
@RestController
public class OpenBankUsingController {

    @Autowired
    private DevelopOpenBankUsingService developOpenBankUsingService;

// red 입출금 처리
    @RequestMapping(value = "/transfer")
    public String transferProcess(
            @RequestParam("withdrawAccount") String withdrawAccount,
            @RequestParam("depositAccount") String depositAccount,
            @RequestParam("transferDate") String transferDate,
            @RequestParam("transferMoney") Integer transferMoney,
            @RequestParam("transferContent") String transferContent
    ) throws Exception {

        String errorMsg = "";
        try {
            developOpenBankUsingService.transferProcess(withdrawAccount,depositAccount,transferDate,transferMoney,transferContent);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        return errorMsg;
    }


// red Develop 페이지 ModelAndView
    @GetMapping(value="")
    public ModelAndView accountUsingList(HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView();

        // 해당 계좌 리스트 가지고가기
        String selectedAccount = request.getParameter("account");
        List<OpenBankUsingDTO> usingList = developOpenBankUsingService.getUsingList(selectedAccount);

        // 해당 계좌의 마지막 거래일자 들고가기
        String lastDate = "";

        if(!usingList.isEmpty()) {
            int accountMaxNum = developOpenBankUsingService.getAccountMaxNum(selectedAccount);
            lastDate = developOpenBankUsingService.getLastDate(selectedAccount,accountMaxNum);
            lastDate = lastDate.replace(" ", "T"); // HTML 형태에 맞게 바꿔주기
        }
        
        mav.addObject("selectedAccount",selectedAccount);
        mav.addObject("usingList", usingList);
        mav.addObject("lastDate", lastDate);
        mav.setViewName("forDevelop/accountUsing");
        return mav;
    }

    @PostMapping(value = "/addInquiry")
    public ModelAndView accountUsingAdd(OpenBankUsingDTO openBankUsingDTO) throws Exception {
        ModelAndView mav = new ModelAndView();

        //0. 자주 사용할 변수
        String account = openBankUsingDTO.getOpen_account();
        //1. 최대값 가져오기
        int maxNum = developOpenBankUsingService.getUsingMaxNum();
        openBankUsingDTO.setOpenuse_num(maxNum + 1);

        //2. 마지막 행의 잔액 가지고오기
        int lastBalance = 0;
        int accountMaxNum = developOpenBankUsingService.getAccountMaxNum(account);
        if(accountMaxNum!=0) {
            lastBalance = developOpenBankUsingService.getBalance(accountMaxNum, account);
        }

        //3. 잔액 계산 후 세팅
        int inMoney = openBankUsingDTO.getOpenuse_inmoney();
        int outMoney = openBankUsingDTO.getOpenuse_outmoney();
        int updateBalance = 0;

        if(inMoney==0 && outMoney!=0) {
            // 지출이 있는 상태면
            updateBalance = lastBalance-outMoney;
            openBankUsingDTO.setOpenuse_balance(updateBalance);
            developOpenBankUsingService.updateAccountTableBalance(updateBalance, account);
        } else if (inMoney!=0 && outMoney==0) {
            // 수입이 있는 상태면
            updateBalance = lastBalance+inMoney;
            openBankUsingDTO.setOpenuse_balance(updateBalance);
            developOpenBankUsingService.updateAccountTableBalance(updateBalance, account);
        }

        //4. 날짜 형식 바꿔주기
            // POST 방식으로 넘어온 날짜는 2023-11-11T13:55 형태임
        String postDate = openBankUsingDTO.getOpenuse_date();
        openBankUsingDTO.setOpenuse_date(postDate.replace("T"," "));

        //4. insert 하기
        developOpenBankUsingService.insertUsing(openBankUsingDTO);
        System.out.println("[" + openBankUsingDTO.getOpen_account() + "] 계좌에 내역 추가 완료");

        mav.setViewName("redirect:?account=" + openBankUsingDTO.getOpen_account());
        return mav;
    }

    @PostMapping(value = "/updateInquiry")
    public ModelAndView accountUsingUpdate(OpenBankUsingDTO openBankUsingDTO) throws Exception {
        ModelAndView mav = new ModelAndView();

        developOpenBankUsingService.updateUsing(openBankUsingDTO);
        System.out.println("[" + openBankUsingDTO.getOpen_account() + "] 계좌내역 [" + openBankUsingDTO.getOpenuse_num() + "]번 수정 완료");

        mav.setViewName("redirect:?account=" + openBankUsingDTO.getOpen_account());
        return mav;
    }

    @PostMapping(value = "/deleteInquiry")
    public ModelAndView accountUsingDelete(OpenBankUsingDTO dto) throws Exception {
        ModelAndView mav = new ModelAndView();
        String redirectAccount = dto.getOpen_account();

        int updateBalance = 0;
        if(dto.getOpenuse_inmoney()!=0 && dto.getOpenuse_outmoney()==0) {
            System.out.println(dto.getOpenuse_inmoney());
            updateBalance = (-1) * dto.getOpenuse_inmoney();
        } else if (dto.getOpenuse_outmoney()!=0 && dto.getOpenuse_inmoney()==0) {
            System.out.println(dto.getOpenuse_outmoney());
            updateBalance = dto.getOpenuse_outmoney();
        }

        int lastBalance = 0;
        int accountMaxNum = developOpenBankUsingService.getAccountMaxNum(redirectAccount);
        if(accountMaxNum!=0) {
            lastBalance = developOpenBankUsingService.getBalance(accountMaxNum, redirectAccount);
            System.out.println(lastBalance);
        }

        developOpenBankUsingService.updateAccountTableBalance(lastBalance+updateBalance, redirectAccount);

        developOpenBankUsingService.deleteUsing(dto.getOpenuse_num());
        // 계좌 내역을 삭제한 후 잔액을 업데이트 하게 되면, 마지막 내역이 삭제된 상태에서 진행되기 때문에
        // 해당하는 내역을 기준으로 하는 것이 아니라, 그 전 내역을 바탕으로 처리가 되게 된다.
        // 그러므로 계좌 잔액을 업데이트 한 다음에 내역을 삭제해야한다.
        System.out.println("[" + dto.getOpen_account() + "] 계좌내역 [" + dto.getOpenuse_num() + "]번 삭제 완료");

        mav.setViewName("redirect:?account=" + redirectAccount);
        return mav;
    }

}
