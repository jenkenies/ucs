package com.utstar.ucs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.utstar.ucs.conf.CommonConfig;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.req.Favorite;
import com.utstar.ucs.req.*;

import com.utstar.ucs.resp.GetReserve;
import com.utstar.ucs.resp.Result;
import com.utstar.ucs.service.ReserveService;
import com.utstar.ucs.util.LocalDateTimeUtils;
import com.utstar.ucs.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Slf4j
@Service
public class ReserveServiceImpl implements ReserveService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CommonConfig commonConfig;

    /***
     * 新增预约：相同的媒资type 要update
     * 新增预约数大于10后 替换原来的
     * @param reserve
     * @return
     * @throws Exception
     */
    @Override
    public void setReserve(SetReserveReq reserve) throws Exception{
        Integer maxReserveNum = commonConfig.getReservetotal();
        String  userid = reserve.getUserid();
        String key = UcsConstants.RESERVE_REDIS_PREFIX + userid;
        String createTime = LocalDateTimeUtils.NOW;
        String mode = StringUtils.isEmpty(reserve.getMode()) ?  "1" : reserve.getMode();
        reserve.setMode(mode);
        Map<String, Object> newMap = new HashMap<>();
        Map<Object, Object> oldmap =  redisUtil.hmget(key);
        List<SetReserveReq> reserveList = new ArrayList<>();

        SetReserveReq newReserve = copyProperties(reserve);
        // 没有就新增数据
        if(CollectionUtils.isEmpty(oldmap)){
            reserveList.add(newReserve);
            newMap.put(mode,reserveList);
        } else {
            //判断书签的模式是否相同，不相同时则默认把其他模式的数据的加入到集合
            //相同时则需判断媒资的code是否一样，如果一样，则只需更新时间，若不一致，则需要加入到集合
            boolean modeflag = false;
            for(Map.Entry<Object, Object> object: oldmap.entrySet()) {
                String pastMode = (String) object.getKey();
                List<SetReserveReq> pastReqList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), SetReserveReq.class);
                //若为之前已有的模式
                if(StringUtils.equals(pastMode, mode)) {
                    if(!CollectionUtils.isEmpty(pastReqList)) {

                        for(SetReserveReq pastreq:pastReqList) {
                            //修改后需要更新,且插入到队列头
                            if(!StringUtils.equals(pastreq.getMc(), reserve.getMc())) {
                                reserveList.add(pastreq);
                            }
                        }
                        //新增与修改的均要放到队列头
                        reserveList.add(newReserve);

                        //若书签的数量超过指定的数量，则删掉队列历史数据
                        if(reserveList.size() > maxReserveNum) { reserveList.remove(0);}
                    }
                    newMap.put(mode, reserveList);
                    modeflag = true;
                }
            }
            //判断是否为新模式,新模式下需要新增
            if(!modeflag) {
                reserveList.add(copyProperties(reserve));
                newMap.put(mode, reserveList);
            }
        }
        long expireTime = commonConfig.getExpiretime() * 60 * 60 * 24 * 30;
        //最后统一把结果保存到redis
        redisUtil.hmset(key, newMap, expireTime);

    }




    /***
     *
     * 终端查询单个预约媒资,根据MC来查询,只有10个值
     * @param reserve
     * @return
     */
    @Override
    public GetReserve getReserve(GetReserve reserve) throws Exception{
        String mode = StringUtils.isEmpty(reserve.getMode()) ?  "1" : reserve.getMode();
        GetReserve resp = new GetReserve();
        String mc = reserve.getMc();
        String userid = reserve.getUserid();
        String mcKey = UcsConstants.RESERVE_REDIS_PREFIX + userid;
        Map<Object, Object> map = redisUtil.hmget(mcKey);
        List<GetReserve> reserveList = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if(entry.getKey().toString().equals(mode)){
                List<GetReserve> reserves = JSONArray.parseArray(JSON.toJSONString(entry.getValue()),GetReserve.class);
                reserveList = reserves;
            }
        }

        for (GetReserve getReserve : reserveList) {
            if(StringUtils.equals(getReserve.getMc(), mc)){
                resp.setMc(getReserve.getMc());
                resp.setType(getReserve.getType());
                resp.setCreatetime(getReserve.getCreatetime());
                resp.setEndtime(getReserve.getEndtime());
                return resp;
            }
        }

        return resp;
    }

    @Override
    public Result getAllReserve(GetReserve reserve) {
        Result result = new Result();
        int start = reserve.getStart();
        int conut = reserve.getCount();
        //拼接key
        String key = UcsConstants.RESERVE_REDIS_PREFIX + reserve.getUserid();
        Map<Object, Object> map = redisUtil.hmget(key);
        List<GetReserve> list = new ArrayList<>();

        // 如果有多个mode数据就拼接成一个list
        for (Map.Entry<Object, Object> object : map.entrySet()) {
            List<GetReserve> reqList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), GetReserve.class);
            list.addAll(reqList);
        }
        List<GetReserve> getList = new ArrayList<>();
        // 查询下标数 + 查询数据条数 = 查询数据最后一条数据的下标
        int endIndex = start + conut;
        // 如果数据列表小于 查询数据最后一条数据的下标 就返回全部数据
        int index = list.size() < endIndex ? list.size() : reserve.getCount();
        for (int i = start; i < index; i++) {
            getList.add(list.get(i));
        }
        result.setCount(conut);
        result.setStart(start);
        result.setTotal(getList.size());
        result.setSubject(getList);
        return result;

    }

    @Override
    public void clearReserve(GetReserve reserve) {
            String key = UcsConstants.RESERVE_REDIS_PREFIX + reserve.getUserid();
            Map<Object, Object> map = redisUtil.hmget(key);
            Map<String, Object> pullMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(map)) {
                for (Map.Entry<Object, Object> object : map.entrySet()) {
                    List<Favorite> pullList = new ArrayList<>();
                    String mode = (String) object.getKey();
                    List<GetReserve> redisList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), GetReserve.class);
                    for (GetReserve getReserve : redisList) {

                    }
                    pullMap.put(mode, pullList);
                }
            }
            redisUtil.hmset(key, pullMap);


    }


    private SetReserveReq copyProperties(SetReserveReq reserve) {

        SetReserveReq reserveReq = SetReserveReq.builder()
                .mode(reserve.getMode())
                .createtime(LocalDateTimeUtils.NOW)
                .starttime(reserve.getStarttime())
                .endtime(reserve.getEndtime())
                .mc(reserve.getMc())
                .userid(reserve.getUserid())
                .type(reserve.getType()).build();
        return reserveReq;
    }


}


