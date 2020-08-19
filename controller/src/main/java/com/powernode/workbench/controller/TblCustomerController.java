package com.powernode.workbench.controller;

import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblCustomer;
import com.powernode.workbench.service.TblCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("customer")
public class TblCustomerController {
    //分页查询
    @Autowired
    private TblCustomerService service;
    @RequestMapping("/get")
    public Result get(@RequestParam(value = "pageNum",required = true,defaultValue = "1") int pageNum,
                      @RequestParam(value = "pageSize",required = true) int pageSize,
                      @RequestParam(required =false) String owner,
                      @RequestParam(required =false) String name,
                      @RequestParam(required =false) String phone,
                      @RequestParam(required =false) String web

    ){
        PageResult list = service.getList(pageNum, pageSize, owner, name, phone, web);
        return Result.OK(list);
    }
    //添加客户
    @RequestMapping("/add")
    public Result addCustom(TblCustomer customer, HttpServletRequest request){
        TblUser user = (TblUser) request.getSession().getAttribute("USER");
        String name = user.getName();
        customer.setCreateby(name);
        service.add(customer);
        return Result.OK();

    }
    //修改客户
    @RequestMapping("/update")
    public Result update (TblCustomer customer, HttpServletRequest request){
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        String createBy = user.getName();
        service.update(customer,createBy);
        return Result.OK();
    }
    //修改客户弹窗
    @RequestMapping("/dos")
    public Result dos(String id){
        TblCustomer dos = service.dos(id);
        return Result.OK(dos);
    }
    //删除客户
    @RequestMapping("/del")
    public Result del(@RequestParam("its[]") String[] its){
        service.del(its);
        return Result.OK();

    }
    //跳转页面查询客户

   @RequestMapping("/select")
    public Result select(String id){
       TblCustomer tblCustomer = service.select1(id);
       return Result.OK(tblCustomer);
   }
   //获取客户备注
    @RequestMapping("/remark")
    public Result getRemark(String id){
        Map<String, Object> getremark = service.getremark(id);
        return Result.OK(getremark);
    }
    //删除备注
    @RequestMapping("/delx")
    public Result delX(String tid){
        service.del(tid);
        return Result.OK();
    }
    //添加备注
    @RequestMapping("/notes")
    public Result note(String id, String content, HttpServletRequest request){
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        String name = user.getId();
        service.addnote(id,content,name);
        return Result.OK();
    }
    //查询交易
    @RequestMapping("/transaction")
    public Result getJY(String id, HttpServletRequest request){
        Map map2 = (Map)request.getSession().getServletContext().getAttribute("map2");
        Map<String, Object> tran = service.getTran(id);
        tran.put("KN",map2);
        return Result.OK(tran);
    }
    //删除交易
    @RequestMapping("/deljy")
    public Result deljy(String id){
        service.delJY(id);
        return Result.OK();
    }
    //查找所有的联系人
    @RequestMapping("/contacts")
    public Result getcontacts(String id){
        List<TblContacts> lx = service.getLx(id);
        return Result.OK(lx);
    }

}
