package com.powernode.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.PageResult;
import com.powernode.util.UUIDUtil;
import com.powernode.workbench.mapper.TblContactsActivityRelationMapper;
import com.powernode.workbench.mapper.TblContactsMapper;
import com.powernode.workbench.mapper.TblContactsRemarkMapper;
import com.powernode.workbench.mapper.TblCustomerMapper;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblContactsServiceImpl implements TblContactsService {
    @Autowired
    private TblUserMapper userMapper;
    @Autowired
    private TblCustomerMapper customerMapper;
    @Autowired
    private TblContactsMapper contactsMapper;
    @Autowired
    private TblContactsRemarkMapper contactsRemarkMapper;
    @Autowired
    private TblContactsActivityRelationMapper tblContactsActivityRelationMapper;
    //分页查询
    @Override
    public PageResult pageList(String pageNum, String pageSize, String owner, String name, String cusname, String bith, String source) {
        int pn = Integer.parseInt(pageNum);
        int ps = Integer.parseInt(pageSize);

        TblContactsExample tblContactsExample = new TblContactsExample();
        TblContactsExample.Criteria criteria = tblContactsExample.createCriteria();
        if (owner !=null && !"".equals(owner)){
            TblUserExample userExample = new TblUserExample();
            TblUserExample.Criteria criteria2 = userExample.createCriteria();
            criteria2.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers1 = userMapper.selectByExample(userExample);
            for (TblUser tblUser : tblUsers1) {
                String id = tblUser.getId();
                criteria.andOwnerLike("%"+id+"%");
            }
        }
        if (name != null && !"".equals(name)){
            criteria.andFullnameLike("%"+name+"%");

        }
        if (cusname != null && !"".equals(cusname)){
            TblCustomerExample tblCustomerExample = new TblCustomerExample();
            TblCustomerExample.Criteria criteria1 = tblCustomerExample.createCriteria();
            criteria1.andNameLike("%"+cusname+"%");
            List<TblCustomer> tblCustomers = customerMapper.selectByExample(tblCustomerExample);
            if (tblCustomers!=null &&tblCustomers.size()!=0){
                String id = tblCustomers.get(0).getId();
                criteria.andCustomeridLike("%"+id+"%");
            }


        }
        if (source != null && !"".equals(source)){
            criteria.andSourceLike("%"+source+"%");
        }
        if (bith != null && !"".equals(bith)){
            criteria.andBirthLike("%"+bith+"%");
        }

        PageHelper.startPage(pn,ps);
        List<TblContacts> tblContacts = contactsMapper.selectByExample(tblContactsExample);
        if (tblContacts ==null || tblContacts.size()==0){
            throw new ResultException("查询为空");
        }
        for (TblContacts tblContact : tblContacts) {
            String owner1 = tblContact.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria1 = tblUserExample.createCriteria();
            criteria1.andIdEqualTo(owner1);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            String name1 = tblUsers.get(0).getName();
            tblContact.setOwner(name1);
            String customerid = tblContact.getCustomerid();
            TblCustomer tblCustomer = customerMapper.selectByPrimaryKey(customerid);
            if (tblCustomer!=null){
                String name2 = tblCustomer.getName();
                tblContact.setCustomerid(name2);
            }
        }
        PageInfo<TblContacts> pageInfo = new PageInfo<>(tblContacts);
        List<TblContacts> list = pageInfo.getList();
        long total = pageInfo.getTotal();
        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }
    //创建联系人
    @Override
    public void save(TblContacts contacts, String createBy) {
        String uuid = UUIDUtil.getUUID();
        String sysTime = DateTimeUtil.getSysTime();
        contacts.setCreateby(createBy);
        contacts.setCreatetime(sysTime);
        contacts.setId(uuid);
        if (contacts!=null){
            String owner = contacts.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            tblUserExample.createCriteria().andNameEqualTo(owner);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            if (tblUsers!=null &&tblUsers.size()!=0){
                TblUser tblUser = tblUsers.get(0);
                String id = tblUser.getId();
                contacts.setOwner(id);
            }
            try {
                contactsMapper.insertSelective(contacts);
            } catch (Exception e) {
                throw new ResultException("添加失败");
            }

        }
    }
    //修改联系人
    @Override
    public TblContacts updateM(String id) {
        if (id!=null &&!"".equals(id)){
            TblContacts tblContacts = contactsMapper.selectByPrimaryKey(id);
            return tblContacts;
        }else {
            throw new ResultException("查询为空");
        }

    }
    //修改客户
    @Override
    public void update(TblContacts contacts) {
        String sysTime = DateTimeUtil.getSysTime();
        if (contacts!=null){
            contacts.setEdittime(sysTime);
            try {
                contactsMapper.updateByPrimaryKeySelective(contacts);
            } catch (Exception e) {
                throw new ResultException("修改异常");
            }
        }
    }
    //删除客户
    @Override
    public void delC(String[] its) {
        if (its!=null &&its.length!=0){
            for (String it : its) {
                TblContactsRemarkExample tblContactsRemarkExample = new TblContactsRemarkExample();
                tblContactsRemarkExample.createCriteria().andContactsidEqualTo(it);
                contactsRemarkMapper.deleteByExample(tblContactsRemarkExample);
                TblContactsActivityRelationExample tblContactsActivityRelationExample = new TblContactsActivityRelationExample();
                tblContactsActivityRelationExample.createCriteria().andContactsidEqualTo(it);
                tblContactsActivityRelationMapper.deleteByExample(tblContactsActivityRelationExample);
                contactsMapper.deleteByPrimaryKey(it);
            }
        }
    }
    //查找客户
    @Override
    public TblContacts selece(String id) {
        if (id!=null && !"".equals(id)){
            TblContacts tblContacts = contactsMapper.selectByPrimaryKey(id);
            String customerid = tblContacts.getCustomerid();
            TblCustomer tblCustomer = customerMapper.selectByPrimaryKey(customerid);
            if (tblCustomer!=null){
                String name = tblCustomer.getName();
                tblContacts.setCustomerid(name);
            }
            return tblContacts;
        }else {
            throw new ResultException("没有这个客户");
        }

    }
}
