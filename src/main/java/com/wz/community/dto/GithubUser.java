package com.wz.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class GithubUser {

    private String name;
    private Long id;
    private String bio;//描述
    private String avatarUrl;

}
