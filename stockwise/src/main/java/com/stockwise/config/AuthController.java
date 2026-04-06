package com.stockwise.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles authentication page routing.
 */
@Controller
public class AuthController {

    /**
     * Displays the custom login page.
     *
     * @return login view
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}