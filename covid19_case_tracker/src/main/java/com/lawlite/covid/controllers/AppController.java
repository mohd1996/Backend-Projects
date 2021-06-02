
package com.lawlite.covid.controllers;

import java.util.List;

import com.lawlite.covid.models.LocationStats;
import com.lawlite.covid.services.Impl.COVIDDataServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AppController
 */
@Controller
public class AppController {

    @Autowired
    COVIDDataServiceImpl service;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = service.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        return "home";
    }

}