package com.wz.community.enums;

import com.wz.community.exception.ICustomizeErrorCode;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    /*
            2xxx 系统问题
     */
    QUESTION_NOT_FOUND(2001, "你找的问题不在了，要不要看看别的？"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登录，请登录后重试"),
    SYSTEM_ERROR(2004,"不好意思！服务器冒烟了！"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论不存在了，看点别的？"),
    COMMENT_IS_EMPTY(2007,"评论不能为空"),
    READ_NOTIFICATION_FAIL(2008,"你这是读别人的消息呢？"),
    NOTIFICATION_NOT_FOUND(2009,"通知不存在了，看看别的吧！")
    ;

    private Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
