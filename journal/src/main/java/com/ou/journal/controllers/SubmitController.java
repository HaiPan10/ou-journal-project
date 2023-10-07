package com.ou.journal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// test
@Controller
public class SubmitController {

    @GetMapping({"/test/submit", "/test/submit/step1"})
    public String submitPage() {
        return "client/submitManuscript/step1";
    }

    @GetMapping({"/test/submit/step2"})
    public String submitPageStep1() {
        return "client/submitManuscript/step2";
    }
}
