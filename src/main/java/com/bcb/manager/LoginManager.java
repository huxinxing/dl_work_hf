package com.bcb.manager;

import com.bcb.annotation.ActionType;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.WorldValue;
import com.bcb.domain.repository.SystemLoginAccountRepository;
import com.bcb.domain.entity.SystemLogOperation;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.service.CommonService;
import com.bcb.util.PasswordUtil;
import com.bcb.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class LoginManager {
    @Autowired
    private CommonService commonService;

    @Autowired
    private SystemLoginAccountRepository systemLoginAccountRepository;

    public SystemLoginAccount validLogin(String loginName, String loginPass) {
        SystemLoginAccount systemLoginAccount = systemLoginAccountRepository.findByLoginName(loginName);
        String codePass = PasswordUtil.MD5Salt(loginPass,systemLoginAccount.getSalt());
        if(!codePass.equalsIgnoreCase(systemLoginAccount.getPassword())){
            return  null;
        }
        return systemLoginAccount;
    }

    public ResponseResult modifyPass(String oldpass,String newpass,Integer userId) {
        SystemLoginAccount systemLoginAccount = commonService.querySystemLoginAccount(userId);
        String salt = systemLoginAccount.getSalt();
        String old=PasswordUtil.MD5Salt(oldpass,salt);
        if(!PasswordUtil.MD5Salt(oldpass,salt).equalsIgnoreCase(systemLoginAccount.getPassword())){
            return new ResponseResult(WorldValue.ERROR,"500","原密码输入错误,请重新输入。");
        }
        String newSalt = RandomUtil.GetRandomString(6);
        systemLoginAccount.setSalt(newSalt);
        systemLoginAccount.setPassword(PasswordUtil.MD5Salt(newpass,newSalt));
        commonService.saveOrUpdateSystemLoginAccount(systemLoginAccount);
        SystemLogOperation tempLog=new SystemLogOperation();
      //  SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
        Date now = new Date();
        tempLog.setCreateTime(now) ;
        tempLog.setCommentDescribe("个人中心");
        tempLog.setOperatorName(systemLoginAccount.getLoginName());
        tempLog.setActionType(ActionType.UPDATE.getText());
        tempLog.setParameterContext("修改密码"+":"+systemLoginAccount.getLoginName());
        commonService.saveOperationLog(tempLog);
        return new ResponseResult(WorldValue.SUCCESS,"200","密码修改成功。");
    }
}
