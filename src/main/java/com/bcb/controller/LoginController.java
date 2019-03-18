package com.bcb.controller;

import com.bcb.annotation.ActionType;
import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.AccountDto;
import com.bcb.config.RedisClient;
import com.bcb.domain.entity.SystemConfigInfo;
import com.bcb.domain.repository.SystemConfigurationRepository;
import com.bcb.domain.entity.SystemConfiguration;
import com.bcb.domain.entity.SystemLogOperation;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.manager.LoginManager;
import com.bcb.service.CommonService;
import com.bcb.shiro.JWTToken;
import com.bcb.util.GoogleAuthenticatorUtil;
import com.bcb.util.PasswordUtil;
import com.bcb.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by kx on 2018/1/12.
 */
@Slf4j
@Api("登录接口")
@RestController
@RequestMapping("/admin/login")
public class LoginController {
    @Autowired
    private LoginManager loginManager;
    @Autowired
    private CommonService commonService;

    @Resource
    protected RedisClient redisClient;


    @ApiOperation("登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginName",value = "用户名",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "loginPass",value = "密码",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "/postLogin",method = RequestMethod.POST)
    public ResponseResult postLogin(HttpServletRequest request) {
        try{
            String loginName = request.getParameter("username");
            String loginPass = request.getParameter("password");
            String googleCode = request.getParameter("googleCode");

            if(StringUtils.isEmpty(googleCode)){
                return new ResponseResult(WorldValue.ERROR, "510", "谷歌验证码不能为空");
            }

            long t = System.currentTimeMillis();
            Boolean flag=  GoogleAuthenticatorUtil.check_code(Long.parseLong(googleCode), t, commonService.querySystemConfigInfo(1).getGoogleSecret());
            if (!flag){
                return new ResponseResult(WorldValue.ERROR, "510", "谷歌验证码错误");
            }

            Subject currentUser = SecurityUtils.getSubject();
            String stk = TokenUtil.createJwtToken(loginName,loginPass,System.currentTimeMillis());
            JWTToken tk = new JWTToken(stk);
            currentUser.login(tk);

            SystemLoginAccount systemLoginAccount =(SystemLoginAccount) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();

            AccountDto accountDto = new AccountDto();
            accountDto.setLoginName(systemLoginAccount.getLoginName());
            accountDto.setToken(stk);
            accountDto.setRole(systemLoginAccount.getRole());

            if(systemLoginAccount == null)
                return new ResponseResult(WorldValue.ERROR, "520", "账户不存在");
            if(systemLoginAccount.getStatus().equals(0))
                return new ResponseResult(WorldValue.ERROR, "521", "账户已失效");
            String codePass = PasswordUtil.MD5Salt(loginPass,systemLoginAccount.getSalt());
            if(!codePass.equalsIgnoreCase(systemLoginAccount.getPassword()))
                return new ResponseResult(WorldValue.ERROR,"523", "密码错误");

            redisClient.set("user"+systemLoginAccount.getId(),stk,1800L);

            SystemLogOperation tempLog=new SystemLogOperation();
            //  SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("个人中心");
            tempLog.setOperatorName(systemLoginAccount.getLoginName());
            tempLog.setActionType(ActionType.OTHERRESULT.getText());
            tempLog.setParameterContext("登录系统"+":"+""+systemLoginAccount.getLoginName());
            commonService.saveOperationLog(tempLog);
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),accountDto,ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("登录失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, "登录失败" ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
    }

    @LoginRequired
    @RequestMapping(value = "/modifyPass",method = RequestMethod.POST)
    public ResponseResult modifyPass(HttpServletRequest request) {
        try{
            String oldpass = request.getParameter("oldpass");
            String newpass = request.getParameter("newpass");
            SystemLoginAccount systemLoginAccount = gainAccount(request);
            SystemLogOperation tempLog=new SystemLogOperation();
            //  SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("个人中心");
            tempLog.setOperatorName(systemLoginAccount.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            tempLog.setParameterContext("修改密码"+":"+""+systemLoginAccount.getLoginName());
            commonService.saveOperationLog(tempLog);
            return loginManager.modifyPass(oldpass,newpass,systemLoginAccount.getId());
        }catch (Exception e){
            log.error("修改密码失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, "修改密码失败" ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());

    }

    private SystemLoginAccount gainAccount(HttpServletRequest request) {
        return (SystemLoginAccount) request.getAttribute("account");
    }


    @LoginRequired
    @RequestMapping(value = "/loginout",method = RequestMethod.POST)
    public ResponseResult loginloginout(HttpServletRequest request) {
        try{
            SystemLoginAccount systemLoginAccount = gainAccount(request);
            SystemLogOperation tempLog=new SystemLogOperation();
            Date now = new Date();
            tempLog.setCreateTime(now) ;
            tempLog.setCommentDescribe("个人中心");
            tempLog.setOperatorName(systemLoginAccount.getLoginName());
            tempLog.setActionType(ActionType.OTHERRESULT.getText());
            tempLog.setParameterContext("退出登录"+":"+""+systemLoginAccount.getLoginName());
            commonService.saveOperationLog(tempLog);
            return new ResponseResult(WorldValue.SUCCESS,"200","成功退出");
        }catch (Exception e){
            log.error("退出登录失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, "退出登录失败" ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
    }
}
