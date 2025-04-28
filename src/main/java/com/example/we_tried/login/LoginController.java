package com.example.we_tried.login;

import com.example.we_tried.user.model.User;
import com.example.we_tried.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
