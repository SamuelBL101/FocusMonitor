package com.focusmonitor.backend.Controller;

import com.focusmonitor.backend.model.AppUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UssageController {
   @Autowired
    AppUsage appUsage;

}
