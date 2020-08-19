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
import com.powernode.workbench.mapper.*;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TblCustomerServiceImpl implements TblCustomerService {
    @Autowired
    private TblUserMapper userMapper;
    @Autowired
    private TblCustomerMapper mapper;
    @Autowired
    private TblCustomerRemarkMapper remarkMapper;
    @Autowired
    private TblTranMapper tranMapper;
    @Autowired
    private TblTranHistoryMapper historyMapper;
    @Autowired
    private TblTranRemarkMapper tranRemarkMapper;
    @Autowired
    private TblContactsMapper contactsMapper;
    //分页查询
    @Override
    public PageResult getList(int pageNum, int pageSize, String owner, String name, String phone, String web) {
        TblCustomerExample tblCustomerExample = new TblCustomerExample();
        TblCustomerExample.Criteria criteria = tblCustomerExample.createCriteria();
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
            criteria.andNameLike("%"+name+"%");
        }
        if (phone != null && !"".equals(phone)){
            criteria.andPhoneLike("%"+phone+"%");
        }
        if (web != null && !"".equals(web)){
            criteria.andWebsiteLike("%"+web+"%");
        }
        PageHelper.startPage(pageNum,pageSize);
        List<TblCustomer> tblCustomers = mapper.selectByExample(tblCustomerExample);
        if (tblCustomers ==null || tblCustomers.size()==0){
            throw new ResultException("查询为空");
        }

        for (TblCustomer tblCustomer : tblCustomers) {
            String owner1 = tblCustomer.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria1 = tblUserExample.createCriteria();
            criteria1.andIdEqualTo(owner1);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            String name1 = tblUsers.get(0).getName();
            tblCustomer.setOwner(name1);
        }
        PageInfo<TblCustomer> PageInfo = new PageInfo<>(tblCustomers);
        List<TblCustomer> list = PageInfo.getList();
        long total = PageInfo.getTotal();
        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }
    //添加客户
    @Override
    public void add(TblCustomer customer) {
        if (customer!=null){
            String uuid = UUIDUtil.getUUID();
            String sysTime = DateTimeUtil.getSysTime();
            customer.setId(uuid);
            customer.setCreatetime(sysTime);
            String owner = customer.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            tblUserExample.createCriteria().andNameEqualTo(owner);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            if (tblUsers!=null && tblUsers.size()!=0){
                String id = tblUsers.get(0).getId();
                customer.setOwner(id);
            }
            try {
                mapper.insertSelective(customer);
            } catch (Exception e) {
                throw new ResultException("创建失败");
            }
        }
    }
//修改客户
    @Override
    public void update(TblCustomer tblCustomer, String createBy) {
        String sysTime = DateTimeUtil.getSysTime();
        tblCustomer.setEditby(createBy);
        tblCustomer.setEdittime(sysTime);
        if (tblCustomer!=null){
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria = tblUserExample.createCriteria();
            criteria.andNameEqualTo(tblCustomer.getOwner());
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            if (tblUsers!=null&&tblUsers.size()!=0){
                TblUser tblUser = tblUsers.get(0);
                String id = tblUser.getId();
                tblCustomer.setOwner(id);
            }
            try {
                mapper.updateByPrimaryKeySelective(tblCustomer);
            } catch (Exception e) {
                throw new ResultException("修改失败");
            }
        }
    }
