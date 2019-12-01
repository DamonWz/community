package com.wz.community.mapper;

import com.wz.community.model.Comment;

public interface CommentExtMapper {
    int increaseCommentCount(Comment comment);
}