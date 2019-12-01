package com.wz.community.controller;

import com.wz.community.dto.FileDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图片上传的解决方案
 * 1、上传之后创建一个temp目录或者一个固定的目录，将文件放在目录中，
 *      再使用nginx或者tomcat直接映射出一个地址往前端返回。
 *      缺点：非常依赖主机迁移，当服务器从远端的云服务器上迁到
 *           本地服务器时，需要将所有的静态资源迁移
 * 2、将图片转换成二进制文件存到数据库里面，返回到页面时再进行序列化
 *      缺点：成本高
 * 3、使用云服务器
 */


@RestController
public class FileController {

    @RequestMapping("/file/upload")
    public FileDTO upload() {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setMessage("上传成功");
        fileDTO.setUrl("/static/img/damon.jpg");
        return fileDTO;
    }

}
