package com.utstar.ucs.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Copyright (C), 2015-2019, 优地科技有限公司
 * FileName: Response
 * Author:   creambing
 * Date:     2019-07-15 14:37
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ApiModel(value="ucs return object",description="respose")
public class Response<T> {

    @ApiModelProperty(value="return code",name="code",example="200")
    private Integer code;
    @ApiModelProperty(value="return msg",name="msg",example="success")
    private String msg;
    @ApiModelProperty(value="detail data",name="data",example="ucs object")
    private T data;

}