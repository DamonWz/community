package com.wz.community.advice;

import com.alibaba.fastjson.JSON;
import com.wz.community.dto.ResultDTO;
import com.wz.community.enums.CustomizeErrorCode;
import com.wz.community.exception.CustomizeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request,
                        HttpServletResponse response,
                        Throwable ex) {

        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO = null;
            //返回json
            if (ex instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            response.setContentType("application/json");
            response.setStatus(200);
            response.setCharacterEncoding("UTF-8");
            try {
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
            }
            return null;
        } else {
            //跳转错误页面
            ModelAndView mv = new ModelAndView();
            if (ex instanceof CustomizeException) {
                mv.addObject("message", ex.getMessage());
            } else {
                mv.addObject("message", CustomizeErrorCode.SYSTEM_ERROR.getMessage());
            }
            mv.setViewName("error");
            return mv;
        }
    }
}