//修改框弹出来
    @Override
    public TblCustomer dos(String id) {
        TblCustomer tblCustomer=null;
        if (id!=null){
             tblCustomer = mapper.selectByPrimaryKey(id);
        }
        return tblCustomer;
    }

    @Override
    public void del(String []its) {
        if (its!=null && its.length!=0){
            for (String it : its) {
                TblCustomerRemarkExample tblCustomerRemarkExample = new TblCustomerRemarkExample();
                TblCustomerRemarkExample.Criteria criteria = tblCustomerRemarkExample.createCriteria();
                criteria.andCustomeridEqualTo(it);
                List<TblCustomerRemark> tblCustomerRemarks = remarkMapper.selectByExample(tblCustomerRemarkExample);
                if (tblCustomerRemarks!=null && tblCustomerRemarks.size()!=0){
                    for (TblCustomerRemark tblCustomerRemark : tblCustomerRemarks) {
                        String id = tblCustomerRemark.getId();
                        remarkMapper.deleteByPrimaryKey(id);
                    }
                }
                try {
                    mapper.deleteByPrimaryKey(it);
                } catch (Exception e) {
                    throw new ResultException("删除失败");
                }
            }
        }
    }

    @Override
    public TblCustomer select1(String id) {
        try {
            TblCustomer tblCustomer = mapper.selectByPrimaryKey(id);
            return tblCustomer;
        } catch (Exception e) {
            throw new ResultException("用户不存在");
        }
    }
    //查询备注
    @Override
    public Map<String,Object> getremark(String id) {
        Map<String,Object> map=new HashMap<>();

        TblCustomerRemarkExample tblCustomerRemarkExample = new TblCustomerRemarkExample();
        TblCustomerRemarkExample.Criteria criteria = tblCustomerRemarkExample.createCriteria();
        if (id!=null && !"".equals(id)) {
            TblCustomer tblCustomer = mapper.selectByPrimaryKey(id);
            criteria.andCustomeridEqualTo(id);
            List<TblCustomerRemark> tblCustomerRemarks = remarkMapper.selectByExample(tblCustomerRemarkExample);
            if (tblCustomerRemarks ==null || tblCustomerRemarks.size()==0){
                throw new ResultException("查询为空");
            }else {
                for (TblCustomerRemark tblCustomerRemark : tblCustomerRemarks) {
                    if (tblCustomerRemark.getEditflag().equals("1")){
                        String editby = tblCustomerRemark.getEditby();
                        TblUser tblUser = userMapper.selectByPrimaryKey(editby);
                        String name = tblUser.getName();
                        tblCustomerRemark.setEditby(name);
                    }else{
                        String createby = tblCustomerRemark.getCreateby();
                        TblUser tblUser = userMapper.selectByPrimaryKey(createby);
                        String name = tblUser.getName();
                        tblCustomerRemark.setCreateby(name);
                    }
                }
                map.put("beizhu",tblCustomerRemarks);
                map.put("kehu",tblCustomer);
                return map;
            }
        }else {
            throw new ResultException("无法查寻");
        }

    }
   //删除备注
    @Override
    public void del(String id) {
        try {
            remarkMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //添加备注
    @Override
    public void addnote(String id, String content,String creatby) {
        TblCustomerRemark tblCustomerRemark = new TblCustomerRemark();
        if (id!=null && !"".equals(id)&&content!=null && !"".equals(content)&&creatby!=null && !"".equals(creatby)){
            tblCustomerRemark.setCreateby(creatby);
            tblCustomerRemark.setEditflag("0");
            tblCustomerRemark.setNotecontent(content);
            tblCustomerRemark.setId(UUIDUtil.getUUID());
            tblCustomerRemark.setCustomerid(id);
            tblCustomerRemark.setCreatetime(DateTimeUtil.getSysTime());
            remarkMapper.insertSelective(tblCustomerRemark);
        }else {
         throw new ResultException("添加失败");
        }



    }
    //获取交易
    @Override
    public Map<String,Object> getTran(String id) {
        Map<String,Object> map=new HashMap<>();
        if (id!=null && !"".equals(id)){
            TblTranExample tblTranExample = new TblTranExample();
            TblTranExample.Criteria criteria = tblTranExample.createCriteria();
            criteria.andCustomeridEqualTo(id);
            List<TblTran> tblTrans = tranMapper.selectByExample(tblTranExample);
            if (tblTrans!=null && tblTrans.size()!=0){
                map.put("jy",tblTrans);
            }else {
                throw new ResultException("没有数据");
            }
        }
        return map;
    }
    //删除交易
    @Override
    public void delJY(String id) {
        if (id!=null && !"".equals(id)){

                TblTran tblTran = tranMapper.selectByPrimaryKey(id);
                if (tblTran!=null){
                    TblTranHistoryExample tblTranHistoryExample = new TblTranHistoryExample();
                    TblTranHistoryExample.Criteria criteria = tblTranHistoryExample.createCriteria();
                    criteria.andTranidEqualTo(id);
                    List<TblTranHistory> tblTranHistories = historyMapper.selectByExample(tblTranHistoryExample);
                    if (tblTranHistories.size()!=0 && tblTranHistories!=null){
                        for (TblTranHistory tblTranHistory : tblTranHistories) {
                            String id1 = tblTranHistory.getId();
                            historyMapper.deleteByPrimaryKey(id1);
                        }
                    }
                    TblTranRemarkExample tblTranRemarkExample = new TblTranRemarkExample();
                    TblTranRemarkExample.Criteria criteria1 = tblTranRemarkExample.createCriteria();
                    criteria1.andTranidEqualTo(id);
                    List<TblTranRemark> tblTranRemarks = tranRemarkMapper.selectByExample(tblTranRemarkExample);
                    if (tblTranRemarks!=null && tblTranRemarks.size()!=0){
                        for (TblTranRemark tblTranRemark : tblTranRemarks) {
                            String id1 = tblTranRemark.getId();
                            tranRemarkMapper.deleteByPrimaryKey(id1);
                        }
                    }
                }else {
                    throw new ResultException("交易为空");
                }
                try {
                    tranMapper.deleteByPrimaryKey(id);
                } catch (Exception e) {
                    throw new ResultException("删除失败");
                }

        }
    }

    @Override
    public List<TblContacts> getLx(String id) {
        if (id!=null && !"".equals(id)){
            TblContactsExample tblContactsExample = new TblContactsExample();
            TblContactsExample.Criteria criteria = tblContactsExample.createCriteria();
            criteria.andCustomeridEqualTo(id);
            List<TblContacts> tblContacts = contactsMapper.selectByExample(tblContactsExample);
            if (tblContacts==null || tblContacts.size()==0){
                throw new ResultException("没有联系人");
            }
            return tblContacts;
        }else{
            throw new ResultException("查询联系人失败");
        }
    }


}
