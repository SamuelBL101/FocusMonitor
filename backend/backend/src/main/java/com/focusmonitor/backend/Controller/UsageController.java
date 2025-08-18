package com.focusmonitor.backend.Controller;

import com.focusmonitor.backend.model.Usage;
import com.focusmonitor.backend.services.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
public class UsageController {
   @Autowired
   private UsageService usageService;

   @PostMapping
    public Usage createUsage(@RequestBody Usage usage) {
       Usage usg = usage;
       System.out.println("Received usage: " + usg);
         return usageService.saveUsage(usage);
    }
    @PostMapping("/getUsage/{userId}/{date}")
    public List<Usage> getUsageByUserAndDate(@PathVariable Long userId, @PathVariable LocalDate date) {
        return usageService.getUsageByUserAndDate(userId, date);
    }
}
