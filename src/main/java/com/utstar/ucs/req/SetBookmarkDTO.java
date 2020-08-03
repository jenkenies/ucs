package com.utstar.ucs.req;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
public class SetBookmarkDTO implements Serializable {

    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String mc2; //媒资编码2 连续剧类型，填连续剧剧头编码 电影类型，不填
    private String pt; //记录观影时间HH24MISS 最大支持24小时收视书签
    private String createtime;
    private String st = "1";//状态st 1未同步(default) 2 已清空
}
