package com.bcb.controller;

import com.bcb.bean.JSONParam;
import com.bcb.bean.MyResponseResult;
import com.bcb.bean.ResultDtoFactory;
import com.bcb.bean.dto.financing.UserInverstDetailDto;
import com.bcb.bean.dto.financing.UserInverstDetailResponse;
import com.bcb.bean.dto.financing.UserInverstStatisticsResponse;
import com.bcb.service.FinancingService;
import com.bcb.util.MapUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author:zhw
 * @Date 20180524
 * @description:理财查询Controller
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/financing")
@Api(value="用户理财controller")
public class FinancingQueryController {

    @Autowired
    private FinancingService financingService;


    /**
     * 查询用户理财投资统计
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user/invest/rate", method = {RequestMethod.POST})
    @ApiOperation(value="查询用户理财投资统计")
    public MyResponseResult getInvestFinancingStatistics(@RequestParam(required = true) Integer userId) {
        try{
            Map<String, Object> map = new HashMap<>();
            List<UserInverstStatisticsResponse> resultList = financingService.getInvestFinancingStatistics(userId);
            if (!CollectionUtils.isEmpty(resultList)){
                for (UserInverstStatisticsResponse dto : resultList) {
                    map.put(dto.getTitle()+"投资", dto.getCoinAmount()+"BCB_" + dto.getUsdxAmount()+"USDX");
                }
            }
            return ResultDtoFactory.toSuccess("请求成功",map);
        }catch (Exception e){
            log.error("查询用户理财投资统计失败",e);
        }
        return ResultDtoFactory.toInternalFail("查询用户理财投资统计失败","");
    }


    /**
     * 查询用户理财投资统计
     * @return
     */
    @RequestMapping(value = "/user/invest/list", method = {RequestMethod.POST})
    @ApiOperation(value="查询用户理财投资列表")
    public MyResponseResult getInvestFinancingStatistics(@RequestBody JSONParam[] params) {
        try{
            Map<String, String> paramMap = MapUtil.convertToMap(params);
            String sEcho = paramMap.get("sEcho");
            Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
            Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
            Integer userId = Integer.parseInt(paramMap.get("userId"));


            Integer  count = financingService.getInvestFinancingCount(userId);

            List<UserInverstDetailDto> data= new ArrayList<>();
            if(count!=null&&count!=0){
                data = financingService.getInvestFinancingList(userId,start,length);
            }
            return ResultDtoFactory.toSuccess("请求成功",new UserInverstDetailResponse(sEcho,count,count,data));
        }catch (Exception e){
            log.error("查询用户理财投资列表失败",e);
        }
        return ResultDtoFactory.toInternalFail("查询用户理财投资列表失败","");
    }


}
