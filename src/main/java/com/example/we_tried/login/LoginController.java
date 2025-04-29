package com.example.we_tried.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        mv.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            mv.addObject("errorMessage", "Incorrect username or password!");
        }

        return mv;
    }
}