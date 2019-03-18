package com.bcb.controller;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Api("订单列表")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @ApiOperation("订单_获取订单列表，包括进行中（1进行中）、已赎回（2）、已完成（3）、已失败(4)、每日到期订单(5)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "查询订单类型 （1进行中）、已赎回（2）、已完成（3）、已失败(4)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "recordId", value = "订单id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "financingName", value = "理财项目名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderCollection", value = "用户id、钱包地址、昵称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "searchTime", value = "当日时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderFaildStatus", value = "时间订单处理状态.用于失败订单  1:订单已处理 0：订单未处理", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "redeemptionStatus", value = "时间订单处理状态.用于失败订单   1：本金赎回 2：固定收益赎回 3：本金及固定收益赎回", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "每页数据量", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/orderList", method = RequestMethod.POST)
    public ResponseResult orderList(
            HttpServletRequest request,
            @RequestParam(value = "flag") Integer flag,
            @RequestParam(value = "recordId", required = false) Integer recordId,
            @RequestParam(value = "financingName", required = false) String financingName,
            @RequestParam(value = "orderCollection", required = false) String orderCollection,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "searchTime", required = false) String searchTime,
            @RequestParam(value = "orderFaildStatus", required = false) Integer orderFaildStatus,
            @RequestParam(value = "redeemptionStatus", required = false) Integer redeemptionStatus,
            @RequestParam(value = "pageSize") Integer pageSize,
            @RequestParam(value = "pageNum") Integer pageNum
            ) {
        try{

            if (StringUtils.isEmpty(beginTime)) {
                beginTime = "1970-00-00 00:00:00";
            }
            if(StringUtils.isEmpty(endTime)){
                endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }

            if(flag == 5){
                if(StringUtils.isEmpty(searchTime)){
                    return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"参数格式不正确",ResponseStatus.CODE_WRONG.getValue());
                }
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),orderService.orderListService(flag,recordId,financingName,orderCollection,beginTime,endTime,searchTime,orderFaildStatus,redeemptionStatus,pageSize,pageNum),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("获取订单列表失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取订单列表失败",ResponseStatus.CODE_WRONG.getValue());
    }


    @ApiOperation("进行中_订单_详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "订单id", required = true, dataType = "String", paramType = "query")
    })
       @LoginRequired
        @RequestMapping(value = "/orderGoingDetail", method = RequestMethod.POST)
    public ResponseResult orderGoingDetail(HttpServletRequest request, @RequestParam(value = "recordId") Integer recordId) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),orderService.orderGoingDetailService(recordId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("获取进行中订单详情失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取进行中订单详情失败",ResponseStatus.CODE_WRONG.getValue());
    }


    @ApiOperation("已赎回_订单_详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "订单id", required = true, dataType = "String", paramType = "query")
    })
     @LoginRequired
    @RequestMapping(value = "/orderRedeemptionDetail", method = RequestMethod.POST)
    public ResponseResult orderRedeemptionDetail(HttpServletRequest request, @RequestParam(value = "recordId") Integer recordId) {
        try{

            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),orderService.orderRedeemptionDetailService(recordId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("获取已赎回订单详情",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取已赎回订单详情",ResponseStatus.CODE_WRONG.getValue());
    }


    @ApiOperation("已完成_订单_详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "订单id", required = true, dataType = "String", paramType = "query")
    })
      @LoginRequired
        @RequestMapping(value = "/orderFinishDetail", method = RequestMethod.POST)
    public ResponseResult orderFinishDetail(HttpServletRequest request, @RequestParam(value = "recordId") Integer recordId) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),orderService.orderFinishDetailService(recordId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金项目获取失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金项目获取失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("失败_订单_详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "订单id", required = true, dataType = "String", paramType = "query")
    })
      @LoginRequired
    @RequestMapping(value = "/orderFaildDetail", method = RequestMethod.POST)
    public ResponseResult orderFaildDetail(HttpServletRequest request, @RequestParam(value = "recordId") Integer recordId) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),orderService.orderFaildDetailService(recordId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("获取失败订单详情失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取失败订单详情失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("当日_订单_详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "订单id", required = true, dataType = "String", paramType = "query")
    })
      @LoginRequired
    @RequestMapping(value = "/orderEveryDayDetail", method = RequestMethod.POST)
    public ResponseResult orderEveryDayDetail(HttpServletRequest request, @RequestParam(value = "recordId") Integer recordId) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),orderService.orderEveryDayDetailService(recordId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("获取失败订单详情失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取失败订单详情失败",ResponseStatus.CODE_WRONG.getValue());
    }



    @ApiOperation("失败_状态切换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "failOrderStatus", value = "失败订单处理状态 ：0 表示为处理 1表示已处理", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "recordId", value = "订单id", required = true, dataType = "String", paramType = "query")
    })
      @LoginRequired
    @RequestMapping(value = "/failOrderStatusChange", method = RequestMethod.POST)
    public ResponseResult changeRedeemptionStatus(HttpServletRequest request, @RequestParam(value = "failOrderStatus") Integer failOrderStatus,@RequestParam(value = "recordId") Integer recordId) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),orderService.failOrderStatusChangeService(failOrderStatus,recordId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("获取失败订单详情失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取失败订单详情失败",ResponseStatus.CODE_WRONG.getValue());
    }


}
