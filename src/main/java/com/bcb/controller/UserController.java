package com.bcb.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bcb.domain.dao.UserAccountInfoDao;
import com.bcb.service.CommonService;
import com.bcb.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ButtonParam;
import com.bcb.bean.JSONParam;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.UserInfoResultDto;
import com.bcb.domain.repository.UserAccountInfoRepository;
import com.bcb.domain.repository.UserWalletInfoRepository;
import com.bcb.domain.entity.FinancingIncomeUnknown;
import com.bcb.domain.entity.SystemButtonMenu;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.domain.entity.SystemLoginMenu;
import com.bcb.util.MapUtil;
import com.bcb.util.Node;

@Slf4j
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private CommonService commonService;

    @Autowired
    private UserWalletInfoRepository userWalletInfoRepository;

    @Autowired
    private UserAccountInfoRepository userAccountInfoRepository;

    @Autowired
    UserService userService;

    /*
     * xujintao
     * 菜单栏从数据库动态获取数据 树状结构（该账号拥有菜单栏权限）
     * */
    @LoginRequired
    @RequestMapping(value = "/tableList",method = RequestMethod.GET)
    public ResponseResult tableList(HttpServletRequest request){
        try{
            SystemLoginAccount systemLoginAccount =(SystemLoginAccount) request.getAttribute("account");
            List<SystemLoginMenu> list =  commonService.queryMenuList(systemLoginAccount.getId());
            Node tree = getTreeJson(list);//获得一棵树模型的数据
            return new ResponseResult("ok","200", tree,"请求成功") ;
        }catch (Exception e){
            log.error("菜单栏从数据库动态获取数据 树状结构失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "菜单栏从数据库动态获取数据 树状结构失败", ResponseStatus.CODE_WRONG.getValue());

    }

    public Node getTreeJson(List<SystemLoginMenu> list ) {
        List<Node> nodes = new ArrayList<Node>();//把所有资源转换成树模型的节点集合，此容器用于保存所有节点
        for(SystemLoginMenu ls : list){
            Node node = new Node();
            node.setHref(ls.getMenuPath());
            node.setIcon(null);
            node.setNodeId(ls.getId().toString());
            node.setPid(ls.getParentId().toString());
            node.setText(ls.getMenuName());
            nodes.add(node);//添加到节点容器
        }
        Node tree = new Node();//重要插件，创建一个树模型
        Node mt = tree.createTree(nodes,"0");//Node类里面包含了一个创建树的方法。这个方法就是通过节点的信息（nodes）来构建一颗多叉树manytree->mt。
        return mt;
    }

    /*
     * 获取菜单栏所有数据  List
     * */
    //@LoginRequired
    //@RequiresPermissions(value={"authorityManage"},logical= Logical.OR)
    @RequestMapping(value = "/getAllTableList",method = RequestMethod.GET)
    public ResponseResult getAllTableList(){
        try{
            Node tree = getTreeJson(commonService.getAllTableList());//获得一棵树模型的数据
            return new ResponseResult("ok","200", tree,"请求成功") ;
        }catch (Exception e){
            log.error(" 获取菜单栏所有数据失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "获取菜单栏所有数据失败", ResponseStatus.CODE_WRONG.getValue());

    }

    /*
     * 获取按钮的所有数据  List
     * */
    //@LoginRequired
    //@RequiresPermissions(value={"authorityManage"},logical= Logical.OR)
    @RequestMapping(value = "/queryButtonMenuList",method = RequestMethod.GET)
    public ResponseResult queryButtonMenuList(){
        try{
            List<SystemButtonMenu> systemButtonMenuList = commonService.queryButtonMenuList();

            return new ResponseResult("ok","200", systemButtonMenuList,"请求成功") ;
        }catch (Exception e){
            log.error("获取按钮的所有数据失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "获取按钮的所有数据失败", ResponseStatus.CODE_WRONG.getValue());
    }


    /*保存所有按钮*/
//    @LoginRequired
//    @RequiresPermissions(value={"authorityManage"},logical= Logical.OR)
    @RequestMapping(value = "/saveButtonMenuList",method = RequestMethod.POST)
    public ResponseResult saveButtonMenu(Integer roleId, @RequestBody ButtonParam[] params, HttpServletRequest request){

        return new ResponseResult("ok","200", null,"请求成功") ;
    }


    /*
     * 新增菜单栏(界面未实现)
     * */
    @LoginRequired
    @RequestMapping(value = "/addNewTable",method = RequestMethod.POST)
    public ResponseResult addNewTable(@RequestBody SystemLoginMenu systemLoginMenu){
        try{
            systemLoginMenu.setCreateTime(new Date());//创建时间
            commonService.addOrEditTable(systemLoginMenu);
            return  new ResponseResult("ok","200","请求成功") ;
        }catch (Exception e){
            log.error("新增菜单栏失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "新增菜单栏失败", ResponseStatus.CODE_WRONG.getValue());
    }
    /*
     * 编辑菜单栏
     * */
    @LoginRequired
    @RequestMapping(value = "/editTable",method = RequestMethod.POST)
    public ResponseResult editTable(@RequestBody SystemLoginMenu systemLoginMenu){
        try{
            commonService.addOrEditTable(systemLoginMenu);
            return  new ResponseResult("ok","200","请求成功") ;
        }catch (Exception e){
            log.error(" 编辑菜单栏失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                " 编辑菜单栏失败", ResponseStatus.CODE_WRONG.getValue());
    }
    /*
     * 删除菜单栏
     * */
    @LoginRequired
    @RequestMapping(value = "/deleteTable/{id}",method = RequestMethod.GET)
    public ResponseResult deleteTable(@PathVariable(value = "id") Integer id){
        try{
            commonService.deleteTable(id);
            return  new ResponseResult("ok","200","请求成功") ;
        }catch (Exception e){
            log.error("删除菜单栏失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "删除菜单栏失败", ResponseStatus.CODE_WRONG.getValue());
    }


    /**
     * 个人详情
     * @param request
     * @return
     */
    //@LoginRequired
    @RequestMapping(value = "/userInfoDetail",method = RequestMethod.POST)
    public ResponseResult queryUserInfoDetail(HttpServletRequest request) {
        try{
            Integer userId = Integer.parseInt(request.getParameter("userId"));
            UserInfoResultDto UserInfoResultDto = new UserInfoResultDto();
            UserInfoResultDto=commonService.queryUserInfoDetail(userId);
            return new ResponseResult("ok","200",UserInfoResultDto,"请求成功");
        }catch (Exception e){
            log.error("个人详情失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "个人详情失败", ResponseStatus.CODE_WRONG.getValue());
    }

    /**
     * 获取未绑定地址打币记录
     */
    @LoginRequired
    @RequiresPermissions(value = {"financingunknownIncome"},logical = Logical.OR)
    @RequestMapping(value = "/financingunknownIncome/list",method = RequestMethod.POST)
    public Object financingunknownIncome(@RequestBody JSONParam[] params) {
        StringBuilder stringJson = new StringBuilder();
        try{
            Map<String, String> paramMap = MapUtil.convertToMap(params);
            String sEcho = paramMap.get("sEcho");
            Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
            Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
            int status = Integer.parseInt(paramMap.get("status"));
            String search = paramMap.get("search");
            Map<String, Object> retmap = commonService.queryFinancingIncomeUnknowns(start, length, status ,search);

            Integer renum = (Integer) retmap.get("num");
            List<FinancingIncomeUnknown> list = (List<FinancingIncomeUnknown>) retmap.get("items");
            stringJson = new StringBuilder("{\"sEcho\":" + sEcho
                    + ",\"iTotalRecords\":" + renum + ",\"iTotalDisplayRecords\":"
                    + renum + ",\"aaData\":[");
            FinancingIncomeUnknown model = null;
            for (int i = 0; i < list.size(); i++) {
                model = list.get(i);
                stringJson.append("[");
                stringJson.append("\"" + model.getId() + "\",");
                stringJson.append("\"" + model.getCoinType() + "\",");
                stringJson.append("\"" + model.getPaymentAmount().setScale(3,BigDecimal.ROUND_HALF_UP) + model.getPaymentType() + "\",");
                stringJson.append("\"" + formatStr(model.getCoinAmount().setScale(3,BigDecimal.ROUND_HALF_UP)) + "\",");
                stringJson.append("\"" + model.getTsId() + "\",");
                stringJson.append("\"" + formatStr(model.getConfirmNum()) + "\",");
                stringJson.append("\"" + formatStr(model.getTradeConfirmTimeStr()) + "\",");
                stringJson.append("\"" + formatStr(model.getCoin2Bcb().setScale(3,BigDecimal.ROUND_HALF_UP)) + "\",");
                stringJson.append("\"" + formatStr(model.getBcb2Usdx().setScale(3,BigDecimal.ROUND_HALF_UP)) + "\",");
                stringJson.append("\"" + formatStr(model.getStatus()) + "\",");
                stringJson.append("\"" + formatStr(model.getFromToken()) + "\",");
                stringJson.append("\"" + formatStr(model.getToToken()) + "\",");
                stringJson.append("\"" + formatStr(model.getTsId()) + "\",");

                stringJson.append("\"\"");
                stringJson.append("],");
            }
            if (list.size() > 0)
                stringJson.deleteCharAt(stringJson.length() - 1);
            stringJson.append("]");
            stringJson.append("}");
            return stringJson.toString();
        }catch (Exception e){
            log.error("获取未绑定地址打币记录失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "获取未绑定地址打币记录失败", ResponseStatus.CODE_WRONG.getValue());
    }

    private String formatStr(String str){
        if(StringUtils.isNotBlank(str) && str != null){
            return str;
        }else{
            return "";
        }
    }

    private String formatStr(Object obj){
        if(obj != null) {
            String str = obj.toString();
            if (StringUtils.isNotBlank(str)) {
                return str;
            }
        }
        return "";
    }

    @LoginRequired
    @RequestMapping(value = "/permissionButtonList",method = RequestMethod.GET)
    public ResponseResult permissionButtonList(HttpServletRequest request){
        try{
            SystemLoginAccount systemLoginAccount =(SystemLoginAccount) request.getAttribute("account");
            return new ResponseResult("ok","200", commonService.queryPermissionButtonMenuList(systemLoginAccount.getId()),"请求成功") ;
        }catch (Exception e){
            log.error("权限按钮获取失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "权限按钮获取失败", ResponseStatus.CODE_WRONG.getValue());
    }



    ///////////////用户管理接口////////////////////////////////////
    @ApiOperation("用户管理_获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "condition", value = "用户id或者用户手机号码，支持模糊查询", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/userList")
    public ResponseResult treeList(
            HttpServletRequest request,
            @RequestParam(value = "pageSize") Integer pageSize,
            @RequestParam(value = "pageNum") Integer pageNum,
            @RequestParam(value = "condition",required = false) String condition
    ){
        try{
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),userService.userListService(condition,pageSize,pageNum,account),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("用户列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("用户管理_查看手机号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/checkMobileNumber",method = RequestMethod.POST)
    public ResponseResult checkMobileNumber(
            HttpServletRequest request,
            @RequestParam(value = "userId") Integer userId)
    {
        try{
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),userService.checkMobileNumberService(userId,account),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("查看手机号失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e.getMessage(),ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("用户管理_查看钱包地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })
 //   @LoginRequired
    @RequestMapping(value = "/checkWallet",method = RequestMethod.POST)
    public ResponseResult checkWallet(
            HttpServletRequest request,
            @RequestParam(value = "userId") Integer userId

    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),userService.checkWalletService(userId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("查看钱包地址失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("用户管理_设置用户代理比列、更换上级、折扣比列设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "financingScale", value = "返点比列", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userDiscountRate", value = "折扣比列", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "parentId", value = "更换上级代理的代理id", required = true, dataType = "String", paramType = "query")
    })
 //   @LoginRequired
    @RequestMapping(value = "/settingUserMessage",method = RequestMethod.POST)
    public ResponseResult checkWallet(
            HttpServletRequest request,
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "financingScale",required = false) String financingScale,
            @RequestParam(value = "userDiscountRate",required = false) String userDiscountRate,
            @RequestParam(value = "parentId",required = false) String parentId
    ) {
        try{
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),userService.settingUserMessageService(financingScale,userDiscountRate,parentId,userId,account),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("设置失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e.getMessage(),ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("用户管理_获取用户代理返点比列、用户折扣率、用户的父级id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })
  //  @LoginRequired
    @RequestMapping(value = "/userMessage",method = RequestMethod.POST)
    public ResponseResult userMessage(
            @RequestParam(value = "userId") Integer userId
    ){
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),userService.userMessageService(userId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("用户列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),ResponseStatus.CODE_WRONG.getValue());
        }
    }

}
