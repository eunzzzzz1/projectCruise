package com.cruise.project_cruise.controller;


import com.cruise.project_cruise.dto.CrewDTO;
import com.cruise.project_cruise.dto.CrewMemberDTO;
import com.cruise.project_cruise.dto.MyAccountDTO;
import com.cruise.project_cruise.service.MypageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class MypageController {

    @Autowired
    private MypageService mypageService;


    @GetMapping("/mypage/mypageZero")
    public ModelAndView zero() throws Exception {

        ModelAndView mav = new ModelAndView();

        mav.setViewName("mypage/mypageZero");

        return mav;

    }

    @GetMapping("/mypage/mypage_all")
    public ModelAndView all(HttpServletRequest request) throws  Exception {

        String email = "hchdbsgk@naver.com";

        List<CrewDTO> crewLists = mypageService.getCrews(email);

        ModelAndView mav = new ModelAndView();

        mav.setViewName("mypage/mypage_all");

        mav.addObject("crewLists",crewLists);

        return mav;
    }

    @PostMapping("/mypage/mypage_all")
    public ModelAndView delCrew(HttpServletRequest request) throws  Exception {

        String email = "hchdbsgk@naver.com";
        int crewNum = Integer.parseInt(request.getParameter("crewNum"));

        mypageService.deleteCrew(email,crewNum);

        ModelAndView mav = new ModelAndView();

        mav.setViewName("redirect:/mypage/mypage_all");

        return mav;


    }

}
