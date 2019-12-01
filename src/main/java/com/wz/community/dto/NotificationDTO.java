package com.wz.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors
public class NotificationDTO {

    private Long id;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private String typeName;
    private Long gmtCreate;
    private Integer status;
    private Integer type;
    private Long outerid;

}
