package com.example.we_tried.index;

import com.example.we_tried.login.LoginRequest;
import com.example.we_tried.register.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @GetMapping("/")
    public String getIndexPage() {

        return "index";
    }

}
