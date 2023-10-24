package com.cruise.project_cruise.controller.crew;

import com.cruise.project_cruise.dto.CrewDTO;
import com.cruise.project_cruise.dto.MyAccountDTO;
import com.cruise.project_cruise.dto.ScheduleDTO;
import com.cruise.project_cruise.dto.UserDTO;
import com.cruise.project_cruise.service.CrewDetailService;
import com.cruise.project_cruise.service.CrewSettingService;
import com.cruise.project_cruise.service.MypageService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RequestMapping(value="/crew/setting")
@RestController
public class CrewSettingController {

    @Autowired
    private CrewDetailService crewDetailService;
    @Autowired
    private CrewSettingService crewSettingService;


// red 풀캘린더 데이터 전달 URL
    // - main, setting, mypage에서 공동 사용
    @RequestMapping("/loadCrewSchedule")
    @ResponseBody
    public List<Map<String,Object>> loadCrewSchedule (@RequestParam("crewNum") int crewNum) throws Exception {
        List<ScheduleDTO> crewScheList = crewSettingService.getCrewScheList(crewNum);

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        HashMap<String, Object> calHash = new HashMap<>();

        boolean allDay = false;

        for (int i = 0; i < crewScheList.size(); i++) {
            calHash.put("id",crewScheList.get(i).getSche_num());
            calHash.put("title", crewScheList.get(i).getSche_title());
            calHash.put("start", crewScheList.get(i).getSche_start());

            // Allday가 true일 경우, 시작일과 마감일을 18~20일로 잡아도 달력상엔 18~19일로 표시됨.
            // 이를 방지하기 위함
            SimpleDateFormat endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date endDateObj = endDateFormat.parse(crewScheList.get(i).getSche_end());
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDateObj);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            endDateObj = cal.getTime();

            String AlldayEndDateString = endDateFormat.format(endDateObj);

            if(crewScheList.get(i).getSche_alldayTF().equals("true")){
                allDay = true;
                calHash.put("end", AlldayEndDateString);
            } else {
                allDay = false;
                calHash.put("end", crewScheList.get(i).getSche_end());
            }
            calHash.put("allDay", allDay);
            calHash.put("color", crewScheList.get(i).getSche_assort());
            calHash.put("textColor", "#FFFFFF");

            jsonObject = new JSONObject(calHash);
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

// red 크루 관리

    // green 크루 관리페이지 메인
    @RequestMapping(value="")
    public ModelAndView crewSetting(HttpSession session, HttpServletRequest request) throws Exception {
        ModelAndView mav = new ModelAndView();

        // 선언부
        int crewNum = Integer.parseInt(request.getParameter("crewNum"));
        CrewDTO dto = crewDetailService.getCrewData(crewNum); // 크루 정보
        String userEmail = (String) session.getAttribute("email"); // 접속자 이메일

        List<Map<String,String>> memberList = crewSettingService.getCrewMemberList(crewNum);
        int memberCount = memberList.size(); // 크루 선원 목록
            // 크루 잔액
        int crewAccountBalance = crewDetailService.getAccountBalance(dto.getCrew_accountid());
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        String crewAccountBalanceStr = decimalFormat.format(crewAccountBalance);



        // 로그인 / 로그아웃 여부 걸러내기
        if(userEmail==null||userEmail.isEmpty()) {
            System.out.println("[CrewController] 로그인하지 않은 사용자가 [" + crewNum + " - " + dto.getCrew_name() + "] 관리에 접근");
            mav.addObject("status","logout");
            mav.setViewName("crew/wrongAccess");

            return mav;
        }

        System.out.println("[CrewController] " + userEmail + "이 [" + crewNum + " - " + dto.getCrew_name() + "] 관리에 접근");

        // 선장이 아닌 사용자를 걸러내기
        if(!crewDetailService.isCaptain(crewNum,userEmail)) {
            System.out.println("[CrewController] " + userEmail + "은 선장이 아님");
            mav.addObject("status","notCaptain");
            mav.setViewName("crew/wrongAccess");

            return mav;
        }


        // bold 크루 정보수정 페이지
            UserDTO crewCaptain = crewSettingService.getUser(dto.getCaptain_email()); // 선장 정보

            // 항해 일수
            Date todayDateObj = new Date(); // 오늘 날짜

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date crewCreatedDateObj = formatter.parse(dto.getCrew_created()); // Date 객체로 변환

            long dateDifference = todayDateObj.getTime() - crewCreatedDateObj.getTime() + 1; // 개설일을 1일로
            dateDifference = dateDifference / (1000 * 60 * 60 * 24);
            String crewSailingDayCount = Long.toString(dateDifference) + "일";

        // TODO bold 크루 선원탈퇴 페이지
            // 선원들 목록을 리스트로 뽑아와야해.


        // TODO bold 잔액 1/N하기 View

            // 각 선원별 account정보들을 list에 담고, 이를 다시 map에 담기
        Map<String,List<MyAccountDTO>> memberAccountMap = new HashMap<>();
            // map의 key는 그 회원의 이메일로 주고, View에서 타임리프를 통해 불러낸다.
        for(int i=0;i<memberCount;i++) {
            List<MyAccountDTO> mem_accounts = crewDetailService.getUserAccountList(memberList.get(i).get("MEM_EMAIL"));
            memberAccountMap.put(memberList.get(i).get("MEM_EMAIL"),mem_accounts);
        }

            // 크루 선원들의 총 납입횟수
        int crewMemPayCountSum = crewSettingService.getMemberPayCountSum(crewNum);
            // 1회 납입 당 분담금액
        double amountPerPaymentDouble = ((double)crewAccountBalance/(double)crewMemPayCountSum);
        amountPerPaymentDouble = Math.floor(amountPerPaymentDouble); // 혹시 모를 ... 소수점 버리기
        int amountPerPayment = (int)amountPerPaymentDouble;



        mav.addObject("dto",dto); // 크루 정보
        mav.addObject("crewAccountBalanceStr",crewAccountBalanceStr); // 크루 잔액
        mav.addObject("crewAccountBalance",crewAccountBalance); // 크루 잔액
        mav.addObject("memberList",memberList); // 선원 리스트
        mav.addObject("memberAccountMap",memberAccountMap); // 선원들 계좌리스트
        mav.addObject("crewCaptain",crewCaptain); // 선장 정보
        mav.addObject("crewSailingDayCount",crewSailingDayCount); // 항해일수
        mav.addObject("crewNum",crewNum);
        mav.addObject("amountPerPayment",amountPerPayment); // 크루 선원들의 총 납입횟수
        mav.setViewName("crew/crewSetting");

        return mav;
    }




    // green 크루 정보 수정 submit
    @RequestMapping(value="/updateCrewInfo")
    public JSONObject updateCrewInfo(
            @RequestParam("crewNum") int crewNum,
            @RequestParam("crewInfo") String crewInfo,
            @RequestParam("payDate") int payDate,
            @RequestParam("payMoney") int payMoney,
            @RequestParam("goalMoney") int goalMoney
        ) throws Exception {
        CrewDTO crewDTO = crewDetailService.getCrewData(crewNum);
        crewDTO.setCrew_num(crewNum);
        crewDTO.setCrew_info(crewInfo);
        crewDTO.setCrew_paydate(payDate);
        crewDTO.setCrew_paymoney(payMoney);
        crewDTO.setCrew_goal(goalMoney);

        crewSettingService.updateCrewInfo(crewDTO);

        HashMap<String, Object> hash = new HashMap<>();
        CrewDTO newCrewDTO = crewDetailService.getCrewData(crewNum);
        System.out.println("[CrewController - Setting : crewInfo] " + crewNum + " 크루 정보 수정완료");

        hash.put("newCrewInfo",newCrewDTO.getCrew_info());
        hash.put("newPayDate",newCrewDTO.getCrew_paydate());
        hash.put("newPayMoney",newCrewDTO.getCrew_paymoney());
        hash.put("newGoalMoney",newCrewDTO.getCrew_goal());

        JSONObject newCrewjsonObject = new JSONObject(hash);

        return newCrewjsonObject;
    }


// red 크루관리
    // green 크루 일정 관리
    // bold 일정 추가
    @RequestMapping(value="/addCrewSche")
    @ResponseBody
    public void addCrewSche(
            @RequestParam("crewNum") int crewNum,
            @RequestParam("scheTitle") String scheTitle,
            @RequestParam("scheAssort") String scheAssort,
            @RequestParam("scheAllDayTF") String scheAllDayTF,
            @RequestParam("scheStart") String scheStart,
            @RequestParam("scheEnd") String scheEnd
            ) throws Exception {

        String scheAssortCode = "";

        switch (scheAssort) {
            default:
            case "redSche":
                scheAssortCode = "#FF8383";
                break;
            case "greenSche":
                scheAssortCode = "#22B14C";
                break;
            case "yellowSche":
                scheAssortCode = "#FFC90E";
                break;
            case "blueSche":
                scheAssortCode = "#00A5ED";
                break;
            case "graySche":
                scheAssortCode = "#A1A1A1";
                break;
        }

        if(scheAllDayTF==null || scheAllDayTF.isEmpty()) {
            scheAllDayTF = "false";
        }

        int scheNum = crewSettingService.getScheMaxNum()+1;
        ScheduleDTO dto = new ScheduleDTO();
        dto.setSche_num(scheNum);
        dto.setCrew_num(crewNum);
        dto.setSche_title(scheTitle);
        dto.setSche_assort(scheAssortCode);
        dto.setSche_alldayTF(scheAllDayTF);
        dto.setSche_start(scheStart);
        dto.setSche_end(scheEnd);

        crewSettingService.insertCrewSche(dto);
        System.out.println("===================================================");
        System.out.println("[CrewController - Setting : Calendar] 일정 추가완료");
        System.out.println("---------------------------------------------------");
        System.out.println("[" + crewNum + "번 크루 / " + scheNum + "] scheTitle: " + scheTitle);
        System.out.println("startDate: " + scheStart);
        System.out.println("endDate: " + scheEnd);
        System.out.println("===================================================");

    }

    // bold 일정 수정
    @RequestMapping(value="/updateCrewSche")
    @ResponseBody
    public void updateCrewSche(
            @RequestParam("scheNum") int scheNum,
            @RequestParam("scheTitle") String scheTitle,
            @RequestParam("scheAssort") String scheAssort,
            @RequestParam("scheAllDayTF") String scheAllDayTF,
            @RequestParam("scheStart") String scheStart,
            @RequestParam("scheEnd") String scheEnd
    ) throws Exception {

        String scheAssortCode = "";

        switch (scheAssort) {
            default:
            case "redSche":
                scheAssortCode = "#FF8383";
                break;
            case "greenSche":
                scheAssortCode = "#22B14C";
                break;
            case "yellowSche":
                scheAssortCode = "#FFC90E";
                break;
            case "blueSche":
                scheAssortCode = "#00A5ED";
                break;
            case "graySche":
                scheAssortCode = "#A1A1A1";
                break;
        }

        if(!scheAllDayTF.equals("true")) {
            scheAllDayTF = "false";
        } else {
            scheAllDayTF = "true";
        }

        ScheduleDTO dto = new ScheduleDTO();
        dto.setSche_num(scheNum);
        dto.setSche_title(scheTitle);
        dto.setSche_assort(scheAssortCode);
        dto.setSche_alldayTF(scheAllDayTF);
        dto.setSche_start(scheStart);
        dto.setSche_end(scheEnd);

        crewSettingService.updateCrewSche(dto);
        System.out.println("===================================================");
        System.out.println("[CrewController - Setting : Calendar] 일정 수정완료");
        System.out.println("---------------------------------------------------");
        System.out.println("[" + scheNum + "] scheTitle: " + scheTitle);
        System.out.println("startDate: " + scheStart);
        System.out.println("endDate: " + scheEnd);
        System.out.println("===================================================");

    }

    // bold 일정 삭제
    @RequestMapping(value="/deleteCrewSche")
    @ResponseBody
    public void deleteCrewSche(@RequestParam("scheNum") int scheNum) throws Exception {
        crewSettingService.deleteCrewSche(scheNum);

        System.out.println("===================================================");
        System.out.println("[CrewController - Setting : Calendar] 일정 삭제완료");
        System.out.println("---------------------------------------------------");
        System.out.println("scheNum: " + scheNum);
        System.out.println("===================================================");

    }

    // green 항해 중단하기 ( 중단 결정일 데이터 삽입 )
    @RequestMapping(value="/updateDelDate")
    public ModelAndView updateDelDate(@RequestParam("crewNum") int crewNum) throws Exception {

        ModelAndView mav = new ModelAndView();
        crewSettingService.stopSailing(crewNum); // 업데이트 한 후에
        CrewDTO dto = crewDetailService.getCrewData(crewNum);

        System.out.println("[CrewSettingController] " + dto.getCrew_name() + " 크루 항해 중단...");
        System.out.println("[CrewSettingController] " + dto.getCrew_name() + " 항해 중단 페이지로 리디렉트...");

        mav.setViewName("redirect:/crew/setting/sailingStopCrew?crewNum=" + crewNum);
        return mav;
    }

    // 항해를 중단 유예중인 크루에 접속하면 오는 곳
    @RequestMapping(value="/sailingStopCrew")
    public ModelAndView sailingStopCrew(@RequestParam("crewNum") int crewNum) throws Exception {
        ModelAndView mav = new ModelAndView();
        CrewDTO dto = crewDetailService.getCrewData(crewNum); // 크루 정보 불러와서

        if(dto.getCrew_deldate()==null || dto.getCrew_deldate().equals("")) {
            System.out.println("[CrewController] " + crewNum + " - " + dto.getCrew_name() + "으로 이동...");
            mav.setViewName("redirect:/crew?crewNum=" + crewNum);
            return mav;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String delDecideDateStr = dto.getCrew_deldate(); // 항해 중단 결정일자 불러오고
        Date delDecideDate = dateFormat.parse(delDecideDateStr);

        mav.addObject("status","stopSailingCrew");
        mav.addObject("dto",dto);
        mav.addObject("delDecideDateStr",delDecideDateStr);

        mav.setViewName("crew/wrongAccess");
        return mav;
    }

    // 항해 중단 취소
    @RequestMapping(value="/cancelSailingStop")
    public ModelAndView cancelSailingStop(@RequestParam("crewNum") int crewNum) throws Exception {
        ModelAndView mav = new ModelAndView();

        crewSettingService.cancelStopSailing(crewNum);
        CrewDTO dto = crewDetailService.getCrewData(crewNum);

        System.out.println("[CrewSettingController] " + dto.getCrew_name() + " 크루 항해 중단 취소...");
        System.out.println("[CrewSettingController] " + dto.getCrew_name() + " 크루 페이지로 리디렉트...");

        mav.setViewName("redirect:/crew?crewNum="+crewNum);
        return mav;
    }



// red 선원관리
    // green 선원 강퇴하기 submit
    @RequestMapping(value="/memberBan")
    public void memberBan(@RequestParam("crewNum") int crewNum,
                          @RequestParam("email") String email) throws Exception {
        crewSettingService.deleteMember(email, crewNum);
        System.out.println(
                "[CrewController - Setting : memberBan] " + crewNum + "번 크루에서 "
                        + email + " 강퇴 완료");
    }

}
