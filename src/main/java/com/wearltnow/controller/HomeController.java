package com.wearltnow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping
    public String home() {
        return "Welcome to DuAnTotNghiep!, We are Group 6, this is our api website!! v3";
    }
}
