package com.example.we_tried.index;

import com.example.we_tried.login.LoginRequest;
import com.example.we_tried.register.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @GetMapping("/")
    public ModelAndView getIndexPage() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("loginRequest", new LoginRequest());
        modelAndView.addObject("registerRequest", new RegisterRequest());
        return modelAndView;
    }

}
