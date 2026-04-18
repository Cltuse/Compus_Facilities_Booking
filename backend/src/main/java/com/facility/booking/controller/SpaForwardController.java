package com.facility.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({
            "/",
            "/login",
            "/register",
            "/admin",
            "/admin/{path:[^\\.]*}",
            "/user",
            "/user/{path:[^\\.]*}",
            "/user/facility/{id:[^\\.]*}",
            "/maintainer",
            "/maintainer/{path:[^\\.]*}",
    })
    public String forward() {
        return "forward:/index.html";
    }
}
