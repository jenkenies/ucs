package com.utstar.ucs.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utstar.ucs.annotation.Decrypt;
import com.utstar.ucs.annotation.Encrypt;
import com.utstar.ucs.constants.UcsConstants;

import com.utstar.ucs.req.hot.SetHotReq;
import com.utstar.ucs.req.hot.GetHotReq;
import com.utstar.ucs.req.hot.GetallHotReq;
import com.utstar.ucs.req.hot.CleanHotReq;
import com.utstar.ucs.req.hot.DelHotReq;
import com.utstar.ucs.req.Request;

import com.utstar.ucs.resp.HotResp;
import com.utstar.ucs.resp.Response;
import com.utstar.ucs.resp.Result;
import com.utstar.ucs.service.HotService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "/ucs", tags= {"追剧接口"} , description = "hot")
@RequestMapping("")
@Slf4j

public class HotController {
	@Autowired
	private HotService hotService;
	
	//http://api.utstarcom.cn/ucs/sethot?userid=<string>&type=<string>&mc=<string>
    @Decrypt
    @Encrypt
    @PostMapping(value = "/sethot")
    @ApiOperation(value="新增追剧", response = Response.class)
    public  Response<String> setHot(@Validated @RequestBody SetHotReq setHotReq, BindingResult bindingResult){
    	Response<String> result = new Response<>();
    	try {
			hotService.setHot(setHotReq);
	    	result.setCode(UcsConstants.UCS_SUCESS);
	    	result.setMsg(UcsConstants.UCS_SUCESS_MSG);
		} catch (Exception e) {
			 log.error("sethot fail {}",e.fillInStackTrace());
			 result.setCode(UcsConstants.UCS_FAIL);
	            result.setMsg(UcsConstants.UCS_FAIL_MSG);		
	    }
    	 return result;
    }
    //http://api.utstarcom.cn/ucs/gethot?userid=<string>&type=<string>&mc=<string>
    @Decrypt
    @Encrypt
    @PostMapping(value = "/gethot")
    @ApiOperation(value="获取单个追剧", response = Response.class)
    public Response<HotResp> getHot(@Validated @RequestBody GetHotReq GetHotReq, BindingResult bindingResult) {
        Response <HotResp> result = new Response<>();
        try {
        	HotResp resp  = hotService.getHot(GetHotReq);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
            result.setSubject(resp);
        }catch (Exception e){
            log.error("gethot fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }
    
   // http://api.utstarcom.cn/ucs/gethot?userid=075526593379&start=0&count=100&type=s,p,c
    @Decrypt
    @Encrypt
    @PostMapping(value = "/getallhot")
    @ApiOperation(value="获取所有追剧", response = Response.class)
    public Object getAllHot(@Validated @RequestBody GetallHotReq req, BindingResult bindingResult) {
    	Result result = new Result();
        try {
        	result = hotService.getAllHot(req);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        }catch (Exception e){
            log.error("geallthot fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }
    
    @Decrypt
    @Encrypt
    @PostMapping(value = "/delhot")
    @ApiOperation(value="删除追剧", response = Response.class)
    public Response<String> delHot(@Validated @RequestBody DelHotReq delHotReq, BindingResult bindingResult) {
    	   Response<String> result = new Response<>();
           try {
           	   hotService.delHot(delHotReq);
               result.setCode(UcsConstants.UCS_SUCESS);
               result.setMsg(UcsConstants.UCS_SUCESS_MSG);
           }catch (Exception e){
               log.error("delhot fail {}",e.getMessage());
               result.setCode(UcsConstants.UCS_FAIL);
               result.setMsg(UcsConstants.UCS_FAIL_MSG);
           }
           return result;	
    }
    
    @Decrypt
    @Encrypt
    @PostMapping(value = "/cleanhot")
    @ApiOperation(value="清空追剧", response = Response.class)
    public Response<String> cleanHot(@Validated @RequestBody CleanHotReq cleanHotReq, BindingResult bindingResult) {
    	   Response<String> result = new Response<>();
           try {
           	   hotService.cleanHot(cleanHotReq);
               result.setCode(UcsConstants.UCS_SUCESS);
               result.setMsg(UcsConstants.UCS_SUCESS_MSG);
           }catch (Exception e){
               log.error("cleanhot fail {}",e.getMessage());
               result.setCode(UcsConstants.UCS_FAIL);
               result.setMsg(UcsConstants.UCS_FAIL_MSG);
           }
           return result;	
    }
    
    @Decrypt
    @Encrypt
    @PostMapping(value = "/queryhotstatus")
    @ApiOperation(value="查询追剧状态", response = Response.class)
    public Response<String> queryhotstatus(@Validated @RequestBody Request request, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            boolean ret = hotService.queryhotstatus(request.getUserid());
            result.setCode(UcsConstants.UCS_SUCESS);
            if (ret) result.setMsg(UcsConstants.UCS_SUCESS_MSG);
            else result.setMsg(UcsConstants.UCS_SUCESS_USER_MESSAGE1);
        }catch (Exception e){
            log.error("queryhotstatus fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }
   
}
