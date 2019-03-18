package com.bcb.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bcb.domain.dao.RoleDao;
import com.bcb.domain.repository.SystemLoginAccountRepository;
import com.bcb.service.CommonService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bcb.annotation.ActionType;
import com.bcb.annotation.LoginRequired;
import com.bcb.bean.JSONParam;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.OperationLogDto;
import com.bcb.bean.req.OperationLogRequestDto;
import com.bcb.bean.req.SmsLogRequestDto;
import com.bcb.domain.entity.AppConfigInfo;
import com.bcb.domain.entity.SystemBusinessDictionary;
import com.bcb.domain.entity.SystemConfigInfo;
import com.bcb.domain.entity.SystemLogOperation;
import com.bcb.domain.entity.SystemLogSms;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.domain.entity.SystemLoginRole;
import com.bcb.util.GoogleAuthenticatorUtil;
import com.bcb.util.MapUtil;
import com.bcb.util.PasswordUtil;
import com.bcb.util.RandomUtil;

/**系统管理员后台
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/sys")
public class AdminController {
    @Autowired
    private CommonService commonService;

    //@Autowired
    //private RoleDao roleDao;

    @Autowired
    private SystemLoginAccountRepository systemLoginAccountRepository;

    /*
        获取登录用户列表（包括管理员用户、运营用户、审核人员）
     */
    @LoginRequired
    @RequiresPermissions(value={"accounts","investManage"},logical=Logical.OR)
    @RequestMapping(value = "/account/list", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    Object accountList(@RequestBody JSONParam[] params)  {
        StringBuilder stringJson = new StringBuilder();
        try{
            Map<String, String> paramMap = MapUtil.convertToMap(params);
            Integer sEcho = Integer.parseInt(paramMap.get("sEcho"));
            Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
            Integer pageSize = Integer.parseInt(paramMap.get("iDisplayLength"));
            Page<SystemLoginAccount> list = systemLoginAccountRepository.findAll(PageRequest.of(start/pageSize, pageSize));
            Integer renum = list.getContent().size();
            List<SystemLoginAccount> accounts = list.getContent();
            stringJson = new StringBuilder("{\"sEcho\":" + sEcho+",\"iTotalRecords\":" + list.getTotalElements() + ",\"iTotalDisplayRecords\":"
                    + list.getTotalElements() + ",\"aaData\":[");
            SystemLoginAccount systemLoginAccount = null;
            for (int i = 0; i < accounts.size(); i++) {
                systemLoginAccount = accounts.get(i);
                stringJson.append("[");
                stringJson.append("\"" + systemLoginAccount.getLoginName() + "\",");
                stringJson.append("\"" + gainRoleDesc(systemLoginAccount.getRole())+ "\",");
                stringJson.append("\"" + (systemLoginAccount.getStatus().equals(1)?"启用":"禁用")+ "\",");
                stringJson.append("\"" + systemLoginAccount.getTime()+ "\",");
                stringJson.append("\"" + systemLoginAccount.getId() + "\"");
                stringJson.append("],");
            }
            if (accounts.size() > 0)
                stringJson.deleteCharAt(stringJson.length() - 1);
            stringJson.append("]");
            stringJson.append("}");

        }catch (Exception e){
            log.error("用户列表获取失败",e);
        }
        return stringJson.toString();
    }

    /*
        修改登录用户的状态
     */
    @LoginRequired
//    @RequiresPermissions(value = {"accounts:start"},logical = Logical.OR)
    @RequiresPermissions(value = {"accounts"},logical = Logical.OR)
    @RequestMapping(value = "/account/status",method = RequestMethod.POST)
    public ResponseResult accountStatus(HttpServletRequest request) {
        try{
            Integer accountId = Integer.parseInt(request.getParameter("aid"));
            Integer sval = Integer.parseInt(request.getParameter("sval"));
            SystemLoginAccount systemLoginAccount = commonService.querySystemLoginAccount(accountId);
            systemLoginAccount.setStatus(sval);
            commonService.saveOrUpdateSystemLoginAccount(systemLoginAccount);

            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("系统管理");
            tempLog.setOperatorName(account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            if(sval==1){
                tempLog.setParameterContext("账号启用"+":"+systemLoginAccount.getLoginName());}
            if(sval==0){
                tempLog.setParameterContext("账号禁用"+":"+systemLoginAccount.getLoginName());}
            commonService.saveOperationLog(tempLog);
            return new ResponseResult(WorldValue.SUCCESS,"200","请求成功");
        }catch (Exception e){
            log.error("修改账户状态失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");

    }


    /*
        新增或者修改登录用户状态
     */
    @LoginRequired
    //@RequiresPermissions(value = {"accounts","accounts:add"},logical = Logical.OR)
    @RequiresPermissions(value = {"accounts"},logical = Logical.OR)
    @RequestMapping(value = "/account/add",method = RequestMethod.POST)
    public ResponseResult accountAdd(HttpServletRequest request) {
        try{
            String aid = request.getParameter("aid");
            String apid = request.getParameter("apid");
            String loginName = request.getParameter("loginName");

            String passwd = request.getParameter("passwd");
            Integer status = Integer.parseInt(request.getParameter("status"));
            Integer role = Integer.parseInt(request.getParameter("role"));

            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("系统管理");
            tempLog.setOperatorName(account.getLoginName());


            if(StringUtils.isNotBlank(aid)) {
                //更新账户
                Integer id = Integer.parseInt(aid);
                SystemLoginAccount systemLoginAccount = commonService.querySystemLoginAccount(id);
                List<SystemLoginAccount> sla = systemLoginAccountRepository.findByIdNotAndLoginName(id, loginName); //roleDao.querySystemLoginAccountWithOutName(Integer.parseInt(aid),loginName);
                if (sla.size()>0){
                    return   new ResponseResult(WorldValue.ERROR,"1000","用户已存在");
                }
                systemLoginAccount.setStatus(status);
                systemLoginAccount.setLoginName(loginName);
                systemLoginAccount.setRole(role);
                if(!passwd.equalsIgnoreCase(systemLoginAccount.getPassword()))
                    systemLoginAccount.setPassword(PasswordUtil.MD5Salt(passwd,systemLoginAccount.getSalt()));
                commonService.saveOrUpdateSystemLoginAccount(systemLoginAccount);
                tempLog.setActionType(ActionType.UPDATE.getText());
                tempLog.setParameterContext("账号修改"+":"+systemLoginAccount.getLoginName());
                commonService.saveOperationLog(tempLog);
            }else {
                SystemLoginAccount sla = systemLoginAccountRepository.findByLoginName(loginName);
                if (sla != null){
                    return new ResponseResult(WorldValue.ERROR,"1000","用户已存在");
                }
                SystemLoginAccount systemLoginAccount = new SystemLoginAccount();
                systemLoginAccount.setStatus(status);
                systemLoginAccount.setCreateTime(new Timestamp(System.currentTimeMillis()));
                systemLoginAccount.setLoginName(loginName);
                String salt = RandomUtil.GetRandomString(6);
                systemLoginAccount.setPassword(PasswordUtil.MD5Salt(passwd,salt));
                systemLoginAccount.setRole(role);
                systemLoginAccount.setSalt(salt);
                commonService.saveOrUpdateSystemLoginAccount(systemLoginAccount);
                tempLog.setActionType(ActionType.ADD.getText());
                tempLog.setParameterContext("账号新增"+":"+systemLoginAccount.getLoginName());
                commonService.saveOperationLog(tempLog);
            }
            return new ResponseResult(WorldValue.SUCCESS,"200","请求成功");
        }catch (Exception e){
            log.error("添加用户失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");

    }
   /*
   * 删除登录用户（包括管理人员、运营人员、审核人员的账号）接口
   * */
   @LoginRequired
//   @RequiresPermissions(value = {"accounts:delete"},logical = Logical.OR)
   @RequiresPermissions(value = {"accounts"},logical = Logical.OR)
   @RequestMapping(value = "/account/deleteAccounts/{id}",method = RequestMethod.GET)
   public ResponseResult deleteAccountsById(@PathVariable(value = "id") Integer id,HttpServletRequest request) {
       try{
           SystemLogOperation tempLog=new SystemLogOperation();
           SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
           Date now = new Date();
           tempLog.setCreateTime(now) ;
           tempLog.setCommentDescribe("系统管理");
           tempLog.setOperatorName(account.getLoginName());
           tempLog.setActionType(ActionType.DELETE.getText());
           tempLog.setParameterContext("账号删除"+":"+commonService.querySystemLoginAccount(id).getLoginName());
           commonService.saveOperationLog(tempLog);
           commonService.deleteAccountsById(id);
           return new ResponseResult(WorldValue.SUCCESS,"200",null,"请求成功");
       }catch (Exception e){
           log.error("删除用户失败",e);
       }
       return new ResponseResult(WorldValue.ERROR,"500","请求失败");
   }

   /*
        获取用户的详情信息
    */
    @LoginRequired
   // @RequiresPermissions(value = {"accounts:edit"},logical = Logical.OR)
    @RequiresPermissions(value = {"accounts"},logical = Logical.OR)
    @RequestMapping(value = "/account/detail",method = RequestMethod.POST)
    public ResponseResult accountDetail(HttpServletRequest request) {
        try{
            Integer aid = Integer.parseInt(request.getParameter("aid"));
            SystemLoginAccount systemLoginAccount = commonService.querySystemLoginAccount(aid);
            return new ResponseResult(WorldValue.SUCCESS,"200",systemLoginAccount,"请求成功");
        }catch (Exception e){
            log.error("获取用户详情失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");

    }

    /*
        将登录人员加载未树形结构
     */
    @LoginRequired
    @RequiresPermissions(value={"accounts"},logical=Logical.OR)
    @RequestMapping(value = "/loadTreeData",method = RequestMethod.GET)
    public ResponseResult loadTreeData() {
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            List<SystemLoginAccount> accounts = commonService.querySystemLoginAccounts();
            if(accounts != null && accounts.size()>0) {
                for(SystemLoginAccount account:accounts) {
                    sb.append("{");
                    sb.append("id:"+account.getId()+",");
                    sb.append("name:\""+account.getLoginName()+"\",");
                    sb.append("role:"+account.getRole()+",");
                    if(account.getRole().equals(1)||account.getRole().equals(2))
                        sb.append("open:true");
                    sb.append("},");
                }
            }
            sb.append("]");
            return new ResponseResult("ok","200",sb.toString(),"请求成功");
        }catch (Exception e){
            log.error("加载树形结构失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }

    /**
     * 获取系统字典表数据（目前包括了国家在世界的区号、国家的相关信息）
     * @param request
     * @return
     */
    @RequiresPermissions(value={"projectOp"},logical=Logical.OR)
    @RequestMapping(value = "/dic/get",method = RequestMethod.POST)
    public ResponseResult dicGet(HttpServletRequest request) {
        try{
            Integer groupId = Integer.parseInt(request.getParameter("groupId"));
            List<SystemBusinessDictionary> dics = commonService.querySystemBusinessDictionarys(groupId);
            return new ResponseResult("ok","200",dics,"请求成功");
        }catch (Exception e){
            log.error("获取系统字典表数据失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }

    /*
        目前不知道具体的表业务信息
     */
    @LoginRequired
//    @RequiresPermissions(value={"settings:volatility"},logical=Logical.OR)
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/volatility/set",method = RequestMethod.POST)
    public ResponseResult volatilitySet(HttpServletRequest request) {
        try{
            String volatility = request.getParameter("volatility");
            AppConfigInfo configInfo = commonService.queryAppConfigInfo(1);
            configInfo.setVolatility(volatility);
            commonService.saveOrUpdateAppConfigInfo(configInfo);
            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("设置管理");
            tempLog.setOperatorName(account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            tempLog.setParameterContext("兑换比例设置"+":提交的兑换比例为"+volatility);
            commonService.saveOperationLog(tempLog);
            return new ResponseResult("ok","200","设置成功");
        }catch (Exception e){
            log.error("失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }

    /*
        获取谷歌验证码私钥
     */
    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/googleSecret/get",method = RequestMethod.GET)
    public ResponseResult googleSecretGet(HttpServletRequest request) {
        try{
            SystemConfigInfo systemConfigInfo = commonService.querySystemConfigInfo(1);
            return new ResponseResult("ok","200",systemConfigInfo,"查询成功");
        }catch (Exception e){
            log.error("获取google验证码失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }

    /*
        谷歌验证码？？？？
     */
    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/isShow/set",method = RequestMethod.GET)
    public ResponseResult saveIsShow(HttpServletRequest request) {
        try{
            SystemConfigInfo systemConfigInfo = commonService.querySystemConfigInfo(1);
            systemConfigInfo.setIsShowSuperiorRateSet(Integer.valueOf(request.getParameter("allowShow")));
            commonService.updateSystemConfigInfo(systemConfigInfo);
            return new ResponseResult("ok","200","设置成功");
        }catch (Exception e){
            log.error("失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }



    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/googleSecret/set",method = RequestMethod.POST)
    public ResponseResult googleSecretSet(HttpServletRequest request, SystemConfigInfo systemConfigInfo) {
        try{
            systemConfigInfo.setProjectId(1);
            systemConfigInfo.setId(1);
            commonService.updateSystemConfigInfo(systemConfigInfo);
            return new ResponseResult("ok","200","设置成功");
        }catch (Exception e){
            log.error("设置google验证码失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }

    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/googleSecret/reset",method = RequestMethod.GET)
    public ResponseResult googleSecretReset(HttpServletRequest request) {
        try{
            String googleCode =  GoogleAuthenticatorUtil.generateSecretKey();
            return new ResponseResult("ok","200",googleCode,"查询成功");
        }catch (Exception e){
            log.error("重置google验证码失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }

    /**
     * 20180525zhw 获取短信渠道
     * @param request
     * @return
     */
    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/smsChannel/get",method = RequestMethod.GET)
    public ResponseResult getSmsChannel(HttpServletRequest request) {
        try{
            SystemConfigInfo systemConfigInfo = commonService.querySystemConfigInfo(1);
            return new ResponseResult("ok","200",systemConfigInfo,"查询成功");
        }catch (Exception e){
            log.error("获取短信渠道失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");

    }

    /**
     * 20180525zhw 保存短信渠道
     * @param request
     * @param smsChannel
     * @return
     */
    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/smsChannel/set",method = RequestMethod.POST)
    public ResponseResult saveSmsChannel(HttpServletRequest request, @RequestParam(required =true)  String smsChannel) {
        try{
            SystemConfigInfo systemConfigInfo = commonService.querySystemConfigInfo(1);
            systemConfigInfo.setSmsChannel(smsChannel);
            commonService.updateSystemConfigInfo(systemConfigInfo);
            return new ResponseResult("ok","200","设置成功");
        }catch (Exception e){
            log.error("保存短信渠道失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }



    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/volatility/get",method = RequestMethod.GET)
    public ResponseResult volatilityGet(HttpServletRequest request) {
        try{
            AppConfigInfo configInfo = commonService.queryAppConfigInfo(1);
            return new ResponseResult("ok","200",(StringUtils.isNotBlank(configInfo.getVolatility())
                    ?configInfo.getVolatility():"0"),"设置成功");
        }catch (Exception e){
            log.error("失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }


    /*
        设置是否允许提币
     */
    @LoginRequired
//    @RequiresPermissions(value={"settings:allow"},logical=Logical.OR)
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/allowWithdraw/set",method = RequestMethod.POST)
    public ResponseResult allowWithdrawSet(HttpServletRequest request) {
        try{
            Integer allowWithdraw = Integer.parseInt(request.getParameter("allowWithdraw"));
            AppConfigInfo configInfo = commonService.queryAppConfigInfo(1);
            configInfo.setIsAllowWithdraw(allowWithdraw);
            commonService.saveOrUpdateAppConfigInfo(configInfo);
            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("设置管理");
            tempLog.setOperatorName(account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            if(allowWithdraw==1) {
                tempLog.setParameterContext("代理提币设置"+":允许提币状态");}
            if(allowWithdraw==0) {
                tempLog.setParameterContext("代理提币设置" + ":不允许提币状态");
            }
            commonService.saveOperationLog(tempLog);
            return new ResponseResult("ok","200","设置成功");
        }catch (Exception e){
            log.error("设置提币状态失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }
/*
* 设置实名认证状态
* */
    @LoginRequired
//    @RequiresPermissions(value={"settings:certification"},logical=Logical.OR)
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/isCertification/set",method = RequestMethod.POST)
    public ResponseResult isCertification(HttpServletRequest request) {
        try{
            Integer isCertification = Integer.parseInt(request.getParameter("isCertification"));
            AppConfigInfo configInfo = commonService.queryAppConfigInfo(1);
            configInfo.setIsCertification(isCertification);
            commonService.saveOrUpdateAppConfigInfo(configInfo);
            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("设置管理");
            tempLog.setOperatorName(account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            if(isCertification==1) {
                tempLog.setParameterContext("实名认证设置"+":需要实名认证状态");}
            if(isCertification==0) {
                tempLog.setParameterContext("实名认证设置" + ":不需要实名认证状态");
            }
            commonService.saveOperationLog(tempLog);
            return new ResponseResult("ok","200","设置成功");
        }catch (Exception e){
            log.error("设置实名认证失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }

    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/allowWithdraw/get",method = RequestMethod.GET)
    public ResponseResult allowWithdrawGet(HttpServletRequest request) {
        try{
            AppConfigInfo configInfo = commonService.queryAppConfigInfo(1);
            return new ResponseResult("ok","200",configInfo,"设置成功");
        }catch (Exception e){
            log.error("失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }


    private String gainRoleDesc(Integer role) {
        SystemLoginRole systemLoginRole = commonService.queryRoleById(role);
        return systemLoginRole==null?"":systemLoginRole.getRoleName();
    }

    @LoginRequired
    @RequiresPermissions(value={"operationLog"},logical= Logical.OR)
    @RequestMapping(value = "/operationLoglist", method = RequestMethod.POST)
    public Object operationLoglist(@RequestBody JSONParam[] params) {
        StringBuilder stringJson = new StringBuilder();
        try{
            Map<String, String> paramMap = MapUtil.convertToMap(params);
            Integer sEcho = Integer.parseInt(paramMap.get("sEcho"));
            Integer page = Integer.parseInt(paramMap.get("iDisplayStart"));
            Integer rows = Integer.parseInt(paramMap.get("iDisplayLength"));
            OperationLogRequestDto operationLogtemp =new OperationLogRequestDto();
            String actionType = paramMap.get("actionType");
            String operatorName = paramMap.get("operatorName");
            String createTimeStart = paramMap.get("createTimeStart");
            String createTimeEnd = paramMap.get("createTimeEnd");
            String commentDescribe = paramMap.get("commentDescribe");
            String parameterContext = paramMap.get("parameterContext");
            operationLogtemp.setParameterContext(parameterContext);
            operationLogtemp.setCommentDescribe(commentDescribe);
            operationLogtemp.setActionType(actionType);
            operationLogtemp.setOperatorName(operatorName);
            operationLogtemp.setCreateTimeStart(createTimeStart);
            operationLogtemp.setCreateTimeEnd(createTimeEnd);
            Page<SystemLogOperation> retmap =commonService.operationLoglist(operationLogtemp,page/rows,rows);
            Long renum = retmap.getTotalElements();
            List<SystemLogOperation> list = retmap.getContent();
            stringJson = new StringBuilder("{\"sEcho\":" + sEcho
                    + ",\"iTotalRecords\":" + renum + ",\"iTotalDisplayRecords\":"
                    + renum + ",\"aaData\":[");
            for(SystemLogOperation tempOperationLogDto:list) {
                stringJson.append("[");
                stringJson.append("\"" +tempOperationLogDto.getId() + "\",");
                stringJson.append("\"" + tempOperationLogDto.getActionType() + "\",");
                stringJson.append("\"" + tempOperationLogDto.getCommentDescribe() + "\",");
                stringJson.append("\"" + tempOperationLogDto.getParameterContext() + "\",");
                stringJson.append("\"" + tempOperationLogDto.getOperatorName() + "\",");
                stringJson.append("\"" + tempOperationLogDto.getCreateTime() + "\"");
                stringJson.append("],");
            }
            if (list.size() > 0)
                stringJson.deleteCharAt(stringJson.length() - 1);
            stringJson.append("]");
            stringJson.append("}");
            return stringJson.toString();
        }catch (Exception e){
            log.error("操作日志列表获取失败",e);
        }
        return stringJson.toString();

    }


    @LoginRequired
    @RequestMapping(value = "/smsLoglist", method = RequestMethod.POST)
    public Object smsLoglist(@RequestBody JSONParam[] params) {
        StringBuilder stringJson = new StringBuilder();
        try{
            Map<String, String> paramMap = MapUtil.convertToMap(params);
            String sEcho = paramMap.get("sEcho");
            Integer page = Integer.parseInt(paramMap.get("iDisplayStart"));
            Integer rows = Integer.parseInt(paramMap.get("iDisplayLength"));
            SmsLogRequestDto syslogtemp =new SmsLogRequestDto();
            String mobilePhone = paramMap.get("mobilePhone");
            String isdealWith = paramMap.get("isdealWith");
            String isSend = paramMap.get("isSend");
            String createTimeStart = paramMap.get("createTimeStart");
            String createTimeEnd = paramMap.get("createTimeEnd");
            syslogtemp.setMobilePhone(mobilePhone);
            syslogtemp.setIsdealWith(isdealWith);
            syslogtemp.setIsSend(isSend);
            syslogtemp.setCreateTimeStart(createTimeStart);
            syslogtemp.setCreateTimeEnd(createTimeEnd);
            syslogtemp.setSmsChannel(paramMap.get("smsChannel"));

            Page<SystemLogSms> retmap =commonService.smsLoglist(syslogtemp,page,rows);
            Integer renum = retmap.getContent().size();
            List<SystemLogSms> listSmsLogDto = retmap.getContent();
            stringJson = new StringBuilder("{\"sEcho\":" + sEcho
                    + ",\"iTotalRecords\":" + renum + ",\"iTotalDisplayRecords\":"
                    + renum + ",\"aaData\":[");
            for(SystemLogSms tempSmsLogLogDto:listSmsLogDto) {
                String CountryCode=tempSmsLogLogDto.getCountryCode();
                if("86".equals(CountryCode)) {
                    CountryCode="手机验证";
                } else {
                    CountryCode="海外验证";
                }
                stringJson.append("[");
                stringJson.append("\"" +tempSmsLogLogDto.getUpdateTime() + "\",");
                stringJson.append("\"" + CountryCode + "\",");
                stringJson.append("\"" + tempSmsLogLogDto.getMobilePhone()+ "\",");
                stringJson.append("\"" + tempSmsLogLogDto.getIsdealWith()+ "\",");
                stringJson.append("\"" + tempSmsLogLogDto.getIsSend() + "\",");
                stringJson.append("\"" + tempSmsLogLogDto.getSmsChannel()+ "\",");
                stringJson.append("\"" + tempSmsLogLogDto.getSmsContext()+ "\",");
                stringJson.append("\"" + tempSmsLogLogDto.getResultContext() + "\"");
                stringJson.append("],");
            }
            if (listSmsLogDto.size() > 0)
                stringJson.deleteCharAt(stringJson.length() - 1);
            stringJson.append("]");
            stringJson.append("}");
            return stringJson.toString();
        }catch (Exception e){
            log.error("sms失败",e);
        }
        return stringJson.toString();

    }


    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/langs/get",method = RequestMethod.GET)
    public ResponseResult langsGet() {
        try{
            String langs =  commonService.queryAppConfigInfo(1).getLangs();
            return new ResponseResult("ok","200",langs,"查询成功");
        }catch (Exception e){
            log.error("用户列表获取失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");

    }

    @LoginRequired
    @RequiresPermissions(value={"settings"},logical=Logical.OR)
    @RequestMapping(value = "/langs/set",method = RequestMethod.POST)
    public ResponseResult langsSet(HttpServletRequest request) {
        try{
            String langs = request.getParameter("langs");
            AppConfigInfo configInfo = commonService.queryAppConfigInfo(1);
            configInfo.setLangs(langs);
            commonService.saveOrUpdateAppConfigInfo(configInfo);

            SystemLogOperation tempLog=new SystemLogOperation();
            SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("设置管理");
            tempLog.setOperatorName(account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            tempLog.setParameterContext("更新支持语种为："+langs);
            commonService.saveOperationLog(tempLog);
            return new ResponseResult("ok","200","设置成功");
        }catch (Exception e){
            log.error("更新支持语种失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,"500","请求失败");
    }
}
