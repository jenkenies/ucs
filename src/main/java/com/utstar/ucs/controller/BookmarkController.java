package com.utstar.ucs.controller;

import com.utstar.ucs.annotation.Decrypt;
import com.utstar.ucs.annotation.Encrypt;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.req.*;
import com.utstar.ucs.resp.GetBookmarkResp;
import com.utstar.ucs.resp.Response;
import com.utstar.ucs.resp.Result;
import com.utstar.ucs.service.BookmarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "/ucs", tags= {"书签接口"} , description = "bookmark")
@RequestMapping("")
@Slf4j
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Decrypt
    @Encrypt
    @PostMapping(value = "/setbookmark")
    @ApiOperation(value="新增书签", response = Response.class)
    public Response<String> setbookmark(@Validated @RequestBody SetBookmarkReq setBookmarkReq, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            bookmarkService.setBookmark(setBookmarkReq); 
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        }catch (Exception e){
            log.error("setbookmark fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/getbookmark")
    @ApiOperation(value="获取单个书签", response = Response.class)
    public Response<GetBookmarkResp> getbookmark(@Validated @RequestBody GetBookmarkReq getBookmarkReq, BindingResult bindingResult) {
        Response<GetBookmarkResp> result = new Response<>();
        try {
            GetBookmarkResp resp = bookmarkService.getbookmark(getBookmarkReq);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
            if(StringUtils.isEmpty(resp.getPt())) result.setSubject(null);
            else result.setSubject(resp);
        }catch (Exception e){
            log.error("getbookmark fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/getallbookmark")
    @ApiOperation(value="获取所有书签(观看记录)", response = Response.class)
    public Object getallbookmark(@Validated @RequestBody GetallBookmarkReq req, BindingResult bindingResult) {
        Result result = new Result();
        try {
            result = bookmarkService.getAllbookmark(req);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        }catch (Exception e){
            log.error("getallbookmark fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/querybookmarkstatus")
    @ApiOperation(value="查询书签状态", response = Response.class)
    public Response<String> querybookmarkstatus(@Validated @RequestBody Request request, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            boolean ret = bookmarkService.querybookmarkstatus(request.getUserid());
            result.setCode(UcsConstants.UCS_SUCESS);
            if (ret) result.setMsg(UcsConstants.UCS_SUCESS_MSG);
            else result.setMsg(UcsConstants.UCS_SUCESS_USER_MESSAGE1);
        }catch (Exception e){
            log.error("querybookmarkstatus fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/clearbookmark")
    @ApiOperation(value="清空书签", response = Response.class)
    public Response<String> clearbookmark(@Validated @RequestBody Request request, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            bookmarkService.clearbookmark(request.getUserid());
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        }catch (Exception e){
            log.error("clearbookmark fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/delbookmark")
    @ApiOperation(value="删除书签", response = Response.class)
    public Response<String> delbookmark(@Validated @RequestBody DelBookmarkReq request, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            bookmarkService.delbookmark(request);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        }catch (Exception e){
            log.error("delbookmark fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }
}
