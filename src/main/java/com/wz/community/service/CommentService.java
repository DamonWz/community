package com.wz.community.service;

import com.wz.community.dto.CommentCreateDTO;
import com.wz.community.dto.CommentDTO;
import com.wz.community.enums.CommentTypeEnum;
import com.wz.community.enums.CustomizeErrorCode;
import com.wz.community.enums.NotificationStatusEnum;
import com.wz.community.enums.NotificationTypeEnum;
import com.wz.community.exception.CustomizeException;
import com.wz.community.mapper.*;
import com.wz.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentExtMapper commentExtMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    //添加评论
    public void insert(CommentCreateDTO commentCreateDTO, User user) {
        if (commentCreateDTO.getParentId() == null || commentCreateDTO.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        //如果评论类型为null，或者非1或2都是错误的
        if (commentCreateDTO.getType() == null || !CommentTypeEnum.isExist(commentCreateDTO.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        //这时创建出来评论对象
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setCommentatorId(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setLikeCount(0);
        comment.setCommentCount(0);
        if (commentCreateDTO.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            //1 先知道是回复了哪条评论
            Comment parentComment = commentMapper.selectByPrimaryKey(commentCreateDTO.getParentId());
            if (parentComment == null) {
                //2 如果被回复的评论不存在了
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //回复的哪个问题
            Question question = questionMapper.selectByPrimaryKey(parentComment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //3 进行添加评论入库
            commentMapper.insert(comment);
            parentComment.setCommentCount(1);
            commentExtMapper.increaseCommentCount(parentComment);
            //通知
            createNotify(comment, user.getName(), NotificationTypeEnum.REPLY_COMMENT, parentComment.getCommentatorId(), question.getTitle(), question.getId());
        } else {
            //回复问题
            //1 先知道是回复的哪个问题
            Question question = questionMapper.selectByPrimaryKey(commentCreateDTO.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //进行回复入库
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.increaseCommentCount(question);
            //通知
            createNotify(comment, user.getName(), NotificationTypeEnum.REPLY_QUESTION, question.getCreator(), question.getTitle(), question.getId());
        }
    }

    /**
     *
     * @param comment   评论对象
     * @param notifierName   通知者名称
     * @param replyQuestion   通知类型
     * @param receiver    接收通知者
     * @param outerTitle    通知的标题
     * @param outerId    通知的id
     */
    private void createNotify(Comment comment, String notifierName, NotificationTypeEnum replyQuestion, Long receiver, String outerTitle, Long outerId) {
        if (receiver == comment.getCommentatorId()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(replyQuestion.getType());
        notification.setNotifier(comment.getCommentatorId());
        notification.setReceiver(receiver);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setOuterid(outerId);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    //查询全部评论
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CommentDTO> listByTargetId(Long parentId, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(parentId)
                .andTypeEqualTo(type.getType());
        //直接按照创建时间排序  该字符串是可以直接拼接到SQL语句后面的
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);
        return load(comments);
    }

    //model-->DTO
    private List<CommentDTO> load(List<Comment> comments) {
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //拿到评论人的id集合  去重
        Stream<Comment> stream = comments.stream();
        List<Long> userIds = stream.map(comment -> comment.getCommentatorId()).distinct().collect(Collectors.toList());
        //查询用户
        UserExample example = new UserExample();
        example.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(example);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //进行判断，封装CommentDTO
        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentatorId()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOs;
    }
}
