package com.utstar.ucs.req;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class GetHotReq implements Serializable{

    private String userid; //用户id
    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    private String typelist;
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String mclist; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String eposidenum;
    private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式  
    private int start;//偏移位置，默认0为开始位置
    private int count;//请求个数，默认100个
    private String begintime; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String endtime;//预约操作结束时间
    private String order;//排序方式，默认按时间倒序
    
    
}
