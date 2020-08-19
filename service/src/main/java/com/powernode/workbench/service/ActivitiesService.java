package com.powernode.workbench.service;

import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.workbench.pojo.TblActivity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ActivitiesService {
    int add(TblActivity tblActivity, HttpServletRequest request);
    List<TblUser> getUser();
    PageResult getList(String pageNum, String pageSize, String owner, String name, String startdate, String enddate);
    //自己写的删除
    /*void delect (String id);*/
    void delete(String[] id);
    //更新框信息
    TblActivity update(String id);
    //
    void update1(TblActivity tblActivity);

    TblActivity actSelect(String id);

}
