package com.utstar.ucs;

import com.utstar.ucs.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TestRedis {

    @Autowired
    private RedisUtil redisUtil;

    //@Test
    public void testSet() {
        try{
            Map<String, Object> map = new HashMap<>();
            map.put("age", 12);
            map.put("name", "xm");
            redisUtil.set("info", map);
            log.info("testSet:{}", redisUtil.get("info"));
        } catch (Exception ex) {
            log.error("test[{}]", ex.fillInStackTrace());
        }
    }

}
