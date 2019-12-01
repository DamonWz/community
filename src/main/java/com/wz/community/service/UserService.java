package com.wz.community.service;

import com.wz.community.dto.GithubUser;
import com.wz.community.mapper.UserMapper;
import com.wz.community.model.User;
import com.wz.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User createOrUpdate(GithubUser githubUser) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(String.valueOf(githubUser.getId()));//githubUser的id即为user的accountId
        //查询数据库中是否已经有当前申请登录用户的记录
        List<User> users = userMapper.selectByExample(userExample);
        //无论创建还是更新，都是要对这几个属性赋值的，放外面
        User user = new User();
        user.setName(githubUser.getName());
        user.setBio(githubUser.getBio());
        user.setAvatarUrl(githubUser.getAvatarUrl());
        if (users.size() == 0) {
            //第一次创建需要setAccountId和token,setGmtCreate
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setToken(UUID.randomUUID().toString());
            user.setGmtCreate(System.currentTimeMillis());
            userMapper.insert(user);
        } else {
            //更新用户
            User dbUser = users.get(0);//更改需要使用用户的id
            user.setGmtModified(System.currentTimeMillis());
            userExample.createCriteria().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(user, userExample);
        }
        //这里需要返回用户的最新信息
        List<User> newUsers = userMapper.selectByExample(userExample);
        return newUsers.get(0);
    }
}
