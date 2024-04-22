package com.cruise.project_cruise.controller;

import com.cruise.project_cruise.service.TemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@RestController
public class TemplateController {

    //안녕?
    //또 안녕?

    @Resource
    private TemplateService templateService;

    @RequestMapping(value="/template")
    public ModelAndView template() throws Exception {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("template");

        return mav;
    }

}
