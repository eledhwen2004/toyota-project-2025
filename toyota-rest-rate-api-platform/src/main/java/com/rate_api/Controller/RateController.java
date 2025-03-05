package com.rate_api.Controller;

import com.rate_api.Dto.RateDto;
import com.rate_api.Service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateController {

    @Autowired
    RateService rateService;

    @GetMapping("/rates/{rate_name}")
    public RateDto getRateByName(@PathVariable String rate_name){
        return rateService.getRateByName(rate_name);
    }

}
