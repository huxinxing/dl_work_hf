package com.bcb.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.bcb.bean.ResponseStatus;
import com.bcb.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.UserInvestRateDto;

/**交易相关
 * Created by kx on 2018/1/28.
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/trade")
public class TradeController {
    @Autowired
    private CommonService commonService;

    @LoginRequired
    @RequiresPermissions(value={"trades","transferFlow","agentsWithdraws"},logical= Logical.OR)
    @RequestMapping(value = "/agents/list",method = RequestMethod.GET)
    public ResponseResult agentsList(HttpServletRequest request) {
        try{
            Integer tradeId = Integer.parseInt(request.getParameter("tradeId"));
            List<UserInvestRateDto> list = commonService.queryAgentTradeList(tradeId);
            return new ResponseResult(WorldValue.SUCCESS,"200",list,"请求成功");
        }catch (Exception e){
            log.error("交易失败",e);
        }
        return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
                "交易失败失败", ResponseStatus.CODE_WRONG.getValue());
    }
}
