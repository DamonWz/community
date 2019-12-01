package com.wz.community.mapper;

import com.wz.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {

    //增加阅读量
    int increaseViewCount(Question record);
    //增加评论数
    int increaseCommentCount(Question record);
    //根据标签进行相关度查询
    List<Question> selectByTag(Question question);

}