package com.utstar.ucs.service;

import com.utstar.ucs.req.SetReserveReq;
import com.utstar.ucs.resp.GetReserve;
import com.utstar.ucs.resp.Result;


public interface ReserveService {

    void setReserve(SetReserveReq reserve) throws Exception;

    GetReserve getReserve(GetReserve reserve) throws Exception;

    Result getAllReserve(GetReserve reserve);

    void clearReserve(GetReserve reserve);
}
