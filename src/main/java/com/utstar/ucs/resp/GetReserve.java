package com.utstar.ucs.resp;


import com.utstar.ucs.util.Pagination;
import lombok.Data;


@Data
public class GetReserve extends Pagination {

    private String userid; //用户id
    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式

    private String starttime;
    private String endtime;

    private String createtime;


}
