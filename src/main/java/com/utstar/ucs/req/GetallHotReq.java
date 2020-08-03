package com.utstar.ucs.req;

import lombok.Data;

@Data
public class GetallHotReq {
	
    private String userid; //用户id
    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    private String typelist; //书签类型： s:连续剧，p:电影，c:栏目
    private int start=0;
    private int count=100;
    private String begintime;
    private String endtime;
    private String order; //默认按时间倒叙 1倒叙(最新的记录在前面)
   // private String selection; //支持几种模式，1：连续剧只返回最后一集书签,default 1 2：返回所有剧集书签
    private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式
}
