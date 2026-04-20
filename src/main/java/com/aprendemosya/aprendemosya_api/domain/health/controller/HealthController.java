package com.aprendemosya.aprendemosya_api.domain.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home (){
        return "AprendemosYa API activa";
    }
    @GetMapping("/api/test")
    public String test (){
        return "API funcionando correctamente";
    }
}
