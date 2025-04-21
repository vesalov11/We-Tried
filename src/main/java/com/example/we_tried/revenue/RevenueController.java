package com.example.we_tried.revenue;

import com.example.we_tried.revenue.service.RevenueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/revenue")
    public ModelAndView showRevenue(@RequestParam(defaultValue = "daily") String period) {
        List<RevenueResponse> revenueList = revenueService.getRevenueByPeriod(period);

        ModelAndView modelAndView = new ModelAndView("revenue");
        modelAndView.addObject("revenueList", revenueList);
        modelAndView.addObject("selectedPeriod", period);

        return modelAndView;
    }
}

