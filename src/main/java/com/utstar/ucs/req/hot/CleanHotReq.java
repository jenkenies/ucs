package com.utstar.ucs.req.hot;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;


import lombok.Data;

@Data
public class CleanHotReq implements Serializable{
	
	@NotBlank(message = "userid不能为空")
	private String userid; // 用户id	
	private String mode; // 1:标准模式 (default)2:少年模式 3:老年模式	
	@NotBlank(message = "typelist不能为空")	
    private String typelist;//和mclist对应的节目类型，半角逗号分隔

}
