package com.wz.community.dto;


import com.wz.community.enums.CustomizeErrorCode;
import com.wz.community.exception.CustomizeException;
import lombok.Data;

@Data
public class ResultDTO<T> {

    private Integer code;//错误码
    private String message;//错误提示信息
    private T data;//返回一个json对象

    //出错
    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {
        return new ResultDTO(errorCode.getCode(), errorCode.getMessage());
    }

    //登录成功
    public static ResultDTO successOf() {
        return new ResultDTO(200, "请求成功");
    }

    //返回json
    public static ResultDTO errorOf(CustomizeException customizeException) {
        return new ResultDTO(customizeException.getCode(), customizeException.getMessage());
    }

    public static <T> ResultDTO successOf(T t) {
        return new ResultDTO(200,"请求成功",t);
    }

    public ResultDTO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultDTO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
