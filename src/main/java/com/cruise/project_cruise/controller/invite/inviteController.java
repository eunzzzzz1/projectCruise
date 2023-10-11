package com.cruise.project_cruise.controller.invite;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class inviteController {

    @GetMapping("/index")
    public String index(HttpSession session,Model model) throws Exception{

        //모임을 만들고 초대 버튼을 누르는 순간 그에 해당하는 모임의 정보가 세션에 들어가고
        //그 세션 정보를 토대로 초대받는 사용자에게 넘겨줄거임
        //아래 코드는 임의로 세션에 정보가 들어왔다고 가정
        //근데 우리 데이터베이스는 crew_num이 Pk이기 때문에 pk도 받아오고
        //사용자에게 보여줄때 1에서 초대했습니다라고 보여줄수 없기때문에
        //pk에 해당하는 crew_name도 넣어놓고 사용자에게 보여줄것임



//        session.setAttribute("group","앙큼불여우단");
//        session.setAttribute("num","1");

        model.addAttribute("group","앙큼불여우단");
        model.addAttribute("num","1");



        return "index";

    }

}
