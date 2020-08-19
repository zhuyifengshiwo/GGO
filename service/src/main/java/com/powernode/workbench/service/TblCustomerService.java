package com.powernode.workbench.service;

import com.powernode.util.PageResult;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblCustomer;

import java.util.List;
import java.util.Map;

public interface TblCustomerService {
    //分页查询
    PageResult getList(int pageNum, int pageSize, String owner, String name, String phone, String web);
    //c创建客户
    void add(TblCustomer customer);
    //修改客户
    void update(TblCustomer tblCustomer, String createBy);
    //修改客户弹窗
    TblCustomer dos(String id);
    //删除客户
    void del(String[] its);
    //查询单个客户
    TblCustomer select1(String id);
    //获取客户备注
    Map<String,Object> getremark(String id);
    //删除备注
    void del(String id);
    //添加备注
    void addnote(String id, String content, String creatby);
    //获取交易
    Map<String,Object>  getTran(String id);
    //删除交易
    void delJY(String id);
    //获取联系人
    List<TblContacts> getLx(String id);
}
