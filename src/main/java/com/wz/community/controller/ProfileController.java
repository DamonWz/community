package com.wz.community.controller;

import com.wz.community.dto.PaginationDTO;
import com.wz.community.model.User;
import com.wz.community.service.NotificationService;
import com.wz.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                          @RequestParam(name = "pageSize", required = false, defaultValue = "4") Integer pageSize) {
        User user = (User) request.getSession().getAttribute("user");
        //如果用户未登录且无法自动登录，重定向至idnex.html
        if (user == null) {
            return "redirect:/";
        }
        PaginationDTO paginationDTO = null;
        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            paginationDTO = questionService.list(user.getId(), page, pageSize);
        } else if ("replies".equals(action)) {
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            paginationDTO = notificationService.list(user.getId(), page, pageSize);
        }
        model.addAttribute("pagination", paginationDTO);
        return "profile";
    }
}
