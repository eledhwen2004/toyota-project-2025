package com.rate_api.Controller;

import com.rate_api.Dto.RateDto;
import com.rate_api.Service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes endpoints related to currency rate data.
 * <p>
 * All endpoints in this controller are prefixed with {@code /api}.
 */
@RestController
@RequestMapping("/api")
public class RateController {

    @Autowired
    RateService rateService;

    /**
     * Retrieves the current exchange rate information for the given rate name.
     *
     * @param rate_name the name of the rate (e.g., "USDTRY")
     * @return a {@link RateDto} object containing the rate's details
     */
    @GetMapping("/rates/{rate_name}")
    public RateDto getRateByName(@PathVariable String rate_name){
        System.out.println("Rate request has come for : " + rate_name );
        return rateService.getRateByName(rate_name);
    }
}
