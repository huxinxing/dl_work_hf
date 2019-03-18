package com.bcb.controller;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.WhitelistSearchDto;
import com.bcb.domain.entity.Whitelist;
import com.bcb.service.WhitelistService;
import com.bcb.service.PushUserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/whitelist")
public class WhitelistController {

    @Autowired
    private WhitelistService whitelistService;

    @ApiOperation("白名单_获取白名单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletAddress", value = "钱包地址", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页num", required = true, dataType = "String", paramType = "query")})
    @LoginRequired
    @RequestMapping(value = "/whiteListList", method = RequestMethod.POST)
    public ResponseResult whiteList(
            HttpServletRequest request,
            @RequestParam(value = "walletAddress",required = false) String walletAddress,
            @RequestParam(value = "pageSize") Integer pageSize,
            @RequestParam(value = "pageNum") Integer pageNum
    ) {
        try {
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(), whitelistService.whiteListService(walletAddress, pageSize, pageNum), ResponseStatus.SUCCESS.getValue());
        } catch (Exception ex) {
            log.error("获取白名单失败：", ex);
            return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(), ResponseStatus.CODE_WRONG.getValue());
        }

    }

    @ApiOperation("白名单_新增白名单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletAddress", value = "钱包地址", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/whiteAdd", method = RequestMethod.POST)
    public ResponseResult whiteAdd(
            @RequestParam(value = "walletAddress") String walletAddress
    ) {
        // 新增白名单
        try {
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(), whitelistService.whiteAddService(walletAddress), ResponseStatus.SUCCESS.getValue());
        } catch (Exception ex) {
            log.error("新增白名单失败：", ex);
            return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(), ex.getMessage());
        }
    }

    @ApiOperation("白名单_移除白名单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "whiteId", value = "白名单id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/whiteDel", method = RequestMethod.POST)
    public ResponseResult whiteDel(
            @RequestParam(value = "whiteId") Integer whiteId
    ) {
        // 新增白名单
        try {
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),whitelistService.whiteDelService(whiteId), ResponseStatus.SUCCESS.getValue());
        } catch (Exception ex) {
            log.error("移除白名单处理失败：", ex);
            return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(), ex.getMessage());
        }
    }


    @ApiOperation("白名单_修改白名单备注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "whiteId", value = "白名单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "白名单备注", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/whiteUpdateRemark", method = RequestMethod.POST)
    public ResponseResult whiteUpdateRemark(
            @RequestParam(value = "whiteId") Integer whiteId,
            @RequestParam(value = "remark") String remark
    ) {
        // 新增白名单
        try {
            return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),whitelistService.whiteUpdateRemarkService(whiteId,remark), ResponseStatus.SUCCESS.getValue());
        } catch (Exception ex) {
            log.error("修改白名单备注失败：", ex);
            return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(), ex.getMessage());
        }
    }

}
