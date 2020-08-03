package com.utstar.ucs.req;

import lombok.Data;

@Data
public class ModeDTO {

    private String pt; //记录观影时间HH24MISS 最大支持24小时收视书签
    private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式
    private String createtime;
}
