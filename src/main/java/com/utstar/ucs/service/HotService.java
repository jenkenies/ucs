package com.utstar.ucs.service;


import com.utstar.ucs.req.hot.CleanHotReq;
import com.utstar.ucs.req.hot.DelHotReq;
import com.utstar.ucs.req.hot.GetHotReq;
import com.utstar.ucs.req.hot.GetallHotReq;
import com.utstar.ucs.req.hot.SetHotReq;
import com.utstar.ucs.resp.HotResp;
import com.utstar.ucs.resp.Result;

public interface HotService {
	
    boolean setHot(SetHotReq setHotReq ) throws Exception;

    HotResp getHot(GetHotReq getHotReq) throws Exception;
    
    Result getAllHot(GetallHotReq getallHotReq) throws Exception;

	void delHot(DelHotReq delHotReq) throws Exception;
	
	void cleanHot(CleanHotReq cleanHotReq) throws Exception;

	boolean queryhotstatus(String userid) throws Exception;


}
