package com.wz.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

//dto数据传输模型,介于网络与网络之间
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AccessTokenDTO {

    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;


}
