package com.wz.community.service;

import com.wz.community.dto.NotificationDTO;
import com.wz.community.dto.PaginationDTO;
import com.wz.community.enums.CustomizeErrorCode;
import com.wz.community.enums.NotificationStatusEnum;
import com.wz.community.enums.NotificationTypeEnum;
import com.wz.community.exception.CustomizeException;
import com.wz.community.mapper.NotificationMapper;
import com.wz.community.mapper.UserMapper;
import com.wz.community.model.Notification;
import com.wz.community.model.NotificationExample;
import com.wz.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    public PaginationDTO list(Long id, Integer page, Integer pageSize) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(id);
        int totalCount = (int) notificationMapper.countByExample(notificationExample);
        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);
        int offset = page == 0 ? 0 : (page - 1) * pageSize;
        //分页查询
        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, pageSize));
        //转换为NotificationDTO对象
        List<NotificationDTO> notificationDTOS = load(notifications);
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    private List<NotificationDTO> load(List<Notification> notifications) {
        ArrayList<NotificationDTO> notificationDTOS = new ArrayList<>();
        if (notifications == null) {
            return notificationDTOS;
        }
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        return notificationDTOS;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Long unreadCount(Long receiver) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(receiver)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
