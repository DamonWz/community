package com.wz.community.controller;

import com.wz.community.dto.PaginationDTO;
import com.wz.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {


    @Autowired
    private QuestionService questionService;

    @GetMapping("/greating")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greating";
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                        @RequestParam(name = "pageSize", required = false, defaultValue = "4") Integer pageSize) {

        //将最新问题展示在主页
        PaginationDTO pagination = questionService.findAll(page, pageSize);
        model.addAttribute("pagination", pagination);
        return "index";
    }
}