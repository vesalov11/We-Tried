package com.example.we_tried.config;

import com.example.we_tried.security.AuthenticationMetaData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.management.relation.Relation;
import java.io.IOException;
import java.util.UUID;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticationMetaData userDetails) {

            if (userDetails.getRole().name().equals("DELIVERER")) {
                UUID delivererId = userDetails.getId();
                response.sendRedirect("/all-orders");
            } else {
                response.sendRedirect("/restaurants");
            }
        } else {
            response.sendRedirect("/restaurants");
        }

    }

}
