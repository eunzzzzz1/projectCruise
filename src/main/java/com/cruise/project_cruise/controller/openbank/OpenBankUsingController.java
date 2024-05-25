package com.cruise.project_cruise.controller.openbank;

import com.cruise.project_cruise.dto.develop.OpenBankDTO;
import com.cruise.project_cruise.dto.develop.OpenBankUsingDTO;
import com.cruise.project_cruise.exception.NoDataException;
import com.cruise.project_cruise.service.DevelopOpenBankUsingService;
import com.cruise.project_cruise.service.DevelopOpenBankingService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value="/develop/openbank/using")
@RestController
public class OpenBankUsingController {

    @Autowired
    private DevelopOpenBankUsingService developOpenBankUsingService;
    @Autowired
    private DevelopOpenBankingService developOpenBankingService;

// red 입출금 처리
    @PostMapping(value = "/transfer")
    public JSONArray transferProcess(
            @RequestParam("withdrawAccount") String withdrawAccount,
            @RequestParam("depositAccount") String depositAccount,
            @RequestParam("transferDate") String transferDate,
            @RequestParam("transferMoney") String transferMoney,
            @RequestParam("transferContent") String transferContent
    ) throws Exception {

        String repMessage = "success";
        JSONObject data = new JSONObject();

        try {
            OpenBankDTO wdDto = developOpenBankingService.getAccountInfo(withdrawAccount);
            if(wdDto == null) throw new NoDataException("WITHDRAWAL_ACCOUNT_DOES_NOT_EXIST");
            OpenBankDTO dpsDto = developOpenBankingService.getAccountInfo(depositAccount);
            if(dpsDto == null) throw new NoDataException("DEPOSIT_ACCOUNT_DOES_NOT_EXIST");

            developOpenBankUsingService.transferProcess(withdrawAccount,depositAccount,transferDate,transferMoney,transferContent);

            data.put("req_message", repMessage);
            data.put("wd_account_holder_name", wdDto.getOpen_name()); // 송금인 성명
            data.put("wd_bank_name", wdDto.getOpen_bank()); // 출금 계좌 기관명
            data.put("wd_account_num", withdrawAccount); // 출금 계좌
            data.put("dps_account_holder_name", dpsDto.getOpen_name()); // 수취인 성명
            data.put("dps_bank_name", dpsDto.getOpen_bank()); // 송금 계좌 기관명
            data.put("dps_account_num", depositAccount); // 송금 계좌

        } catch (Exception e) {
            repMessage = e.getMessage();
            data.put("req_message", repMessage);
        }

        JSONArray dataArray = new JSONArray();
        dataArray.add(data);

        return dataArray;
    }

// red 거래내역 조회
    @PostMapping(value = "/search")
    @ResponseBody
    public JSONArray searchInquiry(
            @RequestParam("searchType") int searchType,
            @RequestParam("selectedAccount") String selectedAccount,
            @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
            @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate
    ) throws Exception {
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        HashMap<String, Object> usingHash = new HashMap<>();

        // searchType
        // 0 : 계좌의 모든 거래내역
        // 1 : 계좌 + 특정일자
        // 2 : 계좌 + 특정 거래내용
        // 3 : 특정 거래내용 + 특정일자
        // 4 : 특정 거래내용 + 특정일자 간의 출금액 합계, 입금액 합계


        List<OpenBankUsingDTO> usingList = null;
        if(searchType >=0 && searchType <=3) {
            switch (searchType) {

                case 0:
                    usingList = developOpenBankUsingService.getUsingList(selectedAccount);
                    break;
                case 1:
                    if(startDate.isEmpty()) throw new NoDataException("NO_STARTDATE_DATA_EXCEPTION");
                    if(endDate.isEmpty()) throw new NoDataException("NO_ENDDATE_DATA_EXCEPTION");
                    usingList = developOpenBankUsingService.searchInquiryForDate(selectedAccount,startDate,endDate);
                    break;
                case 2:
                    if(content.isEmpty() || content.equals("")) throw new NoDataException("NO_CONTENT_DATA_EXCEPTION");
                    usingList = developOpenBankUsingService.searchInquiryForContent(selectedAccount,content);
                    break;
                case 3:
                    if(startDate.isEmpty()) throw new NoDataException("NO_STARTDATE_DATA_EXCEPTION");
                    if(endDate.isEmpty()) throw new NoDataException("NO_ENDDATE_DATA_EXCEPTION");
                    if(content.isEmpty()) throw new NoDataException("NO_CONTENT_DATA_EXCEPTION");
                    usingList = developOpenBankUsingService.searchInquiryForDateAndContent(selectedAccount,startDate,endDate,content);
                    break;
            }

            for(int i=0;i<usingList.size();i++) {
                usingHash.put("account",usingList.get(i).getOpen_account());
                usingHash.put("date",usingList.get(i).getOpenuse_date());
                usingHash.put("assort",usingList.get(i).getOpenuse_assort());
                usingHash.put("outMoney",usingList.get(i).getOpenuse_outmoney());
                usingHash.put("inMoney",usingList.get(i).getOpenuse_inmoney());
                usingHash.put("content",usingList.get(i).getOpenuse_content());
                usingHash.put("balance",usingList.get(i).getOpenuse_balance());

                jsonObject = new JSONObject(usingHash);
                jsonArray.add(jsonObject);
            }

        } else if (searchType==4) {

            if(startDate.isEmpty()) throw new NoDataException("NO_STARTDATE_DATA_EXCEPTION");
            if(endDate.isEmpty()) throw new NoDataException("NO_ENDDATE_DATA_EXCEPTION");

            Map<String,Integer> result = developOpenBankUsingService.searchSumForDateAndContent(selectedAccount,startDate,endDate,content);
            Map<String,Object> sumMap = new HashMap<>();
            sumMap.put("account", selectedAccount);
            sumMap.put("searchContent", content);
            sumMap.put("startDate", startDate);
            sumMap.put("endDate", endDate);
            sumMap.put("inMoney",result.get("INMONEY"));
            sumMap.put("outMoney",result.get("OUTMONEY"));

            jsonObject = new JSONObject(sumMap);
            jsonArray.add(sumMap);
        }

        return jsonArray;
    }

// red 잔액 조회
    @PostMapping(value = "/balance")
    @ResponseBody
    public JSONObject getAccountBalance(@RequestParam("account_num") String account_num) throws Exception {
        HashMap<String, Object> map = new HashMap<>();

        map.put("account_num", account_num);
        map.put("bank_name", developOpenBankingService.getAccountBalance(account_num).get("OPEN_BANK"));
        map.put("balance_amt", developOpenBankingService.getAccountBalance(account_num).get("OPEN_BALANCE"));

        return new JSONObject(map);
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
