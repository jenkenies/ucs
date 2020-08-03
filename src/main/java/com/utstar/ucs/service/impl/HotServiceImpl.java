package com.utstar.ucs.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.utstar.ucs.conf.CommonConfig;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.req.hot.CleanHotReq;
import com.utstar.ucs.req.hot.DelHotReq;
import com.utstar.ucs.req.hot.GetHotReq;
import com.utstar.ucs.req.hot.GetallHotReq;
import com.utstar.ucs.req.HotDTO;
import com.utstar.ucs.req.hot.SetHotReq;
import com.utstar.ucs.resp.HotResp;
import com.utstar.ucs.resp.Result;
import com.utstar.ucs.service.HotService;
import com.utstar.ucs.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "hotService")
public class HotServiceImpl implements HotService {
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private CommonConfig commonConfig;
	
	// 新增追剧
	@Override
	public boolean setHot(SetHotReq setHotReq) throws Exception {
		// TODO Auto-generated method stub
		int hotToal = commonConfig.getHottotal();
		String userid = setHotReq.getUserid();
		String mode = StringUtils.isEmpty(setHotReq.getMode())?"1":setHotReq.getMode();
		String key =UcsConstants.HOT_REDIS_PREFIX+userid;
		Map<String,Object> newmap= new HashMap();
		Map<Object,Object> oldmap =redisUtil.hmget(key);
		List<HotDTO> reqList = new ArrayList<>();
		String createtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		 //若该用户不存在历史数据，则直接新增一条
		if (CollectionUtils.isEmpty(oldmap)) {
			HotDTO req = new HotDTO();
			BeanUtils.copyProperties(setHotReq, req);
			req.setCreatetime(createtime);
			req.setSt("1");
			reqList.add(req);
			newmap.put(mode, reqList);
			
		}else{
            //判断书签的模式是否相同，不相同时则默认把其他模式的数据的加入到集合
            //相同时则需判断媒资的code是否一样，如果一样，则只需更新时间，若不一致，则需要加入到集合
            boolean modeflag = false;
            for(Map.Entry<Object, Object> object: oldmap.entrySet()) {
                String pastMode = (String) object.getKey();
                List<HotDTO> pastReqList = JSONArray.parseArray(JSON.toJSONString(object.getValue()), HotDTO.class);
                //若为之前已有的模式
                if(StringUtils.equals(pastMode, mode)) {
                    if(!CollectionUtils.isEmpty(pastReqList)) {

                        for(HotDTO pastreq:pastReqList) {
                            //修改后需要更新,且插入到队列头
                            if(!StringUtils.equals(pastreq.getMc(), setHotReq.getMc())) {
                            	HotDTO req = new HotDTO();
                            	BeanUtils.copyProperties(pastreq, req);
                            	req.setSt("1");
                                reqList.add(req);
                            }
                        }
                        //新增与修改的均要放到队列头
                        HotDTO req = new HotDTO();
                        BeanUtils.copyProperties(setHotReq, req);
                        req.setCreatetime(createtime);
                        req.setSt("1");
                        reqList.add(req);

                        //若追剧的数量超过指定的数量，则删掉队列历史数据
                        if(reqList.size() > hotToal) { reqList.remove(0);}
                    }
                    newmap.put(mode, reqList);
                    modeflag = true;
                }
            }
            //判断是否为新模式,新模式下需要新增
            if(!modeflag) {
            	HotDTO req = new HotDTO();
                BeanUtils.copyProperties(setHotReq, req);
                req.setCreatetime(createtime);
                req.setSt("1");
                reqList.add(req);
                newmap.put(mode, reqList);
            }
		}
		  long expireTime = commonConfig.getExpiretime() * 60 * 60 * 24 * 30;
	        //最后统一把结果保存到redis
	      redisUtil.hmset(key, newmap, expireTime);

		
		return true;
	}
	//http://api.utstarcom.cn/ucs/gethot?userid=<string>&type=<string>&mc=<string>
	//查询单个追剧
	@Override
	public HotResp getHot(GetHotReq getHotReq) throws Exception {
		
		String key = UcsConstants.HOT_REDIS_PREFIX+getHotReq.getUserid();
		String mode = StringUtils.isEmpty(getHotReq.getMode())?"1":getHotReq.getMode();
		Object object =  redisUtil.hget(key, mode);
        List<HotDTO> list = JSONArray.parseArray(JSON.toJSONString(object), HotDTO.class);
    	HotResp resp = null;
        for (HotDTO hotDTO : list) {
        	if(hotDTO != null && !StringUtils.equals(hotDTO.getSt(), "2") 
        			&& StringUtils.equals(hotDTO.getMc(), getHotReq.getMc()) 
    				&& getHotReq.getType().contains(hotDTO.getType())) {
    			    resp = new HotResp();	
    	            resp.setMc(hotDTO.getMc());
    	            resp.setType(hotDTO.getType());
    	            resp.setCurrentnum(hotDTO.getCurrentnum());
             }
		}
	     return resp;
	}
	//查询所有追剧
	@Override
	public Result getAllHot(GetallHotReq req) throws Exception {
		Result result = new Result();
		String key = UcsConstants.HOT_REDIS_PREFIX+req.getUserid();
		String mode = StringUtils.isEmpty(req.getMode())?"1":req.getMode();
        int start = req.getStart() == 0? 0: req.getStart();
        int count = req.getCount() == 0? 100: req.getCount(); //默认请求100个
        int fromIndex = req.getStart();
        int toIndex = req.getStart() + req.getCount()-1;  
        String order = StringUtils.isEmpty(req.getOrder()) ? "1":"2";//1默认倒序时间最新的排前面        
		Object object =  redisUtil.hget(key, mode);
        List<HotDTO> list = JSONArray.parseArray(JSON.toJSONString(object), HotDTO.class);
        List<HotDTO> resultlist = new ArrayList<>();
        String[] typelist =req.getTypelist().split(",");     
        if(StringUtils.isNotBlank(req.getBegintime()) && StringUtils.isNotBlank(req.getEndtime())){
    		if(Long.valueOf(req.getBegintime()) <= Long.valueOf(req.getEndtime())){
    			resultlist = list.stream().filter(hotDTO ->Arrays.asList(typelist).contains(hotDTO.getType())
      				&& !"2".equals(hotDTO.getSt()) &&  Long.valueOf(hotDTO.getCreatetime()) >= Long.valueOf(req.getBegintime()) &&
      				 Long.valueOf(hotDTO.getCreatetime()) <= Long.valueOf(req.getEndtime()))
        			.collect(Collectors.toList());
    		}
  	    }else if(StringUtils.isNotBlank(req.getBegintime())){
  	    	resultlist = list.stream().filter(hotDTO ->Arrays.asList(typelist).contains(hotDTO.getType())
      				&& !"2".equals(hotDTO.getSt()) &&  Long.valueOf(hotDTO.getCreatetime()) >= Long.valueOf(req.getBegintime()) )
        			.collect(Collectors.toList());
        }else if((StringUtils.isNotBlank(req.getEndtime()))){
        	resultlist = list.stream().filter(hotDTO ->Arrays.asList(typelist).contains(hotDTO.getType())
      				&& !"2".equals(hotDTO.getSt()) && Long.valueOf(hotDTO.getCreatetime()) <= Long.valueOf(req.getEndtime()) )
        			.collect(Collectors.toList());	
        }else{
        	 resultlist =list;
        }
              
        if (!CollectionUtils.isEmpty(resultlist)) {
        	  if(StringUtils.equals(order, "1")) {//倒序
        		  resultlist =  resultlist.stream().sorted(Comparator.comparing(HotDTO::getCreatetime).reversed())
        		  .collect(Collectors.toList());
        	  }else{
        		  resultlist = resultlist.stream().sorted(Comparator.comparing(HotDTO::getCreatetime))
            		  .collect(Collectors.toList());
        	  }
		}
        List<HotDTO> subjectlist = null;
        if(fromIndex > resultlist.size()||resultlist.size()==0){
        	subjectlist = new ArrayList<>();
        }else if(toIndex< resultlist.size()){
        	subjectlist =resultlist.subList(fromIndex, toIndex);
        }else{
        	subjectlist =resultlist.subList(fromIndex,resultlist.size());
        }
        result.setStart(start);
        result.setCount(count);
        result.setTotal(subjectlist.size());
        result.setSystemtotal(commonConfig.getHottotal());
        result.setSubject(subjectlist);       
		return result;
	}
	
