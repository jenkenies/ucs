package com.utstar.ucs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.utstar.ucs.conf.CommonConfig;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.req.Favorite;
import com.utstar.ucs.req.HotDTO;
import com.utstar.ucs.req.Request;
import com.utstar.ucs.req.hot.CleanHotReq;
import com.utstar.ucs.req.hot.DelHotReq;
import com.utstar.ucs.resp.GetFavoriteReq;
import com.utstar.ucs.resp.Result;
import com.utstar.ucs.service.FavoriteService;
import com.utstar.ucs.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "favoriteService")
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CommonConfig commonConfig;


    @Override
    public void setfavorite(Favorite favorite) {
        String mode = StringUtils.isEmpty(favorite.getMode()) ?  "1" : favorite.getMode();
        List<Favorite> list = new ArrayList<>();
        //拼接key
        String key = UcsConstants.FAVORITE_REDIS_PREFIX + favorite.getUserid();
        String createTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Map<String, Object> newmap = new HashMap<>();
        Map<Object, Object> oldmap = redisUtil.hmget(key);
        favorite.setSt("1");

        //若该用户不存在历史数据，则直接新增一条
        if(CollectionUtils.isEmpty(oldmap)) {
            favorite.setCreatetime(createTime);
            list.add(favorite);
            newmap.put(mode, list);
        } else {
            //判断书签的模式是否相同，不相同时则默认把其他模式的数据的加入到集合
            for(Map.Entry<Object, Object> object: oldmap.entrySet()) {
                String pastMode = (String) object.getKey();
                // 获取已有数据列表
                List<Favorite> redisList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), Favorite.class);

                //若为之前已有的模式
                if(StringUtils.equals(pastMode, mode)) {
                    if(!CollectionUtils.isEmpty(redisList)) {
                        // 遍历数据列表，并添加最新的数据
                        for (Favorite fav : redisList) {
                            // 先将与本次操作无关的数据放入集合
                            if(!StringUtils.equals(fav.getMc(), favorite.getMc())) {
                                list.add(fav);
                            }
                        }
                        //新增与修改的均要放到队列头
                        favorite.setCreatetime(createTime);
                        list.add(favorite);
                        //若书签的数量超过指定的数量，则删掉队列历史数据
                        if(list.size() >= commonConfig.getFavoritektotal()) {
                            list.remove(0);
                        }
                    }

                } else {//新模式下需要新增
                    favorite.setCreatetime(createTime);
                    list.add(favorite);
                }
                newmap.put(mode, list);
            }
        }
        //最后统一把结果保存到redis
        redisUtil.hmset(key, newmap);
    }

    @Override
    public Favorite getfavorite(Favorite favorite) {
        // 如果没传mode 默认为1
        String mode = StringUtils.isEmpty(favorite.getMode()) ?  "1" : favorite.getMode();
        //拼接key
        String key = UcsConstants.FAVORITE_REDIS_PREFIX + favorite.getUserid();
        Map<Object, Object> map = redisUtil.hmget(key);
        if (!map.isEmpty()) {
            List<Favorite> list = JSONArray.parseArray(JSON.toJSONString(map.get(mode)), Favorite.class);
            for (Favorite fav : list) {
                // 条件符合就返回该数据
                if (fav.getType().equals(favorite.getType()) && fav.getMc().equals(favorite.getMc())) {
                    return fav;
                }
            }
        }

        return null;
    }

    @Override
    public boolean queryfavoritestatus(Request request) {
        boolean flag = false;
        String key = UcsConstants.FAVORITE_REDIS_PREFIX+request.getUserid();
        if(redisUtil.hasKey(key)) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    @Override
    public void delfavorite(DelHotReq req) {
        String key = UcsConstants.FAVORITE_REDIS_PREFIX+req.getUserid();
        String mode = StringUtils.isEmpty(req.getMode())?"1":req.getMode();
        Object object =  redisUtil.hget(key, mode);
        List<Favorite> list = JSONArray.parseArray(JSON.toJSONString(object), Favorite.class);

        List<Favorite> newList = new ArrayList<>();
        Favorite favorite = new Favorite();
        String[] mclist =req.getMclist().split(",");
        String[] typelist =req.getTypelist().split(",");
        list.stream().map(favDTO ->{
            for (int i=0;i<mclist.length;i++) {
                if (typelist[i].equals(favDTO.getType()) && mclist[i].equals(favDTO.getMc()) ) {
                    BeanUtils.copyProperties(favDTO, req);
                    favorite.setSt("2");
                }
                newList.add(favorite);
            }
            return favDTO;
        }).collect(Collectors.toList());
        redisUtil.hset(key, mode, newList);

    }

    @Override
    public Result getallfavorite(GetFavoriteReq getFavoriteReq) throws ParseException {
        Result result = new Result();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        int start = getFavoriteReq.getStart();
        int conut = getFavoriteReq.getCount();

        // 将时间转为时间戳
        long btime = StringUtils.isEmpty(getFavoriteReq.getBegintime()) ? 0 : Long.valueOf(getFavoriteReq.getBegintime());
        long etime = StringUtils.isEmpty(getFavoriteReq.getEndtime()) ? 0 : Long.valueOf(getFavoriteReq.getEndtime());

        //拼接key
        String key = UcsConstants.FAVORITE_REDIS_PREFIX + getFavoriteReq.getUserid();
        Map<Object, Object> map = redisUtil.hmget(key);
        // 如果没传mode 默认为1
        String mode = StringUtils.isEmpty(getFavoriteReq.getMode()) ?  "1" : getFavoriteReq.getMode();
        List<Favorite> list = new ArrayList<>();
        List<Favorite> favList = new ArrayList<>();
        list = JSONArray.parseArray(JSON.toJSONString(map.get(mode)), Favorite.class);

        // 先筛选符合条件数据，再分页
        if(!CollectionUtils.isEmpty(list)) {
            for (Favorite favoriteReq : list) {
                long creaTime = Long.valueOf(favoriteReq.getCreatetime());
                String type = favoriteReq.getType();
                String mc = favoriteReq.getMc();
                boolean status = true;
                // 开始时间不为空 判断该条数据是否满足条件
                if (btime > 0) {
                    if (creaTime < btime) {
                        status = false;
                    }
                }
                // 结束时间不为空 判断该条数据是否满足条件
                if (etime > 0) {
                    if (creaTime > etime) {
                        status = false;
                    }
                }
                // 类型组合不为空 判断该条数据是否满足条件
                if (!StringUtils.isEmpty(getFavoriteReq.getTypelist())) {
                    String[] types = getFavoriteReq.getTypelist().split(",");
                    if (!Arrays.asList(types).contains(type)) {
                        status = false;
                    }
                }
                // 媒资编码列表不为空 判断该条数据是否满足条件
                // 文档上没有写出分隔符，默认以逗号分隔
                if (!StringUtils.isEmpty(getFavoriteReq.getMclist())) {
                    String[] mcs = getFavoriteReq.getMclist().split(",");
                    if (!Arrays.asList(mcs).contains(mc)) {
                        status = false;
                    }
                }
                // 判断状态是否为未同步
                if (favoriteReq.getSt().equals("2")) {
                    status = false;
                }

                // 满足条件就添加到集合里
                if (status) {
                    favList.add(favoriteReq);
                }

            }
        }
        // 将满足条件的数据分页封装
        List<Favorite> getList = new ArrayList<>();
        // 查询下标数 + 查询数据条数 = 查询数据最后一条数据的下标
        int endIndex = start + conut;
        // 如果数据列表小于 查询数据最后一条数据的下标 就返回全部数据
        int index = favList.size() < endIndex ? favList.size() : endIndex;


        for (int i = start; i < index; i++) {
            getList.add(favList.get(i));
        }
        result.setCount(conut);
        result.setStart(start);
        result.setTotal(getList.size());
        // 将list倒序排列，将最新放在队列头
        Collections.reverse(getList);
        result.setSubject(getList);

        return result;
    }

    @Override
    public void clearfavorite(CleanHotReq hotReq) {
        String key = UcsConstants.FAVORITE_REDIS_PREFIX+hotReq.getUserid();
        String mode = StringUtils.isEmpty(hotReq.getMode())?"1":hotReq.getMode();

        Object object =  redisUtil.hget(key, mode);
        List<Favorite> list = JSONArray.parseArray(JSON.toJSONString(object), Favorite.class);
        List<Favorite> newList = new ArrayList<>();
        String[] typelist =hotReq.getTypelist().split(",");
        list.stream().map(favDTO ->{
            if (Arrays.asList(typelist).contains(favDTO.getType())) {
                favDTO.setSt("2");
            }
            newList.add(favDTO);
            return favDTO;
        }).collect(Collectors.toList());
        //最后统一把结果保存到redis
        redisUtil.hset(key, mode,newList);

    }

}
