package com.wz.community.service;

import com.wz.community.dto.PaginationDTO;
import com.wz.community.dto.QuestionDTO;
import com.wz.community.enums.CustomizeErrorCode;
import com.wz.community.exception.CustomizeException;
import com.wz.community.mapper.QuestionExtMapper;
import com.wz.community.mapper.QuestionMapper;
import com.wz.community.mapper.UserMapper;
import com.wz.community.model.Question;
import com.wz.community.model.QuestionExample;
import com.wz.community.model.User;
import com.wz.community.model.UserExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    public PaginationDTO list(Long userId, Integer page, Integer pageSize) {
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        int totalCount = (int) questionMapper.countByExample(questionExample);
        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);
        int offset = page == 0 ? 0 : (page - 1) * pageSize;
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, pageSize));
        List<QuestionDTO> questionDTOs = load(questions);
        paginationDTO.setData(questionDTOs);
        return paginationDTO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PaginationDTO<QuestionDTO> findAll(Integer page, Integer pageSize) {
        PaginationDTO paginationDTO = new PaginationDTO<>();
        int totalCount = (int) questionMapper.countByExample(new QuestionExample());
        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);
        int offset = page == 0 ? 0 : (page - 1) * pageSize;
        QuestionExample example = new QuestionExample();
        example.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, pageSize));
        List<QuestionDTO> questionDTOs = load(questions);
        paginationDTO.setData(questionDTOs);
        return paginationDTO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public QuestionDTO getQuestionById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setUser(user);
        BeanUtils.copyProperties(question, questionDTO);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //新增问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        } else {
            //修改问题
            question.setGmtModified(System.currentTimeMillis());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(question, questionExample);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    private List<QuestionDTO> load(List<Question> questions) {
        if (questions.size() == 0) {
            return new ArrayList<>();
        }
        //拿到每个问题的作者
        List<Long> userIds = questions.stream().map(question -> question.getCreator()).distinct().collect(Collectors.toList());
        //查询所有用户
        UserExample example = new UserExample();
        example.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(example);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //封装QuestionDTO
        List<QuestionDTO> questionDTOs = questions.stream().map(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(userMap.get(question.getCreator()));
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOs;
    }

    //阅读数的increase
    public void increaseViewCount(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.increaseViewCount(question);
    }

    //拿到相关问题的集合
    public List<QuestionDTO> selectRelated(QuestionDTO questionDTO) {
        //先判断当前问题的标签是否为空
        if (StringUtils.isBlank(questionDTO.getTag())) {
            return new ArrayList<>();
        }
        //将查询条件处理
        String[] tags = StringUtils.split(questionDTO.getTag(), "，");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));

        Question questionWithIdTag = new Question();
        questionWithIdTag.setId(questionDTO.getId());
        questionWithIdTag.setTag(regexpTag);
        //相关问题集合
        List<Question> questions = questionExtMapper.selectByTag(questionWithIdTag);
        //这里需要判断一下相关问题集合是否为空
        if (questions == null || questions.size() == 0) {
            return new ArrayList<>();
        }

        //相关问题的作者ids
        List<Long> creators = questions.stream().map(question -> question.getCreator()).distinct().collect(Collectors.toList());
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(creators);
        //查询作者
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //question -> questionDTO
        List<QuestionDTO> questionDTOList = questions.stream().map(question -> {
            QuestionDTO questiondto = new QuestionDTO();
            BeanUtils.copyProperties(question, questiondto);
            questiondto.setUser(userMap.get(question.getId()));
            return questiondto;
        }).collect(Collectors.toList());
        return questionDTOList;
    }
}
