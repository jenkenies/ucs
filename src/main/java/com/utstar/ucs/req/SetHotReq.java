package com.utstar.ucs.req;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Data
public class SetHotReq implements Serializable {
	 @NotNull(message = "uerid不能为空")
	  private String userid;//用户id
	  private String mode; //1:标准模式，default 1标准模式 2:少年模式 3:老年模式
	  private String type;//类型： s:连续剧，p:电影，c:栏目
	  private String mc; //连续剧类型，填连续剧单集编码 电影类型，填电影编码
	  private String currentnum;//第几集
	  @JsonIgnore
	  private String createtime;
}
