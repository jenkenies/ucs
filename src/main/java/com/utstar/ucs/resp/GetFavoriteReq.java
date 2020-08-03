package com.utstar.ucs.resp;

import com.utstar.ucs.req.Favorite;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GetFavoriteReq {

    @NotBlank(message = "userid不能为空")
    private String userid; //用户id
    private String typelist; //书签类型： s:连续剧，p:电影，c:栏目
    private int start=0;
    private int count=100;
    private String begintime;
    private String endtime;
    private String order; //默认按时间倒叙 1倒叙(最新的记录在前面)
    private String mclist;
    private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式

}
