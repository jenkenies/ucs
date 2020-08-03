package com.utstar.ucs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.utstar.ucs.conf.CommonConfig;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.req.*;
import com.utstar.ucs.resp.GetBookmarkResp;
import com.utstar.ucs.resp.Result;
import com.utstar.ucs.service.BookmarkService;
import com.utstar.ucs.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.rmi.runtime.Log;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service(value = "bookmarkService")
public class BookmarkServiceImpl implements BookmarkService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CommonConfig commonConfig;


    //新增书签
    @Override
    public boolean setBookmark(SetBookmarkReq setBookmarkReq) throws Exception{
        int bookmarkTotal = commonConfig.getBookmarktotal();
        String userid = setBookmarkReq.getUserid();
        String mode = StringUtils.isEmpty(setBookmarkReq.getMode()) ?  "1" : setBookmarkReq.getMode();
        String key = UcsConstants.BOOKMARK_REDIS_PREFIX+userid;
        Map<String, Object> newmap = new HashMap<>();
        Map<Object, Object> oldmap = redisUtil.hmget(key);
        List<SetBookmarkDTO> reqList = new ArrayList<>();
        String createTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //若该用户不存在历史数据，则直接新增一条
        if(CollectionUtils.isEmpty(oldmap)) {
            SetBookmarkDTO req = new SetBookmarkDTO();
            BeanUtils.copyProperties(setBookmarkReq, req);
            req.setCreatetime(createTime);
            reqList.add(req);
            newmap.put(mode, reqList);
        } else {
            //判断书签的模式是否相同，不相同时则建一个新的模式
            //相同时则需判断媒资的code是否一样,无论是更新还是新增均加到队列尾部
            boolean modeflag = false;
            for(Map.Entry<Object, Object> object: oldmap.entrySet()) {
                String pastMode = (String) object.getKey();
                List<SetBookmarkReq> pastReqList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), SetBookmarkReq.class);
                //若为之前已有的模式
                if(StringUtils.equals(pastMode, mode)) {
                    if(!CollectionUtils.isEmpty(pastReqList)) {

                        for(SetBookmarkReq pastreq:pastReqList) {
                            //历史的数据放到集合
                            if(!StringUtils.equals(pastreq.getMc(), setBookmarkReq.getMc())&&
                            !StringUtils.equals(pastreq.getType(), setBookmarkReq.getType())) {
                                SetBookmarkDTO req = new SetBookmarkDTO();
                                BeanUtils.copyProperties(pastreq, req);
                                reqList.add(req);
                            }
                        }
                        //新增与修改的均要放到队列头
                        SetBookmarkDTO req = new SetBookmarkDTO();
                        BeanUtils.copyProperties(setBookmarkReq, req);
                        req.setCreatetime(createTime);
                        reqList.add(req);

                        //若书签的数量超过指定的数量，则删掉队列历史数据
                        if(reqList.size() > bookmarkTotal) { reqList.remove(0);}
                    }
                    newmap.put(mode, reqList);
                    modeflag = true;
                }
            }
            //判断是否为新模式,新模式下需要新增
            if(!modeflag) {
                SetBookmarkDTO req = new SetBookmarkDTO();
                BeanUtils.copyProperties(setBookmarkReq, req);
                req.setCreatetime(createTime);
                reqList.add(req);
                newmap.put(mode, reqList);
            }
        }
        long expireTime = commonConfig.getExpiretime() * 60 * 60 * 24 * 30;
        //最后统一把结果保存到redis
        redisUtil.hmset(key, newmap, expireTime);

        //保存单个书签单个媒资的key-value
        String mcKey = UcsConstants.BOOKMARK_REDIS_PREFIX+userid+":"+setBookmarkReq.getMc();
        BookmarkDTO bookmarkDTO =  JSON.parseObject(JSON.toJSONString(redisUtil.get(mcKey)), BookmarkDTO.class);
        if(bookmarkDTO != null) {
            BeanUtils.copyProperties(setBookmarkReq, bookmarkDTO);
            List<ModeDTO> modeDTOS = bookmarkDTO.getModept();
            List<ModeDTO> DTOS = new ArrayList<>();
            if(CollectionUtils.isEmpty(modeDTOS)) {
                modeDTOS = new ArrayList<>();
                ModeDTO modeDTO = new ModeDTO();
                modeDTO.setMode(mode);
                modeDTO.setPt(setBookmarkReq.getPt());
                modeDTO.setCreatetime(createTime);
                modeDTOS.add(modeDTO);
                bookmarkDTO.setModept(modeDTOS);
            } else {
                ModeDTO modes= new ModeDTO();
                modes.setPt(setBookmarkReq.getPt());
                modes.setCreatetime(createTime);
                modes.setMode(mode);
                //每种模式下存一种mode
                for(ModeDTO modeDTO: modeDTOS) {
                    if(!StringUtils.equals(modeDTO.getMode(), mode)) {
                        DTOS.add(modeDTO);
                    }
                }
                DTOS.add(modes);
                bookmarkDTO.setModept(DTOS);
            }
        } else {
            bookmarkDTO = new BookmarkDTO();
            BeanUtils.copyProperties(setBookmarkReq, bookmarkDTO);
            List<ModeDTO> modeDTOS = new ArrayList<>();
            ModeDTO modeDTO = new ModeDTO();
            modeDTO.setMode(mode);
            modeDTO.setPt(setBookmarkReq.getPt());
            modeDTO.setCreatetime(createTime);
            modeDTOS.add(modeDTO);
            bookmarkDTO.setModept(modeDTOS);
        }
        redisUtil.set(mcKey, bookmarkDTO, expireTime);

        return true;
    }

    //查询单个书签
    @Override
    public GetBookmarkResp getbookmark(GetBookmarkReq getBookmarkReq) throws Exception{
        GetBookmarkResp resp = new GetBookmarkResp();
        String mc = getBookmarkReq.getMc();
        String userid = getBookmarkReq.getUserid();
        String mode = getBookmarkReq.getMode();
        String mcKey = UcsConstants.BOOKMARK_REDIS_PREFIX+userid+":"+ mc;
        BookmarkDTO bookmarkDTO =  JSON.parseObject(JSON.toJSONString(redisUtil.get(mcKey)), BookmarkDTO.class);
        if(bookmarkDTO != null && !StringUtils.equals(bookmarkDTO.getSt(), "2")) {
            resp.setUserid(bookmarkDTO.getUserid());
            resp.setMc(bookmarkDTO.getMc());
            resp.setType(bookmarkDTO.getType());
            resp.setMc2(bookmarkDTO.getMc2());
            List<ModeDTO> modeDTOS = bookmarkDTO.getModept();
            if(!CollectionUtils.isEmpty(modeDTOS)) {
                for(ModeDTO modeDTO:modeDTOS) {
                    if(StringUtils.equals(mode, modeDTO.getMode())) {
                        resp.setPt(modeDTO.getPt());
                    }
                }
            }
        }
        return resp;
    }

    //查询书签状态
    @Override
    public boolean querybookmarkstatus(String userid) throws Exception{
        boolean ret = false;
        String key = UcsConstants.BOOKMARK_REDIS_PREFIX+userid;
        if(redisUtil.hasKey(key)) {
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    //清空书签
    @Override
    public void clearbookmark(String userid) throws Exception{
        String key = UcsConstants.BOOKMARK_REDIS_PREFIX+userid;
        Map<Object, Object> map = redisUtil.hmget(key);
        long expireTime = 1 * 60 * 60 * 24 * 30;
        Map<String, Object> newmap = new HashMap<>();
        List<String> list = new ArrayList<>();//记录子key
        if(!CollectionUtils.isEmpty(map)) {
            for (Map.Entry<Object, Object> object : map.entrySet()) {
                String mode = (String) object.getKey();
                List<SetBookmarkDTO> newList = new ArrayList<>();
                List<SetBookmarkDTO> reqList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), SetBookmarkDTO.class);
                if(!CollectionUtils.isEmpty(reqList)) {
                    for(SetBookmarkDTO req: reqList) {
                        req.setSt("2");//设置状态为2
                        newList.add(req);
                        String sonkey = UcsConstants.BOOKMARK_REDIS_PREFIX+userid+":"+req.getMc();
                        BookmarkDTO bookmarkDTO =  JSON.parseObject(JSON.toJSONString(redisUtil.get(sonkey)), BookmarkDTO.class);
                        if(StringUtils.isNotEmpty(req.getMc()) && !list.contains(sonkey) && bookmarkDTO != null) {
                            list.add(sonkey);
                            bookmarkDTO.setSt("2");
                            redisUtil.set(sonkey, bookmarkDTO, expireTime);
                        }
                    }
                }
                newmap.put(mode, newList);
            }
        }
        redisUtil.hmset(key, newmap, expireTime);
    }

    //删除指定的书签
    @Override
    public void delbookmark(DelBookmarkReq request) {
        String userid = request.getUserid();
        String requestMode = request.getMode();
        List<String> mcList = Arrays.asList(request.getMclist().split(","));
        List<String> typeList = Arrays.asList(request.getTypelist().split(","));
        String key = UcsConstants.BOOKMARK_REDIS_PREFIX+ userid;
        Map<Object, Object> map = redisUtil.hmget(key);
        long expireTime = 1 * 60 * 60 * 24 * 30;
        Map<String, Object> newmap = new HashMap<>();
        List<String> list = new ArrayList<>();//记录子key
        if(!CollectionUtils.isEmpty(map)) {
            for (Map.Entry<Object, Object> object : map.entrySet()) {
                String mode = (String) object.getKey();
                //只修改指定模式下的数据
                if(StringUtils.equals(mode, requestMode)) {
                    List<SetBookmarkDTO> newList = new ArrayList<>();
                    List<SetBookmarkDTO> reqList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), SetBookmarkDTO.class);
                    if (!CollectionUtils.isEmpty(reqList)) {
                        for (SetBookmarkDTO req : reqList) {
                            //删除指定媒资
                            if (mcList.contains(req.getMc())) {
                                req.setSt("2");//设置状态为2
                            }
                            newList.add(req);
                            String sonkey = UcsConstants.BOOKMARK_REDIS_PREFIX + userid + ":" + req.getMc();
                            BookmarkDTO bookmarkDTO = JSON.parseObject(JSON.toJSONString(redisUtil.get(sonkey)), BookmarkDTO.class);
                            //删除指定媒资
                            if (StringUtils.isNotEmpty(req.getMc()) && !list.contains(sonkey) &&
                                    bookmarkDTO != null && mcList.contains(req.getMc()) && typeList.contains(req.getType())) {
                                list.add(sonkey);
                                bookmarkDTO.setSt("2");
                                redisUtil.set(sonkey, bookmarkDTO, expireTime);
                            }

                        }
                    }
                    redisUtil.hset(key, mode, newList, expireTime);
                }
            }
        }
    }

    @Override
    public Result getAllbookmark(GetallBookmarkReq req) {
        Result result = new Result();
        String userid = req.getUserid();
        int count = req.getCount() == 0? 100: req.getCount(); //默认请求100个
        int start = req.getStart() == 0? 0: req.getStart();
        String selection = req.getSelection();
        //模式未传入值时默认为标准模式
        String mode = StringUtils.isEmpty(req.getMode()) ? "1": req.getMode();
        String order = StringUtils.isEmpty(req.getOrder()) ? "1":req.getOrder();//1默认倒序时间最新的排前面
        String key = UcsConstants.BOOKMARK_REDIS_PREFIX+ userid;
        Map<Object, Object> map = redisUtil.hmget(key);
        //记录符合部分条件的数据
        List<SetBookmarkDTO> newList = new ArrayList<>();
        Map<String, String> mcMap = new HashMap<>(); //key记录mc2,value记录mc_createtime
        if(!CollectionUtils.isEmpty(map)) {
            for (Map.Entry<Object, Object> object : map.entrySet()) {
                String oldmode = (String) object.getKey();
                if(StringUtils.equals(mode, oldmode)) {
                    List<SetBookmarkDTO> reqList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), SetBookmarkDTO.class);
                    if (!CollectionUtils.isEmpty(reqList)) {
                        for(SetBookmarkDTO setBookmarkDTO:reqList) {
                            //先过滤出状态不为2且模式相同的并且部分符合条件的数据
                            if(!StringUtils.equals(setBookmarkDTO.getSt(), "2") && filterData(req, setBookmarkDTO)) {
                                newList.add(setBookmarkDTO);

                                String mc2 = setBookmarkDTO.getMc2();
                                String mc = setBookmarkDTO.getMc();
                                String createtime = setBookmarkDTO.getCreatetime();
                                String type = setBookmarkDTO.getType();
                                //为剧头时记录下谁是最后一集的书签
                                if(StringUtils.isNotEmpty(mc2) && StringUtils.equals(type, "s")) {
                                    String mc_createtime = mcMap.get(mc2);
                                    if(StringUtils.isNotEmpty(mc_createtime)) {
                                        long pastcreatTime = Long.parseLong(mc_createtime.split("_")[1]);
                                        if(pastcreatTime < Long.parseLong(createtime))  mcMap.put(mc2, mc + "_" + createtime);
                                    } else {
                                        mcMap.put(mc2, mc + "_" + createtime);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        int actualNum = 0;
        List<GetallBookmarkDTO> subjectList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(newList)) {
            int length = newList.size();
            //默认倒序 start是偏移量，count 请求个数
            if(StringUtils.equals(order, "1")) {
                for(int i = length - 1 - start; i >= 0; i--) {
                    if(actualNum >= count) break;

                    SetBookmarkDTO setBookmarkDTO = newList.get(i);
                    String mc2 = setBookmarkDTO.getMc2();
                    String mc = setBookmarkDTO.getMc();
                    if(StringUtils.equals(selection, "1") && StringUtils.isNotEmpty(mc2)) {
                        String pastMc = mcMap.get(mc2).split("_")[0];
                        if(StringUtils.equals(pastMc, mc)) {
                            GetallBookmarkDTO getallBookmarkDTO = new GetallBookmarkDTO();
                            getallBookmarkDTO.setUserid(userid);
                            BeanUtils.copyProperties(setBookmarkDTO, getallBookmarkDTO);
                            subjectList.add(getallBookmarkDTO);
                            actualNum = actualNum + 1;
                        }
                    } else {
                        GetallBookmarkDTO getallBookmarkDTO = new GetallBookmarkDTO();
                        getallBookmarkDTO.setUserid(userid);
                        BeanUtils.copyProperties(setBookmarkDTO, getallBookmarkDTO);
                        subjectList.add(getallBookmarkDTO);
                        actualNum = actualNum + 1;
                    }
                }
            } else {
                //顺序进行排序
                for(int i = 0 + start ;i < length; i++) {
                    SetBookmarkDTO setBookmarkDTO = newList.get(i);
                    String mc2 = setBookmarkDTO.getMc2();
                    String mc = setBookmarkDTO.getMc();
                    if(actualNum >= count) break;
                    if(StringUtils.equals(selection, "1") && StringUtils.isNotEmpty(mc2)) {
                        String pastMc = mcMap.get(mc2).split("_")[0];
                        if(StringUtils.equals(pastMc, mc)) {
                            GetallBookmarkDTO getallBookmarkDTO = new GetallBookmarkDTO();
                            getallBookmarkDTO.setUserid(userid);
                            BeanUtils.copyProperties(setBookmarkDTO, getallBookmarkDTO);
                            subjectList.add(getallBookmarkDTO);
                            actualNum = actualNum + 1;
                        }
                    } else {
                        GetallBookmarkDTO getallBookmarkDTO = new GetallBookmarkDTO();
                        getallBookmarkDTO.setUserid(userid);
                        BeanUtils.copyProperties(setBookmarkDTO, getallBookmarkDTO);
                        subjectList.add(getallBookmarkDTO);
                        actualNum = actualNum + 1;
                    }
                }
            }
        }
        result.setCount(req.getCount());//用户请求的个数
        result.setStart(start);//偏移量开始位置
        result.setTotal(actualNum); //实际总数
        result.setSubject(subjectList);
        result.setSystemtotal(commonConfig.getBookmarktotal()); //系统配置总数
        return result;
    }

    /**
     *  req 请求时的参数
     *  setBookmarkDTO 需要判断的对象
     */

    public Boolean filterData(GetallBookmarkReq req,SetBookmarkDTO setBookmarkDTO) {
        //判断所有的条件，如果有一个为false，则为false
        List<Boolean> flags = new ArrayList<>();
        String type = req.getType();
        String begintime = req.getBegintime();
        String endtime = req.getEndtime();
        int count = req.getCount() == 0? 100: req.getCount(); //默认请求100个
        int start = req.getStart() == 0? 0: req.getStart();
        String selection = req.getSelection();
        if(StringUtils.isNotEmpty(begintime)) {
            if(Long.valueOf(begintime) <= Long.valueOf(setBookmarkDTO.getCreatetime())) flags.add(true);
            else flags.add(false);
        }
        if(StringUtils.isNotEmpty(endtime)) {
            if( Long.valueOf(endtime) >= Long.valueOf(setBookmarkDTO.getCreatetime())) flags.add(true);
            else flags.add(false);
        }
        if(StringUtils.isNotEmpty(type)) {
            if(StringUtils.equals(type, setBookmarkDTO.getType())) flags.add(true);
            else flags.add(false);
        }
        //为剧集时该字段不能为空
        if(StringUtils.isNotEmpty(selection)) {
            if(StringUtils.isNotEmpty(setBookmarkDTO.getMc2())) flags.add(true);
            else flags.add(false);
        }

        if(CollectionUtils.isEmpty(flags)) {
            return true;
        } else {
            return !flags.contains(false);
        }
    }
}
