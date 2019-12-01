package com.wz.community.controller;

import com.wz.community.dto.NotificationDTO;
import com.wz.community.enums.NotificationTypeEnum;
import com.wz.community.model.User;
import com.wz.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {


    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,
                          HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        //如果用户未登录且无法自动登录，重定向至idnex.html
        if (user == null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id,user);
        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
            || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else{
            return "redirect:/";
        }
    }
}
