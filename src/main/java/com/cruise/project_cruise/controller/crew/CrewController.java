package com.cruise.project_cruise.controller.crew;

import com.cruise.project_cruise.dto.*;
import com.cruise.project_cruise.service.CrewDetailService;
import com.cruise.project_cruise.service.CrewSettingService;
import com.cruise.project_cruise.service.MypageService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping(value="/crew")
@RestController
public class CrewController {

    /**
     * TODO [CrewController] : 만들어야 할 메소드 목록
     * 1. 메인화면 출력
     * 2. 크루관리페이지 출력
     */

    @Autowired
    private CrewDetailService crewDetailService;
    @Autowired
    private CrewSettingService crewSettingService;
    @Autowired
    private MypageService mypageService;


// red 오류페이지
    @RequestMapping("/wrongAccess")
    public ModelAndView wrongAccess() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("crew/wrongAccess");

        return mav;
    }

// red 풀캘린더 데이터 전달 URL
    // - main, setting, mypage에서 공동 사용
    @RequestMapping("/setting/loadCrewSchedule")
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

// red 크루 상세페이지

    // green 크루 상세페이지 출력
    @RequestMapping(value="")
    public ModelAndView crewMain(HttpSession session, HttpServletRequest request) throws Exception {

        // 크루 상세페이지 메인화면
        ModelAndView mav = new ModelAndView();
        
        int crewNum = Integer.parseInt(request.getParameter("crewNum"));
        CrewDTO dto = crewDetailService.getCrewData(crewNum);
        
        
        /**
         * TODO
         * 0. 내가 크루원일 때만 해당 페이지에 들어올 수 있게 하기
         * 1. 크루 소식조회
         * 2. crewNum에 해당하는 크루 기본정보 데이터 - 완료
         * 3. 거래내역 조회
         * 4. 회비내역 조회
         * 5. 납입기능
         * 6. 일정 간편조회
         */

        // bold 0. 크루원만 해당크루 상세페이지에 접속가능하게 처리하기 - 완료
            // 1. 세션에서 크루원의 이메일 받기
            String userEmail = (String)session.getAttribute("email");
            // 2. 선원 테이블에서 이메일이 해당 이메일이고 crewnum이 해당 num인 데이터 찾기
            if(userEmail==null||userEmail.isEmpty()) {
                System.out.println("[CrewController] 로그인하지 않은 사용자가 [" + crewNum + " - " + dto.getCrew_name() + "] 에 접근");
                mav.addObject("status","logout");
                mav.setViewName("crew/wrongAccess");

                return mav;
            }

            if(!crewDetailService.isMember(crewNum,userEmail)) {
                System.out.println("[CrewController] " + crewNum + " - " + dto.getCrew_name() + "에 " + userEmail + " 접근 실패");
                mav.addObject("status","notMember");
                mav.setViewName("crew/wrongAccess");

                return mav;
            }

        // bold 0. 크루 캡틴에게만 크루관리 뜨게 하기 - 완료
            if(crewDetailService.isCaptain(crewNum,userEmail)) {
                System.out.println("[CrewController] " + crewNum + " - " + dto.getCrew_name() + "에 선장 " + userEmail + " 접속");
                mav.addObject("isCaptain","true");
            } else {
                System.out.println("[CrewController] " + crewNum + " - " + dto.getCrew_name() + "에 선원 " + userEmail + " 접속");
                mav.addObject("isCaptain","false");
            }

        // TODO 1. 크루 소식조회

        // bold 2. 크루 기본정보 데이터 - 완료
            String captainName = crewDetailService.getCaptainName(dto.getCaptain_email());

            // 3. 날짜 데이터 ~년, ~월 ~일 형태로 바꾸어주기
            String fullCreatedDate = dto.getCrew_created();
            String[] createdDate = new String[3];

            createdDate[0] = fullCreatedDate.substring(0,4);
            createdDate[1] = fullCreatedDate.substring(5,7);
            createdDate[2] = fullCreatedDate.substring(8,10);

            // 4-1. 오픈뱅킹 대용 테이블에서 계좌 잔액 가지고오기
            int crewAccountBalance = crewDetailService.getAccountBalance(dto.getCrew_accountid());
            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            String crewAccountBalanceStr = decimalFormat.format(crewAccountBalance);

            // 4-2. 목표금액, 달성율
            int achievePer = (int)(((double)crewAccountBalance/(double)dto.getCrew_goal())*100);
                // int 는 정수이기 때문에 나눗셈의 소숫점 결과값을 얻으려면 double로 형변환 해주어야 함.
            String crewGoal = decimalFormat.format(dto.getCrew_goal());

        // TODO 거래내역 조회

        // TODO 회비내역 조회

        // bold 납입기능 양식 출력 - 완료
            // 접속한 유저의 선원 정보 가지고오기
            Map<String,Object> crewUserInfo = crewDetailService.getCrewUserInfo(crewNum,userEmail);

            // 숫자로 표기해야할 부분.. 따로 넘겨주기 (계산해야하니까)
            int userMustPayCount = Integer.parseInt(String.valueOf(crewUserInfo.get("MUST_PAYCOUNT")));
            int userRealPayCount = Integer.parseInt(String.valueOf(crewUserInfo.get("REAL_PAYCOUNT")));
            int userTotalPay = Integer.parseInt(String.valueOf(crewUserInfo.get("TOTAL_PAY")));

            // 접속한 유저의 등록된 계좌 들고오기
            List<MyAccountDTO> userAccountList = mypageService.getAccountList(userEmail);


        // TODO 일정 간편조회

        // 데이터 넘겨주기
            // 크루 기본정보 관련
            mav.addObject("dto",dto);
            mav.addObject("crewNum",crewNum);
            mav.addObject("captainName",captainName);
            mav.addObject("createdDate",createdDate);
            mav.addObject("crewAccountBalance", crewAccountBalanceStr);
            mav.addObject("achievePer", achievePer);
            mav.addObject("crewGoal", crewGoal);

            // 납입기능 폼 데이터
            mav.addObject("crewUserInfo", crewUserInfo);
            mav.addObject("userMustPayCount", userMustPayCount);
            mav.addObject("userRealPayCount", userRealPayCount);
//            mav.addObject("userTotalPay", userTotalPay);
            mav.addObject("userAccountList", userAccountList);

        mav.setViewName("crew/crewmain");

        return mav;
    }

    // green 납입 처리하기
    @RequestMapping(value="/paymentFee")
    public void paymentFee(@RequestParam("crewNum")int crewNum, @RequestParam("userEmail") String userEmail,
                           @RequestParam("payment") int payment, @RequestParam("payCount") int payCount) throws Exception {

        // OpenBankDTO를 업데이트 해야합니다.
        // crewNum이랑 userEmail, 납입횟수를 받아서
        // 그 행에 해당하는 실제납입횟수에 +n 합시다
        crewDetailService.updateCrewMemberPayment(crewNum,userEmail,payment,payCount);
        System.out.println("[CrewController] [" + crewNum + "] " + userEmail + "이 " + payCount + "회 (" + payment + "원) 납입");
    }


    // green 회원 탈퇴 진행
    @RequestMapping(value="/crewExitOK")
    public ModelAndView crewExit() throws Exception {
        ModelAndView mav = new ModelAndView();

        // FIXME 현재 로그인한 멤버의 이메일, CrewNum 가져오기
        // FIXME 현재 로그인한 멤버가

        String cmemEmail = ""; // 가상 이메일
        int crewNum = 0; // 가상 크루넘버

        crewDetailService.deleteCrewMember(cmemEmail,crewNum);
        mav.setViewName("redirect:/");

        return mav;
    }


