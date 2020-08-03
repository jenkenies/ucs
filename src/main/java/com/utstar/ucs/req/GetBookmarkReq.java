package com.utstar.ucs.req;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Data
public class GetBookmarkReq implements Serializable {
    @NotBlank(message = "userid不能为空")
    private String userid; //用户id
    @NotBlank(message = "type不能为空")
    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    @NotBlank(message = "mc不能为空")
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式
}
