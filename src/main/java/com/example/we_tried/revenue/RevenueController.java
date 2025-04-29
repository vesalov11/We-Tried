package com.example.we_tried.revenue;

import com.example.we_tried.revenue.service.RevenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/revenue")
    public String getRevenuePage(@RequestParam(defaultValue = "daily") String period, Model model) {
        List<RevenueResponse> revenueList = revenueService.getRevenueByPeriod(period);
        BigDecimal totalRevenue = revenueService.calculateTotalRevenue(revenueList);

        model.addAttribute("selectedPeriod", period);
        model.addAttribute("revenueList", revenueList);
        model.addAttribute("totalRevenue", totalRevenue);
        return "revenue";
    }

    @GetMapping("/revenue/daily")
    public String getDailyRevenue(Model model) {
        List<RevenueResponse> revenueList = revenueService.getRevenueByPeriod("daily");

        model.addAttribute("revenueList", revenueList);
        model.addAttribute("period", "daily");
        model.addAttribute("totalRevenue", revenueList.get(0).getRevenue());

        return "revenue";
    }

}

