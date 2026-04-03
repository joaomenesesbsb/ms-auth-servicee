package com.meneses.auth.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminColtroller {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Painel de administração";
    }
}
