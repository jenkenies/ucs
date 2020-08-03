package com.utstar.ucs.service;

import com.utstar.ucs.req.DelBookmarkReq;
import com.utstar.ucs.req.GetBookmarkReq;
import com.utstar.ucs.req.GetallBookmarkReq;
import com.utstar.ucs.req.SetBookmarkReq;
import com.utstar.ucs.resp.GetBookmarkResp;
import com.utstar.ucs.resp.Result;

//书签接口
public interface BookmarkService {

    boolean setBookmark(SetBookmarkReq setBookmarkReq) throws Exception;

    GetBookmarkResp getbookmark(GetBookmarkReq getBookmarkReq) throws Exception;

    boolean querybookmarkstatus(String userid) throws Exception;

    void clearbookmark(String userid) throws Exception;

    void delbookmark(DelBookmarkReq request);

    Result getAllbookmark(GetallBookmarkReq req);
}
