package com.wz.community.controller;


import com.wz.community.cache.TagCache;
import com.wz.community.dto.QuestionDTO;
import com.wz.community.dto.TagDTO;
import com.wz.community.model.Question;
import com.wz.community.model.User;
import com.wz.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id, Model model) {
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        model.addAttribute("id", id);
        model.addAttribute("title", questionDTO.getTitle());
        model.addAttribute("description", questionDTO.getDescription());
        model.addAttribute("tag", questionDTO.getTag());
        model.addAttribute("tags", TagCache.getTag());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags",TagCache.getTag());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(Question question,
                            HttpServletRequest request,
                            Model model) {
        model.addAttribute("question", question);
        List<TagDTO> tagDTOList = TagCache.getTag();
        model.addAttribute("tags", tagDTOList);
        String title = question.getTitle();
        if (title == null || title == "") {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        String description = question.getDescription();
        if (description == null || description == "") {
            model.addAttribute("error", "问题补充不能为空");
            return "publish";
        }
        String tag = question.getTag();
        if (tag == null || tag == "") {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }
        //判断tag是否含有非法字段
        String invalid = TagCache.filterInvalid(tag);
        if(StringUtils.isNotBlank(invalid)){
            //如果非法字符串不为空，则抛异常
            model.addAttribute("error", "输入非法标签"+invalid);
            return "publish";
        }
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }
        question.setCreator(user.getId());
        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}
