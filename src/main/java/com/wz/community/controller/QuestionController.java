package com.wz.community.controller;

import com.wz.community.dto.CommentDTO;
import com.wz.community.dto.QuestionDTO;
import com.wz.community.enums.CommentTypeEnum;
import com.wz.community.service.CommentService;
import com.wz.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model) {
        //拿到要查看的问题DTO
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        model.addAttribute("question", questionDTO);
        //这时需要累加该问题的viewCount
        questionService.increaseViewCount(id);
        //拿到右侧相关问题的集合
        List<QuestionDTO> questionDTOS = questionService.selectRelated(questionDTO);
        model.addAttribute("questionsRelated",questionDTOS);
        //拿到该问题的评论集合
        List<CommentDTO> commentDTOs = commentService.listByTargetId(questionDTO.getId(), CommentTypeEnum.QUESTION);
        model.addAttribute("comments",commentDTOs);
        return "question";
    }
}
