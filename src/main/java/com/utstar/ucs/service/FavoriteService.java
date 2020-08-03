package com.utstar.ucs.service;

import com.utstar.ucs.req.Favorite;
import com.utstar.ucs.req.Request;
import com.utstar.ucs.req.hot.CleanHotReq;
import com.utstar.ucs.req.hot.DelHotReq;
import com.utstar.ucs.resp.GetFavoriteReq;
import com.utstar.ucs.resp.Result;

import java.text.ParseException;


public interface FavoriteService {
    void setfavorite(Favorite favorite);

    Result getallfavorite(GetFavoriteReq getFavorite) throws ParseException;

    void clearfavorite(CleanHotReq hotReq);

    Favorite getfavorite(Favorite favorite);

    boolean queryfavoritestatus(Request request);

    void delfavorite(DelHotReq delHotReq);
}
