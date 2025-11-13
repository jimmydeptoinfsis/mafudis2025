package com.app.thym.ddejim.mafudis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Devuelve el nombre de la plantilla HTML: login.html
    }
}