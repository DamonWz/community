package com.wz.community.interceptor;

import com.wz.community.mapper.UserMapper;
import com.wz.community.model.User;
import com.wz.community.model.UserExample;
import com.wz.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从session中取user，查看用户是否已经登录
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            //如果用户没有登录，查看用户请求是否携带令牌并验证
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length != 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        //请求中携带了令牌 token
                        String token = cookie.getValue();
                        UserExample userExample = new UserExample();
                        userExample.createCriteria().andTokenEqualTo(token);
                        List<User> users = userMapper.selectByExample(userExample);
                        if (users.size() != 0) {
                            //自动登录成功，存入session
                            User userPassed = users.get(0);
                            session.setAttribute("user", userPassed);
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Long unreadCount = notificationService.unreadCount(user.getId());
            session.setAttribute("unreadCount", unreadCount);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
