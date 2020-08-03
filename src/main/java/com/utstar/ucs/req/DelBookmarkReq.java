package com.utstar.ucs.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DelBookmarkReq extends Request{
    @NotBlank(message = "mclist不能为空")
    private String mclist;//删除指定节目code的观看，半角逗号分隔
    @NotBlank(message = "typelist不能为空")
    private String typelist;//和mclist对应的节目类型，半角逗号分隔
    private String mode;//1:标准模式，default 1标准模式 2:少年模式 3:老年模式
}
