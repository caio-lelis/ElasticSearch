package com.pdpj.monitorador.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class RootController {


    @GetMapping("/")
    public String home(){
        return "O monitorador de logs está funcionando ...";
    }
    
}
