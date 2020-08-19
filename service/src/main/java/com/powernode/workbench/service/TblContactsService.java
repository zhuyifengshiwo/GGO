package com.powernode.workbench.service;

import com.powernode.util.PageResult;
import com.powernode.workbench.pojo.TblContacts;

public interface TblContactsService {
    //分页查询
    PageResult pageList(String pageNum, String pageSize, String owner, String name, String cusname, String bith, String source);
    //创建联系人
    void save(TblContacts contacts, String createBy);
    //弹出修改窗口
    TblContacts updateM(String id);
    //修改客户
    void update(TblContacts contacts);
    //删除客户
    void delC(String[] its);
    //查找客户
    TblContacts selece(String id);
}
