package com.utstar.ucs.controller;

import com.utstar.ucs.annotation.Decrypt;
import com.utstar.ucs.annotation.Encrypt;
import com.utstar.ucs.init.AddBookmark;
import com.utstar.ucs.resp.Response;
import com.utstar.ucs.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "/ucs", tags= {"书签接口"} , description = "bookmark")
@RequestMapping("")
@Slf4j
public class BookmarkController {

    @Autowired
    private RedisUtil redisUtil;

    @Decrypt
    @Encrypt
    @PostMapping(value = "/setbookmark")
    @ApiOperation(value="新增书签", response = Response.class)
    public Response<String> setbookmark(@RequestBody AddBookmark addBookmark) {
        log.info("setbookmark:[{}]", addBookmark);
        Response<String> result = new Response<>();
        redisUtil.set(addBookmark.getUserid(), addBookmark);
        log.info("test[{}]",redisUtil.get(addBookmark.getUserid()));
        result.setCode(HttpStatus.OK.value());
        result.setMsg(HttpStatus.OK.name());
        return result;
    }
}
