package com.utstar.ucs.controller;

import com.utstar.ucs.annotation.Decrypt;
import com.utstar.ucs.annotation.Encrypt;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.req.Favorite;
import com.utstar.ucs.req.Request;
import com.utstar.ucs.req.hot.CleanHotReq;
import com.utstar.ucs.req.hot.DelHotReq;
import com.utstar.ucs.resp.GetFavoriteReq;
import com.utstar.ucs.resp.Response;
import com.utstar.ucs.resp.Result;
import com.utstar.ucs.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Api(value = "/ucs", tags= {"收藏接口"} , description = "favorite")
@RequestMapping("")
@Slf4j
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;


    @Decrypt
    @Encrypt
    @PostMapping(value = "/setfavorite")
    @ApiOperation(value="新增收藏", response = Response.class)
    public Response<String> setfavorite(@Validated @RequestBody Favorite favorite , BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            favoriteService.setfavorite(favorite);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        } catch (Exception e) {
            log.error("setfavorite fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return  result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/getallfavorite")
    @ApiOperation(value="获取全部收藏", response = Response.class)
    public Object getallfavorite(@Validated @RequestBody GetFavoriteReq getFavorite, BindingResult bindingResult) throws IOException {
        Result result = new Result();
        try {
            result = favoriteService.getallfavorite(getFavorite);
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
    @PostMapping(value = "/getfavorite")
    @ApiOperation(value="获取单个收藏", response = Response.class)
    public Response<Object> getfavorite(@Validated @RequestBody Favorite favorite, BindingResult bindingResult) {
        Response<Object> result = new Response<>();
        try {
            Favorite fav = favoriteService.getfavorite(favorite);
            result.setSubject(fav);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        } catch (Exception e) {
            log.error("getfavorite fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }

        return result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/clearfavorite")
    @ApiOperation(value="清空收藏", response = Response.class)
    public Response<String> clearfavorite(@Validated @RequestBody CleanHotReq hotReq, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            favoriteService.clearfavorite(hotReq);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        } catch (Exception e) {
            log.error("clearfavorite fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return  result;
    }


    @Decrypt
    @Encrypt
    @PostMapping(value = "/queryfavoritestatus")
    @ApiOperation(value="查询收藏状态", response = Response.class)
    public Response<String> queryfavoritestatus(@Validated @RequestBody Request request, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            boolean ret = favoriteService.queryfavoritestatus(request);
            result.setCode(UcsConstants.UCS_SUCESS);
            if(ret) result.setMsg(UcsConstants.UCS_SUCESS_MSG);
            else result.setMsg(UcsConstants.UCS_SUCESS_USER_MESSAGE1);
        } catch (Exception e) {
            log.error("queryfavoritestatus fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return  result;
    }

    @Decrypt
    @Encrypt
    @PostMapping(value = "/delfavorite")
    @ApiOperation(value="删除收藏", response = Response.class)
    public Response<String> delfavorite(@Validated @RequestBody DelHotReq delHotReq, BindingResult bindingResult) {
        Response<String> result = new Response<>();
        try {
            favoriteService.delfavorite(delHotReq);
            result.setCode(UcsConstants.UCS_SUCESS);
            result.setMsg(UcsConstants.UCS_SUCESS_MSG);
        }catch (Exception e){
            log.error("delfavorite fail {}",e.getMessage());
            result.setCode(UcsConstants.UCS_FAIL);
            result.setMsg(UcsConstants.UCS_FAIL_MSG);
        }
        return result;
    }


}
