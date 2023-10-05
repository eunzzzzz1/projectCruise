package com.cruise.project_cruise.controller.user;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/user")
@Controller
public class MessageController {

    @GetMapping("/messages")
        public String myMessages() throws Exception{
            return "user/message";

    }
}
