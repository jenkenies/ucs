package com.utstar.ucs.req.hot;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;


import lombok.Data;
@Data
public class GetallHotReq implements Serializable{
	
	@NotBlank(message = "userid不能为空")
	private String userid; // 用户id	
	private String mode; // 1:标准模式 (default)2:少年模式 3:老年模式	
    @NotBlank(message = "typelist不能为空")
    private String typelist; //类型，以逗号分隔 比如 p:电影,s:连续剧
    private int start=0;
    private int count=100;
    private String begintime;
    private String endtime;
    private String order; //默认按时间倒叙 1倒叙(最新的记录在前面)
   // private String selection; //支持几种模式，1：连续剧只返回最后一集书签,default 1 2：返回所有剧集书签
 
}
