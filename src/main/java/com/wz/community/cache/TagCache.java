package com.wz.community.cache;

import com.wz.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {


    public static List<TagDTO> getTag(){
        List<TagDTO> tags = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTag(Arrays.asList("javascript","php","css","html","html5","java","node.js","python","c++","c","golang","objective-c","typescript","shell","c#","swift","sass","bash","ruby","less","asp.net","lua","scala","coffeescript","actionscript","rust","erlang","perl"));
        tags.add(program);
        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTag(Arrays.asList("laravel","spring","express","flask","django","yii","koa","struts","tornado","ruby-on-rails"));
        tags.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTag(Arrays.asList("linux","nginx","docker","缓存","负载均衡","apache","ubuntu","centos","tomcat","unix","hadoop","window-server"));
        tags.add(server);

        TagDTO dbAndCache = new TagDTO();
        dbAndCache.setCategoryName("数据库和缓存");
        dbAndCache.setTag(Arrays.asList("mysql","redis","mongodb","sql","oracle","nosql","memcached","sqlserver","postgresql","sqlite"));
        tags.add(dbAndCache);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTag(Arrays.asList("git","github","visual-studio-code","vim","sublime-text","xcode","intellij-idea","eclipse","maven","ide","svn","visual-studio","atom","emacs","textmate","hg"));
        tags.add(tool);

        TagDTO systemTool = new TagDTO();
        systemTool.setCategoryName("系统设备");
        systemTool.setTag(Arrays.asList("android","ios","chrome","windows","iphone","firefox","internet-explorer","safari","ipad","opera","apple-watch"));
        tags.add(systemTool);

        TagDTO els = new TagDTO();
        els.setCategoryName("其他");
        els.setTag(Arrays.asList("html5","react.js","搜索引擎","virtualenv","lucene"));
        tags.add(els);

        return tags;
    }

    public static String filterInvalid(String tag) {
        //前台输入的标签数组
        String[] tags = StringUtils.split(tag, "，");
        //拿到所有标签
        List<TagDTO> tagDTOS = getTag();
        //所有标签组成的集合
        List<String> allTag = tagDTOS.stream().flatMap(tagDTO -> tagDTO.getTag().stream()).collect(Collectors.toList());
        //tags数组中非法的标签组成的集合
        String invalid = Arrays.stream(tags).filter(t -> !allTag.contains(t)).collect(Collectors.joining("，"));
        return invalid;
    }
}
