package com.utstar.ucs.req.hot;


import java.io.Serializable;

import javax.validation.constraints.NotBlank;


import lombok.Data;
@Data
public class GetHotReq implements Serializable {
	
	@NotBlank(message = "userid不能为空")
	private String userid; // 用户id
	
	private String mode; // 1:标准模式 (default)2:少年模式 3:老年模式	

	@NotBlank(message = "mc不能为空")
    private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
	
	@NotBlank(message = "type不能为空")
    private String type; //类型： s:连续剧，p:电影，c:栏目
    
}
