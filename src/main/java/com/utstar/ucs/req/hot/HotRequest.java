package com.utstar.ucs.req.hot;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
@Data
public class HotRequest implements Serializable {
	
	@NotBlank(message = "userid不能为空")
    private String userid; //用户id
	private String mode; //1:标准模式 (default)2:少年模式 3:老年模式 
}