	private boolean filterData(GetallHotReq req, HotDTO hotDTO) {
        //判断所有的条件，如果有一个为false，则为false
        List<Boolean> flags = new ArrayList<>();
        List <String> typelist =Arrays.asList(req.getTypelist().split(","));
        String begintime = req.getBegintime();
        String endtime = req.getEndtime();
        if(StringUtils.isNotEmpty(begintime)) {
            if(Long.valueOf(begintime) <= Long.valueOf(hotDTO.getCreatetime())) flags.add(true);
            else flags.add(false);
        }
        if(StringUtils.isNotEmpty(endtime)) {
            if( Long.valueOf(endtime) >= Long.valueOf(hotDTO.getCreatetime())) flags.add(true);
            else flags.add(false);
        }
        if(!CollectionUtils.isEmpty(typelist)) {
            if(typelist.contains(hotDTO.getType())) flags.add(true);
            else flags.add(false);
        }
        if(CollectionUtils.isEmpty(flags)) {
            return true;
        } else {
            return !flags.contains(false);
        }
	}
	
	/*http://api.utstarcom.cn/ucs/delhot?userid=075526593379
		&mclist=02000000000000012016042999941830,02000002000000012016072199332981,02000000000000012016042999941830
		&typelist=p,p,s*/
	//删除追剧
	@Override
	public void delHot(DelHotReq delHotReq) {
		
		String key = UcsConstants.HOT_REDIS_PREFIX+delHotReq.getUserid();
		String mode = StringUtils.isEmpty(delHotReq.getMode())?"1":delHotReq.getMode();
		Object object =  redisUtil.hget(key, mode);
	    List<HotDTO> list = JSONArray.parseArray(JSON.toJSONString(object), HotDTO.class);
	    long expireTime = 1 * 60 * 60 * 24 * 30;
        List<HotDTO> newList = new ArrayList<>();
        HotDTO req = new HotDTO();
        String[] mclist =delHotReq.getMclist().split(",");
        String[] typelist =delHotReq.getTypelist().split(",");
        list.stream().map(hotDTO ->{
        	for (int i=0;i<mclist.length;i++) {
        		if (hotDTO.getMc().equals(mclist[i]) && hotDTO.getType().equals(typelist[i])) {
            		hotDTO.setSt("2");            		
    			}        		
			}
        	newList.add(hotDTO);
        	return hotDTO;
        }).collect(Collectors.toList());
        redisUtil.hset(key, mode, newList,expireTime);
        
	}
	//清空追剧
	@Override
	public void cleanHot(CleanHotReq req) {
		
		String key = UcsConstants.HOT_REDIS_PREFIX+req.getUserid();
		String mode = StringUtils.isEmpty(req.getMode())?"1":req.getMode();

		Object object =  redisUtil.hget(key, mode);
	    List<HotDTO> list = JSONArray.parseArray(JSON.toJSONString(object), HotDTO.class);
        List<HotDTO> newList = new ArrayList<>();
		String[] typelist =req.getTypelist().split(",");
        list.stream().map(hotDTO ->{
        		if (Arrays.asList(typelist).contains(hotDTO.getType())) {
        			hotDTO.setSt("2");        			
    			}
        		newList.add(hotDTO);
        	return hotDTO;
        }).collect(Collectors.toList());
        long expireTime = commonConfig.getExpiretime() * 60 * 60 * 24 * 30;
        //最后统一把结果保存到redis
        redisUtil.hset(key, mode,newList, expireTime);
        	
	}
	
	//查询追剧状态
	@Override
	public boolean queryhotstatus(String userid) {
		// TODO Auto-generated method stub
	  boolean flag = false;
        String key = UcsConstants.HOT_REDIS_PREFIX+userid;
        if(redisUtil.hasKey(key)) {
        	flag = true;
        } else {
        	flag = false;
        }
        return flag;
    }
	
	

}