// red 크루 관리

    // green 크루 관리페이지 메인
    @RequestMapping(value="/setting")
    public ModelAndView crewSetting(HttpSession session, HttpServletRequest request) throws Exception {
        ModelAndView mav = new ModelAndView();
        int crewNum = Integer.parseInt(request.getParameter("crewNum"));
        CrewDTO dto = crewDetailService.getCrewData(crewNum); // 크루 정보
        String userEmail = (String) session.getAttribute("email");

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

        // bold 크루 선원탈퇴 페이지
            // 선원들 목록을 리스트로 뽑아와야해.
        List<Map<String,String>> memberList = crewSettingService.getCrewMemberList(crewNum);

        mav.addObject("dto",dto); // 크루 정보
        mav.addObject("memberList",memberList); // 선원 리스트
        mav.addObject("crewCaptain",crewCaptain); // 선장 정보
        mav.addObject("crewSailingDayCount",crewSailingDayCount); // 항해일수
        mav.addObject("crewNum",crewNum);
        mav.setViewName("crew/crewSetting");

        return mav;
    }

    // green 선원 강퇴하기 submit
    @RequestMapping(value="/setting/memberBan")
    public void memberBan(@RequestParam("crewNum") int crewNum,
                                     @RequestParam("email") String email) throws Exception {
        crewSettingService.deleteMember(email, crewNum);
        System.out.println(
                "[CrewController - Setting : memberBan] " + crewNum + "번 크루에서 "
                        + email + " 강퇴 완료");
    }

    // green 크루 정보 수정 submit
    @RequestMapping(value="/setting/updateCrewInfo")
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


    // green 크루 일정 관리
        // bold 일정 추가
        @RequestMapping(value="/setting/addCrewSche")
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
        @RequestMapping(value="/setting/updateCrewSche")
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
        @RequestMapping(value="/setting/deleteCrewSche")
        @ResponseBody
        public void deleteCrewSche(@RequestParam("scheNum") int scheNum) throws Exception {
            crewSettingService.deleteCrewSche(scheNum);

            System.out.println("===================================================");
            System.out.println("[CrewController - Setting : Calendar] 일정 삭제완료");
            System.out.println("---------------------------------------------------");
            System.out.println("scheNum: " + scheNum);
            System.out.println("===================================================");

        }

}
