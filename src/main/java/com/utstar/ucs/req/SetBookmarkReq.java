package com.utstar.ucs.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Slf4j
@Data
public class SetBookmarkReq implements Serializable {
    @NotBlank(message = "userid不能为空")
    private String userid; //用户id
    @NotBlank(message = "type不能为空")
    private String type; //书签类型： s:连续剧，p:电影，c:栏目
    @NotBlank(message = "mc不能为空")
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
    private String mc2; //媒资编码2 连续剧类型，填连续剧剧头编码 电影类型，不填
    @NotBlank(message = "pt不能为空")
    private String pt; //记录观影时间HH24MISS 最大支持24小时收视书签
    private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式
    @JsonIgnore
    private String createtime;//该注解作用：字段在swagger-ui不显示
}
