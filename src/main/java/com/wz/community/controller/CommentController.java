package com.wz.community.controller;


import com.wz.community.dto.CommentCreateDTO;
import com.wz.community.dto.CommentDTO;
import com.wz.community.dto.ResultDTO;
import com.wz.community.enums.CommentTypeEnum;
import com.wz.community.enums.CustomizeErrorCode;
import com.wz.community.model.User;
import com.wz.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    //创建回复
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        //查看当前用户是否登录
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        //判断评论内容是否为空
        if(commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        commentService.insert(commentCreateDTO,user);
        return ResultDTO.successOf();
    }

    //查看问题的下属评论
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comment(@PathVariable(name = "id")Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.successOf(commentDTOS);
    }

}
