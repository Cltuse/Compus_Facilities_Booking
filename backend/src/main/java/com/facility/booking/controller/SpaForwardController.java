package com.facility.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({
            "/",
            "/login",
            "/register",
            "/admin/{path:[^\\.]*}",
            "/admin/**/{path:[^\\.]*}",
            "/user/{path:[^\\.]*}",
            "/user/**/{path:[^\\.]*}",
            "/maintainer/{path:[^\\.]*}",
            "/maintainer/**/{path:[^\\.]*}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
