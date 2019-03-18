package com.bcb.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bcb.annotation.ActionType;
import com.bcb.bean.dto.SystemRoleAddDto;
import com.bcb.domain.entity.SystemLogOperation;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.service.CommonService;
import com.bcb.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.RoleDto;
import com.bcb.domain.entity.SystemLoginRole;
import com.bcb.util.Node;

import javax.servlet.http.HttpServletRequest;

/*
* xujintao
* */
@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private CommonService commonService;

    @Autowired
    RoleService roleService;

    /*
    * 新建角色，并授权菜单权限接口
    * */
    @LoginRequired
//    @RequiresPermissions(value={"authorityManage:add"},logical= Logical.OR)
    @RequiresPermissions(value={"authorityManage"},logical= Logical.OR)
    @RequestMapping(value = "/createNewRole",method = RequestMethod.POST)
    public ResponseResult createNewRole( @RequestBody SystemRoleAddDto systemRoleAddDto, HttpServletRequest request){
        try{
            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("系统管理");
            tempLog.setOperatorName(account.getLoginName());

            if (systemRoleAddDto.getId()==null){//新增
                tempLog.setActionType(ActionType.ADD.getText());
                tempLog.setParameterContext("角色新增"+":"+systemRoleAddDto.getRoleName());
                Integer size = commonService.queryRoleUser(systemRoleAddDto.getRoleName()).size();
                if (size>0){
                    return new ResponseResult(WorldValue.ERROR, ResponseStatus.USER_SAME.getKey(),null,ResponseStatus.USER_SAME.getValue());
                }
            }
            SystemLoginRole systemLoginRole=new SystemLoginRole();
            systemLoginRole.setRoleName(systemRoleAddDto.getRoleName());
            systemLoginRole.setCreateTime(new Date());
            systemLoginRole.setId(systemRoleAddDto.getId());
            commonService.createNewRole(systemLoginRole);
            //新增解
            if(systemRoleAddDto.getMenuIdArray().length>0) {
                roleService.insertRolePermission(systemLoginRole.getId(), systemRoleAddDto.getMenuIdArray());
            }
            commonService.saveOperationLog(tempLog);
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),null,ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("新建角色，并授权菜单权限失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"新建角色，并授权菜单权限失败",ResponseStatus.CODE_WRONG.getValue());
    }

    /*
     * 修改角色，并授权菜单权限接口
     * */
    @LoginRequired
//    @RequiresPermissions(value={"authorityManage:edit"},logical= Logical.OR)
    @RequiresPermissions(value={"authorityManage"},logical= Logical.OR)
    @RequestMapping(value = "/editRole",method = RequestMethod.POST)
    public ResponseResult editRole( @RequestBody SystemRoleAddDto systemRoleAddDto, HttpServletRequest request){
        try{
            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("系统管理");
            tempLog.setOperatorName(account.getLoginName());

            if (systemRoleAddDto.getId()==null){//新增

            }else{//修改
                tempLog.setActionType(ActionType.UPDATE.getText());
                tempLog.setParameterContext("角色修改"+":"+systemRoleAddDto.getRoleName());
                Integer size = roleService.queryRoleUserOutName(systemRoleAddDto.getId(),systemRoleAddDto.getRoleName()).size();
                if (size>0){
                    return new ResponseResult(WorldValue.ERROR, ResponseStatus.USER_SAME.getKey(),null,ResponseStatus.USER_SAME.getValue());
                }
            }
            SystemLoginRole systemLoginRole=new SystemLoginRole();
            systemLoginRole.setRoleName(systemRoleAddDto.getRoleName());
            systemLoginRole.setCreateTime(new Date());
            systemLoginRole.setId(systemRoleAddDto.getId());
            commonService.createNewRole(systemLoginRole);
            //新增解
            if(systemRoleAddDto.getMenuIdArray().length>0) {
                roleService.insertRolePermission(systemLoginRole.getId(), systemRoleAddDto.getMenuIdArray());
            }
            commonService.saveOperationLog(tempLog);
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),null,ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("修改角色，并授权菜单权限失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"修改角色，并授权菜单权限失败",ResponseStatus.CODE_WRONG.getValue());
    }



    /*
    * 删除权限接口
    * */
    @LoginRequired
//   @RequiresPermissions(value={"authorityManage:delete"},logical= Logical.OR)
    @RequestMapping(value = "/deleteRoleByIds",method = RequestMethod.GET)
    public ResponseResult deleteRoleByIds(String ids, HttpServletRequest request){
        try{
            if (commonService.queryAccountExit(ids)>0){//角色已被使用不能删除
                return new ResponseResult(WorldValue.ERROR, ResponseStatus.USER_ROLE_WRONG.getKey(),null,ResponseStatus.USER_ROLE_WRONG.getValue());
            }

            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("系统管理");
            tempLog.setOperatorName(account.getLoginName());
            tempLog.setActionType(ActionType.ADD.getText());
            tempLog.setParameterContext("角色删除"+":"+commonService.queryRoleById(Integer.valueOf(ids)).getRoleName());
            commonService.saveOperationLog(tempLog);

            commonService.deleteRoleByIds(ids);
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),null,ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("删除权限失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"删除权限失败",ResponseStatus.CODE_WRONG.getValue());
    }




    /*
    *修改权限(权鉴未做)
    * */
    @LoginRequired
    @RequestMapping(value = "/updateRole",method = RequestMethod.POST)
    public ResponseResult updateRole(SystemLoginRole systemLoginRole){
        try{
            systemLoginRole.setModifyTime(new Date());
            commonService.updateRole(systemLoginRole);
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),null,ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("修改权限失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"修改权限失败",ResponseStatus.CODE_WRONG.getValue());
    }
    /*
    * 查询权限根据id
    * */
    @LoginRequired
    @RequestMapping(value = "/selectRoleById/{id}",method = RequestMethod.GET)
    public ResponseResult selectRoleById(@PathVariable(value = "id") Integer id){
        try{
            List<RoleDto> list =  roleService.selectRoleById(id);
            Node ls = getTreeJson(id);
            return new ResponseResult("ok","200",ls,"请求成功") ;
        }catch (Exception e){
            log.error("查询权限根据id失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"查询权限根据id失败",ResponseStatus.CODE_WRONG.getValue());
    }
    public Node getTreeJson(Integer id) {
        List<RoleDto> list =  roleService.selectRoleById(id);
        List<Node> nodes = new ArrayList<>();//把所有资源转换成树模型的节点集合，此容器用于保存所有节点
        for(RoleDto ls : list){
            Node node = new Node();
            node.setHref("");
            node.setIcon(null);
            node.setNodeId(ls.getId().toString());
            // 权限去掉上级这个字段了
            //node.setPid(ls.getParentId().toString());
            node.setText(ls.getRoleName());
            nodes.add(node);//添加到节点容器
        }
        Node tree = new Node();//重要插件，创建一个树模型

        Node mt = tree.createTree(nodes,id.toString());//Node类里面包含了一个创建树的方法。这个方法就是通过节点的信息（nodes）来构建一颗多叉树manytree->mt。
        System.out.println(mt);
        return mt;
    }
    /*
    * 新增或者修改权限权限
    * */
    @LoginRequired
    @RequestMapping(value = "/updateRolePermission",method = RequestMethod.GET)
    public ResponseResult updateRolePermission(@RequestParam(value = "id") Integer id ,@RequestParam(value = "permissions") String permissions){
        try{
            roleService.updateRolePermission(id ,permissions);
            return new ResponseResult("ok","200",null,"请求成功") ;
        }catch (Exception e){
            log.error("新增或者修改权限失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"新增或者修改权限失败",ResponseStatus.CODE_WRONG.getValue());
    }
    /*
    *查询该权限下面所有后台用户
    * */
    @LoginRequired
    @RequestMapping(value = "/queryRoleUser/{id}",method = RequestMethod.GET)
    public ResponseResult queryRoleUser(@PathVariable(value = "id") Integer id){
        try{
            return new ResponseResult("ok","200", commonService.queryRoleUser(id),"请求成功") ;
        }catch (Exception e){
            log.error("查询该权限下面所有后台用户失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"查询该权限下面所有后台用户失败",ResponseStatus.CODE_WRONG.getValue());
    }
    /*
    *所有权限
    * */
    @LoginRequired
    @RequiresPermissions(value = {"accounts"},logical= Logical.OR)
    @RequestMapping(value = "/queryAllRoles",method = RequestMethod.GET)
    public ResponseResult queryAllRoles(){
        try{
            return new ResponseResult("ok","200", commonService.queryAllRoles(),"请求成功") ;
        }catch (Exception e){
            log.error("所有权限失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"所有权限失败",ResponseStatus.CODE_WRONG.getValue());
    }

    /*
*所有权限对应的菜单
* */
    @LoginRequired
    @RequiresPermissions(value={"authorityManage"},logical= Logical.OR)
    @RequestMapping(value = "/queryAllRolesAndMenu",method = RequestMethod.GET)
    public ResponseResult queryAllRolesAndMenu(){
        try{
            return new ResponseResult("ok","200", roleService.queryAllRolesAndMenu(),"请求成功") ;
        }catch (Exception e){
            log.error("所有权限对应的菜单失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"所有权限对应的菜单失败",ResponseStatus.CODE_WRONG.getValue());
    }

    /*
    *所有权限对应的菜单
* */
    @LoginRequired
    @RequiresPermissions(value={"authorityManage"},logical= Logical.OR)
    @RequestMapping(value = "/queryAllRolesAndMenuById/{id}",method = RequestMethod.GET)
    public ResponseResult queryAllRolesAndMenuById(@PathVariable(value = "id") Integer id){
        try{
            return new ResponseResult("ok","200", roleService.queryAllRolesAndMenuById(id),"请求成功") ;
        }catch (Exception e){
            log.error("所有权限对应的菜单失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"所有权限对应的菜单失败",ResponseStatus.CODE_WRONG.getValue());
    }
    /*
    * 根据权限id查询其相应的权限
    * */
    @LoginRequired
    @RequestMapping(value = "/queryRolesPermission/{id}",method = RequestMethod.GET)
    public ResponseResult queryRolesPermission(@PathVariable(value = "id") Integer id){
        try{
            return new ResponseResult("ok","200", roleService.queryRolesPermission(id),"请求成功") ;
        }catch (Exception e){
            log.error("根据权限id查询其相应的权限",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"根据权限id查询其相应的权限",ResponseStatus.CODE_WRONG.getValue());
    }
}
