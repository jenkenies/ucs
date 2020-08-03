package com.utstar.ucs.controller;


import com.utstar.ucs.annotation.Decrypt;
import com.utstar.ucs.annotation.Encrypt;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.req.SetReserveReq;
import com.utstar.ucs.resp.*;
import com.utstar.ucs.service.ReserveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Api(value = "/ucs", tags= {"预约播放接口"} , description = "reserve")
@RequestMapping("")
@Slf4j
@RequiredArgsConstructor
public class ReserveController {


    private final ReserveService reserveService;

    @Decrypt
    @Encrypt
    @PostMapping(value = "/setreserve")
    @ApiOperation(value="新增预约", response = Response.class)
    public Response<String> setReserve(@RequestBody SetReserveReq reserve) {
        Response<String> result = new Response<>();
        try {
            reserveService.setReserve(reserve);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        }catch (Exception e){
            log.error("setReserve fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }


    @Decrypt
    @Encrypt
    @PostMapping(value = "/getreserve")
    @ApiOperation(value="获取单个预约", response = Response.class)
    public Response<GetReserve> getSingleReserve(@RequestBody GetReserve reserve) {
        Response<GetReserve> result = new Response<>();
        try {
            GetReserve resp = reserveService.getReserve(reserve);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
            result.setSubject(resp);
        }catch (Exception e){
            log.error("getbookmark fail {}",e.fillInStackTrace());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }



    @Decrypt
    @Encrypt
    @PostMapping(value = "/xx")
    @ApiOperation(value="获取全部预约", response = Response.class)
    public Result getReserves(@RequestBody GetReserve reserve) throws IOException {
        Result result = new Result();
        try {
            result = reserveService.getAllReserve(reserve);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        } catch (Exception e) {
            log.error("getallfavorite fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }

        return result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/")
    @ApiOperation(value="清空预约", response = Response.class)
    public Response<String> clearAllReserves(@RequestBody GetReserve reserve) {
        Response<String> result = new Response<>();
        try {
            reserveService.clearReserve(reserve);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        } catch (Exception e) {
            log.error("clearfavorite fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return  result;
    }
}

