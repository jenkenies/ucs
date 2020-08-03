package com.utstar.ucs.req;

import lombok.Data;

import java.util.List;

@Data
public class BookmarkDTO {
    private String userid; //用户id
    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String mc2; //媒资编码2 连续剧类型，填连续剧剧头编码 电影类型，不填
    private String st = "1";//状态st 1未同步(default) 2 已清空
    List<ModeDTO> modept;
}
