package com.utstar.ucs.req;

import java.io.Serializable;

import lombok.Data;
@Data
public class SetHotDTO implements Serializable {
    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
	private String currentnum;//第几集
    private String createtime;
    private String st = "1";//状态st 1未同步(default) 2 已清空
}
