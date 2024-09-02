package com.ekhata.controller;

import com.ekhata.service.EkhataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EkhataController {

    public EkhataController(EkhataService ekhataService) {
        this.ekhataService = ekhataService;
    }

    private final EkhataService ekhataService;

    @GetMapping(value = "compare/{deed_number}/{ec_number}")
    public String checkEcDetails(@PathVariable("deed_number") String deed_number, @PathVariable("ec_number") String ec_number) {
        return ekhataService.doIt(deed_number, ec_number);
    }
}
