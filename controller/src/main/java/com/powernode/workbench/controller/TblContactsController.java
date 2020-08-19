package com.powernode.workbench.controller;

import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.service.TblContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("contacts")
public class TblContactsController {
    @Autowired
    private TblContactsService service;
    //分页查询
    @RequestMapping("/page")
    public Result page(@RequestParam(required = true,defaultValue = "1") String pageNum,
                       @RequestParam(required = true,defaultValue = "1") String pageSize,
                       @RequestParam(required =false)String owner,
                       @RequestParam(required =false)String name,
                       @RequestParam(required =false)String cusname,
                       @RequestParam(required =false)String bith,
                       @RequestParam(required =false)String source
                       ){
        PageResult pageResult = service.pageList(pageNum, pageSize, owner, name, cusname, bith, source);
        return Result.OK(pageResult);
    }
//保存创建
    @RequestMapping("/save")
    public Result saveContact(TblContacts contacts, HttpServletRequest request){
        TblUser user = (TblUser) request.getSession().getAttribute("USER");
        String name = user.getName();
        service.save(contacts,name);
        return Result.OK();

    }
    //修改按钮弹出来
    @RequestMapping("/updatem")
    public Result updateM(String id){
        TblContacts tblContacts = service.updateM(id);
        return Result.OK(tblContacts);

    }
    //修改客户信息
    @RequestMapping("/update")
    public Result updateC(TblContacts contacts, HttpServletRequest request){
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        String editBy = user.getName();
        contacts.setEditby(editBy);
        service.update(contacts);
        return Result.OK();
    }
    //删除客户
    @RequestMapping("/del")
    public Result del(@RequestParam("its[]")String []its){
        service.delC(its);
        return Result.OK();

    }
    //查找单个客户
    @RequestMapping("/select")
    public Result selectC(String id){
        TblContacts selece = service.selece(id);
        return Result.OK(selece);


    }
}
