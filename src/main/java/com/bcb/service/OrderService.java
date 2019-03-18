package com.bcb.service;

import com.bcb.config.RedisClient;
import com.bcb.domain.dao.OrderDao;
import com.bcb.domain.entity.FinancingBaseInfo;
import com.bcb.domain.entity.UserAccountInfo;
import com.bcb.domain.entity.UserFinancingRecord;
import com.bcb.domain.entity.UserFinancingSettlement;
import com.bcb.domain.repository.FinancingBaseInfoRepository;
import com.bcb.domain.repository.UserAccountInfoRepository;
import com.bcb.domain.repository.UserFinancingRecordRepository;
import com.bcb.domain.repository.UserFinancingSettlementRepository;
import com.github.pagehelper.PageInfo;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisClient redisClient;

    @Autowired

    UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    FinancingBaseInfoRepository financingBaseInfoRepository;

    @Autowired
    UserAccountInfoRepository userAccountInfoRepository;

    @Autowired
    UserFinancingSettlementRepository userFinancingSettlementRepository;


    public PageInfo<Map<String,String>> orderListService(Integer flag,Integer recordId, String financingName, String orderCollection, String beginTime, String endTime,String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer pageSize, Integer pageNum) throws Exception {
        PageInfo<Map<String,String>> pageInfo  = new PageInfo<>();
        try{
            if(flag == 1){
                    OrderGoingList(flag,recordId, financingName, orderCollection, beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  pageSize,  pageNum,pageInfo);
            }else if(flag == 2){
                OrderRedeemptionList(flag,recordId,financingName, orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  pageSize,  pageNum,pageInfo);
            }else if(flag == 3){
                OrderFinishList(flag,recordId, financingName, orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  pageSize,  pageNum,pageInfo);
            }else if ( flag == 4){
                OrderFaildList(flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  pageSize,  pageNum,pageInfo);
            }else if( flag == 5){
               OrderEveryDayList(flag,recordId,  financingName,  orderCollection,  beginTime,  endTime, searchTime, orderFaildStatus,  redeemptionStatus,  pageSize,  pageNum,pageInfo);
            }
            return pageInfo;
        }catch (Exception e){
            throw new Exception("构建体验金用户列表失败",e);
        }
    }


    //进行中的订单列表
    public void OrderGoingList(Integer flag, Integer recordId, String financingName, String orderCollection, String beginTime, String endTime,String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer pageSize, Integer pageNum, PageInfo<Map<String, String>> pageInfo ) throws Exception {
        List<Map<String,String>> listGoing = new ArrayList<>();
        List<Map<String,Object>> listGoingObject = orderDao.OrderList( flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize);
        if(!ObjectUtils.isEmpty(listGoingObject)){
            for (int i = 0; i < listGoingObject.size(); i++){
                Map<String,String> listGoingMap = new HashMap<>();
                Map<String,Object> map = listGoingObject.get(i);
                listGoingMap.put("recordId",resultStrFormate(map.get("recordId"),false));
                listGoingMap.put("financingName",resultStrFormate(map.get("financingName"),false));
                listGoingMap.put("userId",resultStrFormate(map.get("userId"),false));
                listGoingMap.put("disPlayName",resultStrFormate(map.get("disPlayName"),false));
                listGoingMap.put("address",resultStrFormate(map.get("address"),false));
                listGoingMap.put("investMent",investMentStr(map.get("coinName"),map.get("amount"),map.get("usdxAmount")));
                listGoingMap.put("recordCreateTime",dateStr(resultStrFormate(map.get("recordCreateTime"),false),"yyyy-MM-dd HH:mm:ss"));
                listGoingMap.put("surplusDay",surplusDay(map.get("recordCreateTime"),map.get("expireTime")));
                listGoing.add(listGoingMap);
            }
            pageInfo.setList(listGoing);
            pageInfo.setTotal(orderDao.OrderListNum( flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize));
        }else{
            pageInfo.setList(listGoing);
            pageInfo.setTotal(0);
        }

    }

    //订单完成的列表
    public  void OrderFinishList(Integer flag, Integer recordId, String financingName, String orderCollection, String beginTime, String endTime,String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer pageSize, Integer pageNum, PageInfo<Map<String, String>> pageInfo ) throws Exception {
        List<Map<String,String>> listFinish = new ArrayList<>();
        List<Map<String,Object>> listFinishObject = orderDao.OrderList(  flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize);
        if(!ObjectUtils.isEmpty(listFinishObject)){
            for (int i = 0; i < listFinishObject.size(); i++){
                Map<String,String> resultMap = new HashMap<>();
                Map<String,Object> map = listFinishObject.get(i);
                resultMap.put("recordId",resultStrFormate(map.get("recordId"),false));
                resultMap.put("financingName",resultStrFormate(map.get("financingName"),false));
                resultMap.put("userId",resultStrFormate(map.get("userId"),false));
                resultMap.put("disPlayName",resultStrFormate(map.get("disPlayName"),false));
                resultMap.put("address",resultStrFormate(map.get("address"),false));
                resultMap.put("investMent",investMentStr(map.get("coinName"),map.get("amount"),map.get("usdxAmount")));
                resultMap.put("recordCreateTime",dateStr(resultStrFormate(map.get("recordCreateTime"),false),"yyyy-MM-dd HH:mm:ss"));
                listFinish.add(resultMap);
            }
            pageInfo.setList(listFinish);
            pageInfo.setTotal(orderDao.OrderListNum( flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize));
        }else{
            pageInfo.setList(listFinish);
            pageInfo.setTotal(0);
        }
    }

    //订单赎回列表
    public  void OrderRedeemptionList(Integer flag, Integer recordId, String financingName, String orderCollection, String beginTime, String endTime,String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer pageSize, Integer pageNum, PageInfo<Map<String, String>> pageInfo ) throws Exception {
        List<Map<String,String>> listRedeemption = new ArrayList<>();
        List<Map<String,Object>> listRedeemptionObject = orderDao.OrderList(  flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize);
        if(!ObjectUtils.isEmpty(listRedeemptionObject)){
            for (int i = 0; i < listRedeemptionObject.size(); i++){
                Map<String,String> resultMap = new HashMap<>();
                Map<String,Object> map = listRedeemptionObject.get(i);
                resultMap.put("recordId",resultStrFormate(map.get("recordId"),false));
                resultMap.put("financingName",resultStrFormate(map.get("financingName"),false));
                resultMap.put("userId",resultStrFormate(map.get("userId"),false));
                resultMap.put("disPlayName",resultStrFormate(map.get("disPlayName"),false));
                resultMap.put("address",resultStrFormate(map.get("address"),false));
                resultMap.put("investMent",investMentStr(map.get("coinName"),map.get("amount"),map.get("usdxAmount")));
                resultMap.put("recordCreateTime",dateStr(resultStrFormate(map.get("recordCreateTime"),false),"yyyy-MM-dd HH:mm:ss"));
                resultMap.put("surplusDay",surplusDay(map.get("recordCreateTime"),map.get("expireTime")));
                resultMap.put("redeemptionStatus",redeemptionStatusStr(map.get("redeemptionStatus")));
                listRedeemption.add(resultMap);
            }
            pageInfo.setList(listRedeemption);
            pageInfo.setTotal(orderDao.OrderListNum( flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize));
        }else{
            pageInfo.setList(listRedeemption);
            pageInfo.setTotal(0);
        }
    }


    public void OrderEveryDayList(Integer flag, Integer recordId, String financingName, String orderCollection, String beginTime, String endTime, String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer pageSize, Integer pageNum, PageInfo<Map<String, String>> pageInfo) throws Exception {
        List<Map<String,String>> listEveryDay = new ArrayList<>();
        List<Map<String,Object>> listEveryDayObject = orderDao.OrderList(  flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize);
        if(!ObjectUtils.isEmpty(listEveryDayObject)){
            for (int i = 0; i < listEveryDayObject.size(); i++){
                Map<String,String> resultMap = new HashMap<>();
                Map<String,Object> map = listEveryDayObject.get(i);
                resultMap.put("recordId",resultStrFormate(map.get("recordId"),false));
                resultMap.put("financingName",resultStrFormate(map.get("financingName"),false));
                resultMap.put("userId",resultStrFormate(map.get("userId"),false));
                resultMap.put("disPlayName",resultStrFormate(map.get("disPlayName"),false));
                resultMap.put("address",resultStrFormate(map.get("address"),false));
                resultMap.put("investMent",investMentStr(map.get("coinName"),map.get("amount"),map.get("usdxAmount")));
                resultMap.put("recordCreateTime",dateStr(resultStrFormate(map.get("recordCreateTime"),false),"yyyy-MM-dd HH:mm:ss"));
                listEveryDay.add(resultMap);
            }
            pageInfo.setList(listEveryDay);
            pageInfo.setTotal(orderDao.OrderListNum( flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize));
        }else{
            pageInfo.setList(listEveryDay);
            pageInfo.setTotal(0);
        }
    }


    //订单失败列表
    public void OrderFaildList(Integer flag, Integer recordId, String financingName, String orderCollection, String beginTime, String endTime,String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer pageSize, Integer pageNum, PageInfo<Map<String, String>> pageInfo ) throws Exception {
        List<Map<String,String>> listFaild = new ArrayList<>();
        List<Map<String,Object>> listFaildObject = orderDao.OrderList(  flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize);
        if(!ObjectUtils.isEmpty(listFaildObject)){
            for (int i = 0; i < listFaildObject.size(); i++){
                Map<String,String> resultMap = new HashMap<>();
                Map<String,Object> map = listFaildObject.get(i);
                resultMap.put("recordId",resultStrFormate(map.get("recordId"),false));
                resultMap.put("financingName",resultStrFormate(map.get("financingName"),false));
                resultMap.put("userId",resultStrFormate(map.get("userId"),false));
                resultMap.put("disPlayName",resultStrFormate(map.get("disPlayName"),false));
                resultMap.put("address",resultStrFormate(map.get("address"),false));
                resultMap.put("investMent",investMentStr(map.get("coinName"),map.get("amount"),map.get("usdxAmount")));
                resultMap.put("recordCreateTime",dateStr(resultStrFormate(map.get("recordCreateTime"),false),"yyyy-MM-dd HH:mm:ss"));
                resultMap.put("failOrderStatus",failOrderStatusStr(map.get("orderFaildStatus")));
                listFaild.add(resultMap);
            }
            pageInfo.setList(listFaild);
            pageInfo.setTotal(orderDao.OrderListNum( flag,recordId,  financingName,  orderCollection,  beginTime,  endTime,searchTime,  orderFaildStatus,  redeemptionStatus,  (pageNum-1)*pageSize,  pageSize));
        }else{
            pageInfo.setList(listFaild);
            pageInfo.setTotal(0);
        }
    }

    //进行中订单详情
    public List<List<Map<String,String>>> orderGoingDetailService(Integer recordId) throws Exception {
        List<List<Map<String,String>>> list = new ArrayList<>();
        try{
            UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(recordId);
            FinancingBaseInfo financingBaseInfo = financingBaseInfoRepository.findOneByFinancingUuid(userFinancingRecord.getFinancingUuid());
            UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(userFinancingRecord.getUserId());
            if (ObjectUtils.isEmpty(userAccountInfo) || ObjectUtils.isEmpty(financingBaseInfo) || ObjectUtils.isEmpty(userAccountInfo))
                throw new Exception("数据完整性异常");
            List<Map<String,String>> listrecord = new ArrayList<>();
            listrecord.add(mapDetailOrder("recordId",userFinancingRecord.getId()));   //订单id
            listrecord.add(mapDetailOrder("recordStatus","进行中"));    //订单状态
            listrecord.add(mapDetailOrder("surplusDay",surplusDay(userFinancingRecord.getRecordCreateTime(),userFinancingRecord.getExpireTime())));  //订单理财剩余天数
            listrecord.add(mapDetailOrder("recordCreateTime",dateStr(userFinancingRecord.getRecordCreateTime().toString(),"yyyy-MM-dd HH:mm:ss")));   //订单计息时间
            listrecord.add(mapDetailOrder("expireTime",dateStr(userFinancingRecord.getExpireTime().toString(),"yyyy-MM-dd HH:mm:ss"))); //订单到期时间
            listrecord.add(mapDetailOrder("paymentModes","到期一次性还本付息"));   //回款方式
            listrecord.add(mapDetailOrder("paymentCoinType",financingBaseInfo.getCoinType()));  //计算币种
            list.add(listrecord);
            List<Map<String,String>> listTotal = new ArrayList<>();
            listTotal.add(mapDetailOrder("totalIncomeUsdx",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit())));  //总收益usdx
            listTotal.add(mapDetailOrder("fixedUsdxAmount",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit())));   //固定收益
            listTotal.add(mapDetailOrder("extraUsdxAmount","-"));   //超额收益分佣
            list.add(listTotal);
            List<Map<String,String>> listIncome = new ArrayList<>();
            listIncome.add(mapDetailOrder("principalUsdxAmount",bigDecimalStr(userFinancingRecord.getUsdxAmount(),1) + "USDX"));  //投资本金
            listIncome.add(mapDetailOrder("subscriptionFee",bigDecimalStr(userFinancingRecord.getSubscriptionFee(),1) + "USDX"));   //认购费
            listIncome.add(mapDetailOrder("subscriptionFeeRate",bigDecimalStr(userFinancingRecord.getSubscriptionFeeRate(),100) + "%"));   //认购费率
            listIncome.add(mapDetailOrder("financingCoinType",financingBaseInfo.getCoinType()));  //理财币种
            listIncome.add(mapDetailOrder("startBCBPrice",bigDecimalStr(userFinancingRecord.getBcb2usdx(),1) + "USDX"));  //起息价格
            listIncome.add(mapDetailOrder("endBCBPrice",bigDecimalStr(bcb2UsdxPrice(),1)  + "USDX"));   //当前价格
            listIncome.add(mapDetailOrder("currentYield",currentYield(userFinancingRecord.getBcb2usdx()) + "%"));  //当前收益率
            listIncome.add(mapDetailOrder("fixedUsdxRate",sumFixedUsdxRate(userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit()) + "%"));  //保底收益率
            listIncome.add(mapDetailOrder("extraRate","-"));  //超额收益率
            listIncome.add(mapDetailOrder("fixedUsdxAmount",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit()) + "USDX"));   //固定收益
            listIncome.add(mapDetailOrder("extraInCome","-"));  //超额收益
            listIncome.add(mapDetailOrder("selfCommission","-"));   //本人分佣
            listIncome.add(mapDetailOrder("superiorCommissionRate","-"));   //资金担保机构分佣
            listIncome.add(mapDetailOrder("foundationCommission","-"));  //基金公司绩效佣金
            list.add(listIncome);
            List<Map<String,String>> listfinancing = new ArrayList<>();
            listfinancing.add(mapDetailOrder("financingName",financingBaseInfo.getTitle())); // 项目名称
            listfinancing.add(mapDetailOrder("userId",userFinancingRecord.getUserId()));  //获取用户id
            listfinancing.add(mapDetailOrder("userName",userAccountInfo.getDisplayName()));  //用户昵称
            listfinancing.add(mapDetailOrder("fromAddress",userFinancingRecord.getFromAddress())); //用户钱包地址
            listfinancing.add(mapDetailOrder("toAddress",userFinancingRecord.getToAddress()));  //平台钱包地址
            listfinancing.add(mapDetailOrder("incomAmount",investMentStr(userFinancingRecord.getCoinName(), userFinancingRecord.getAmount(), userFinancingRecord.getUsdxAmount())));
            listfinancing.add(mapDetailOrder("recordCreateTime",dateStr(userFinancingRecord.getRecordCreateTime().toString(),"yyyy-MM-dd HH:mm:ss")));
            listfinancing.add(mapDetailOrder("txId",userFinancingRecord.getTxId()));
            list.add(listfinancing);
            return list;
        }catch (Exception e){
            throw new Exception("获取进行中订单详情异常",e);
        }
    }


    //已赎回订单详情
    public List<List<Map<String,String>>> orderRedeemptionDetailService(Integer recordId) throws Exception {
        List<List<Map<String,String>>> list = new ArrayList<>();
        try{
            UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(recordId);
            FinancingBaseInfo financingBaseInfo = financingBaseInfoRepository.findOneByFinancingUuid(userFinancingRecord.getFinancingUuid());
            UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(userFinancingRecord.getUserId());
            List<UserFinancingSettlement> userFinancingSettlementList = userFinancingSettlementRepository.findByUserFinancingRecordId(userFinancingRecord.getId());
            if (ObjectUtils.isEmpty(userAccountInfo) || ObjectUtils.isEmpty(financingBaseInfo) || ObjectUtils.isEmpty(userAccountInfo))
                throw new Exception("数据完整性异常");
            UserFinancingSettlement userFinancingSettlement = sumUserFinancingSettlement(userFinancingSettlementList);
            List<Map<String,String>> listrecord = new ArrayList<>();
            listrecord.add(mapDetailOrder("recordId",userFinancingRecord.getId()));   //订单id
            listrecord.add(mapDetailOrder("recordStatus",redeemptionStatusStr(userFinancingRecord.getRedeemptionStatus())));    //订单状态
            listrecord.add(mapDetailOrder("surplusDay","-"));  //订单理财剩余天数
            listrecord.add(mapDetailOrder("recordCreateTime",dateStr(userFinancingRecord.getRecordCreateTime().toString(),"yyyy-MM-dd HH:mm:ss")));   //订单计息时间
            listrecord.add(mapDetailOrder("expireTime",dateStr(userFinancingRecord.getExpireTime().toString(),"yyyy-MM-dd HH:mm:ss"))); //订单到期时间
            listrecord.add(mapDetailOrder("paymentModes","到期一次性还本付息"));   //回款方式
            listrecord.add(mapDetailOrder("paymentCoinType",financingBaseInfo.getCoinType()));  //计算币种
            list.add(listrecord);
            List<Map<String,String>> listTotal = new ArrayList<>();
            if(userFinancingRecord.getRedeemptionStatus() == 1 ){
                listTotal.add(mapDetailOrder("totalIncomeUsdx","-"));  //总收益usdx
                listTotal.add(mapDetailOrder("fixedUsdxAmount","-"));   //固定收益
                listTotal.add(mapDetailOrder("extraUsdxAmount","-"));   //超额收益分佣
            }else if(userFinancingRecord.getRedeemptionStatus() == 3){
                listTotal.add(mapDetailOrder("totalIncomeUsdx",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit())));  //总收益usdx
                listTotal.add(mapDetailOrder("fixedUsdxAmount",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit())));   //固定收益
                listTotal.add(mapDetailOrder("extraUsdxAmount","-"));   //超额收益分佣
            }else if(userFinancingRecord.getRedeemptionStatus() == 2){
                if(StringUtils.isEmpty(userFinancingSettlement.getPrincipalRate())){
                    listTotal.add(mapDetailOrder("totalIncomeUsdx",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit())));  //总收益usdx
                    listTotal.add(mapDetailOrder("fixedUsdxAmount",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit())));   //固定收益
                    listTotal.add(mapDetailOrder("extraUsdxAmount","-"));   //超额收益分佣
                }else{
                    listTotal.add(mapDetailOrder("totalIncomeUsdx",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount().add(userFinancingSettlement.getFixedUsdxAmount()),1))); //总收益usdx
                    listTotal.add(mapDetailOrder("fixedUsdxAmount",bigDecimalStr(userFinancingSettlement.getFixedUsdxAmount(),1)));   //固定收益
                    listTotal.add(mapDetailOrder("extraUsdxAmount",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount(),1) ));   //超额收益分佣
                }
            }

            list.add(listTotal);
            List<Map<String,String>> listIncome = new ArrayList<>();
            listIncome.add(mapDetailOrder("principalUsdxAmount",bigDecimalStr(userFinancingRecord.getUsdxAmount(),1) + "USDX"));  //投资本金
            listIncome.add(mapDetailOrder("subscriptionFee",bigDecimalStr(userFinancingRecord.getSubscriptionFee(),1)  + "USDX"));   //认购费
            listIncome.add(mapDetailOrder("subscriptionFeeRate",bigDecimalStr(userFinancingRecord.getSubscriptionFeeRate(),100) + "%"));   //认购费率
            listIncome.add(mapDetailOrder("financingCoinType",financingBaseInfo.getCoinType()));  //理财币种
            if(userFinancingRecord.getRedeemptionStatus() == 1 || userFinancingRecord.getRedeemptionStatus() == 3){
                listIncome.add(mapDetailOrder("startBCBPrice","-"));  //起息价格
                listIncome.add(mapDetailOrder("endBCBPrice","-"));   //当前价格
            }else{
                listIncome.add(mapDetailOrder("startBCBPrice",bigDecimalStr(userFinancingRecord.getBcb2usdx(),1) + "USDX"));  //起息价格
                listIncome.add(mapDetailOrder("endBCBPrice",bigDecimalStr(bcb2UsdxPrice(),1) + "USDX"));   //当前价格
            }

            if(userFinancingRecord.getRedeemptionStatus() == 1 || userFinancingRecord.getRedeemptionStatus() == 3){
                listIncome.add(mapDetailOrder("currentYield","-"));   //当前收益率
            }else{
                listIncome.add(mapDetailOrder("currentYield",currentYield(userFinancingRecord.getBcb2usdx()) + "%"));  //当前收益率
            }
            listIncome.add(mapDetailOrder("fixedUsdxRate",sumFixedUsdxRate(userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit()) + "%"));  //保底收益率

            if(userFinancingRecord.getRedeemptionStatus() == 2){
                if(StringUtils.isEmpty(userFinancingSettlement.getPrincipalRate())){
                    listIncome.add(mapDetailOrder("extraRate","-"));  //超额收益率
                }else{
                    listIncome.add(mapDetailOrder("extraInCome",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount(),1) + "USDX"));  //超额收益
                }

            }else {
                listIncome.add(mapDetailOrder("extraRate","-"));  //超额收益率
            }
            if(userFinancingRecord.getRedeemptionStatus() == 1){
                listIncome.add(mapDetailOrder("fixedUsdxAmount","-"));   //固定收益
            }else{
                listIncome.add(mapDetailOrder("fixedUsdxAmount",sumFixedUsdxAmount(userFinancingRecord.getUsdxAmount(),userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit()) + "USDX"));   //固定收益
            }
            if(userFinancingRecord.getRedeemptionStatus() == 2){
                if(StringUtils.isEmpty(userFinancingSettlement.getPrincipalRate())){
                    listIncome.add(mapDetailOrder("extraInCome","-"));  //超额收益
                    listIncome.add(mapDetailOrder("selfCommission","-"));   //本人分佣
                    listIncome.add(mapDetailOrder("superiorCommissionRate","-"));  //资金担保机构分佣
                    listIncome.add(mapDetailOrder("foundationCommission","-"));  //基金公司绩效佣金
                }else{
                    listIncome.add(mapDetailOrder("extraInCome",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount(),1) + "USDX"));  //超额收益
                    listIncome.add(mapDetailOrder("selfCommission",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount().subtract(userFinancingSettlement.getBcb2usdx().multiply(userFinancingSettlement.getSuperiorCommission().add(userFinancingSettlement.getSuperiorCommissionTax()))).subtract(userFinancingSettlement.getFoundationCommission().multiply(userFinancingSettlement.getBcb2usdx())).setScale(3,BigDecimal.ROUND_HALF_UP),1) + "USDX"));   //本人分佣
                    listIncome.add(mapDetailOrder("superiorCommissionRate",bigDecimalStr(userFinancingSettlement.getBcb2usdx().multiply(userFinancingSettlement.getSuperiorCommission()),1) + "USDX"));  //资金担保机构分佣
                    listIncome.add(mapDetailOrder("foundationCommission",bigDecimalStr(userFinancingSettlement.getBcb2usdx().multiply(userFinancingSettlement.getFoundationCommission()),1) + "USDX"));  //基金公司绩效佣金
                }

            }else {
                listIncome.add(mapDetailOrder("extraInCome","-"));  //超额收益
                listIncome.add(mapDetailOrder("selfCommission","-"));   //本人分佣
                listIncome.add(mapDetailOrder("superiorCommissionRate","-"));  //资金担保机构分佣
                listIncome.add(mapDetailOrder("foundationCommission","-"));  //基金公司绩效佣金
            }

            list.add(listIncome);
            List<Map<String,String>> listfinancing = new ArrayList<>();
            listfinancing.add(mapDetailOrder("financingName",financingBaseInfo.getTitle())); // 项目名称
            listfinancing.add(mapDetailOrder("userId",userFinancingRecord.getUserId()));  //获取用户id
            listfinancing.add(mapDetailOrder("userName",userAccountInfo.getDisplayName()));  //用户昵称
            listfinancing.add(mapDetailOrder("fromAddress",userFinancingRecord.getFromAddress())); //用户钱包地址
            listfinancing.add(mapDetailOrder("toAddress",userFinancingRecord.getToAddress()));  //平台钱包地址
            listfinancing.add(mapDetailOrder("incomAmount",investMentStr(userFinancingRecord.getCoinName(), userFinancingRecord.getAmount(), userFinancingRecord.getUsdxAmount())));
            listfinancing.add(mapDetailOrder("recordCreateTime",dateStr(userFinancingRecord.getRecordCreateTime().toString(),"yyyy-MM-dd HH:mm:ss")));
            listfinancing.add(mapDetailOrder("txId",userFinancingRecord.getTxId()));
            list.add(listfinancing);
            return list;
        }catch (Exception e){
            throw new Exception("获取已赎回订单详情异常",e);
        }

    }


    //已完成订单详情
    public List<List<Map<String,String>>> orderFinishDetailService(Integer recordId) throws Exception {
        List<List<Map<String,String>>> list = new ArrayList<>();
        try{
            UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(recordId);
            FinancingBaseInfo financingBaseInfo = financingBaseInfoRepository.findOneByFinancingUuid(userFinancingRecord.getFinancingUuid());
            UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(userFinancingRecord.getUserId());
            List<UserFinancingSettlement> userFinancingSettlementList = userFinancingSettlementRepository.findByUserFinancingRecordId(userFinancingRecord.getId());
            if (ObjectUtils.isEmpty(userAccountInfo) || ObjectUtils.isEmpty(financingBaseInfo) || ObjectUtils.isEmpty(userAccountInfo) || ObjectUtils.isEmpty(userFinancingSettlementList))
                throw new Exception("数据完整性异常");
            UserFinancingSettlement userFinancingSettlement = sumUserFinancingSettlement(userFinancingSettlementList);
            List<Map<String,String>> listrecord = new ArrayList<>();
            listrecord.add(mapDetailOrder("recordId",userFinancingRecord.getId()));   //订单id
            listrecord.add(mapDetailOrder("recordStatus","已完成"));    //订单状态
            listrecord.add(mapDetailOrder("recordCreateTime",dateStr(userFinancingRecord.getRecordCreateTime().toString(),"yyyy-MM-dd HH:mm:ss")));   //订单计息时间
            listrecord.add(mapDetailOrder("expireTime",dateStr(userFinancingRecord.getExpireTime().toString(),"yyyy-MM-dd HH:mm:ss"))); //订单到期时间
            listrecord.add(mapDetailOrder("paymentModes","到期一次性还本付息"));   //回款方式
            listrecord.add(mapDetailOrder("paymentCoinType",financingBaseInfo.getCoinType()));  //计算币种
            list.add(listrecord);
            List<Map<String,String>> listTotal = new ArrayList<>();
            listTotal.add(mapDetailOrder("totalIncomeUsdx",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount().add(userFinancingSettlement.getFixedUsdxAmount()),1))); //总收益usdx
            listTotal.add(mapDetailOrder("fixedUsdxAmount",bigDecimalStr(userFinancingSettlement.getFixedUsdxAmount(),1)));   //固定收益
            listTotal.add(mapDetailOrder("fixedBCBAmount",bigDecimalStr(userFinancingSettlement.getFixedBcbAmount(),1) + "BCB"));   //固定收益
            listTotal.add(mapDetailOrder("extraUsdxAmount",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount(),1) ));   //超额收益分佣
            listTotal.add(mapDetailOrder("extraBCBAmount",bigDecimalStr(userFinancingSettlement.getExtraBcbAmount(),1) + "BCB"));   //超额收益分佣
            list.add(listTotal);
            List<Map<String,String>> listIncome = new ArrayList<>();
            listIncome.add(mapDetailOrder("principalUsdxAmount",bigDecimalStr(userFinancingRecord.getUsdxAmount(),1) + "USDX"));  //投资本金
            listIncome.add(mapDetailOrder("subscriptionFee",bigDecimalStr(userFinancingRecord.getSubscriptionFee(),1)));   //认购费
            listIncome.add(mapDetailOrder("subscriptionFeeRate",bigDecimalStr(userFinancingRecord.getSubscriptionFeeRate(),100) + "%"));   //认购费率
            listIncome.add(mapDetailOrder("financingCoinType",financingBaseInfo.getCoinType()));  //理财币种
            listIncome.add(mapDetailOrder("startBCBPrice",bigDecimalStr(userFinancingRecord.getBcb2usdx(),1) + "USDX"));  //起息价格
            listIncome.add(mapDetailOrder("endBCBPrice",bigDecimalStr(userFinancingSettlement.getBcb2usdx(),1) + "USDX"));   //当前价格
            listIncome.add(mapDetailOrder("settleYield",bigDecimalStr(userFinancingSettlement.getBcb2usdx().divide(userFinancingRecord.getBcb2usdx(),5,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100).setScale(3,BigDecimal.ROUND_HALF_UP)),1) + "%"));  //结算收益率
            listIncome.add(mapDetailOrder("fixedUsdxRate",sumFixedUsdxRate(userFinancingRecord.getAnnualRate(),userFinancingRecord.getServiceRate(),financingBaseInfo.getFreezeNumber(),financingBaseInfo.getFreezeUnit()) + "%" ));  //保底收益率
            listIncome.add(mapDetailOrder("extraRate",userFinancingSettlement.getExtraUsdxAmount().divide(userFinancingRecord.getUsdxAmount(),5,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(3,BigDecimal.ROUND_HALF_UP) + "%"));  //超额收益率
            listIncome.add(mapDetailOrder("fixedUsdxAmount",bigDecimalStr(userFinancingSettlement.getFixedUsdxAmount(),1) + "USDX"));   //固定收益
            listIncome.add(mapDetailOrder("extraInCome",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount(),1) + "USDX"));  //超额收益
            listIncome.add(mapDetailOrder("selfCommission",bigDecimalStr(userFinancingSettlement.getExtraUsdxAmount().subtract(userFinancingSettlement.getBcb2usdx().multiply(userFinancingSettlement.getSuperiorCommission().add(userFinancingSettlement.getSuperiorCommissionTax()))).subtract(userFinancingSettlement.getFoundationCommission().multiply(userFinancingSettlement.getBcb2usdx())).setScale(3,BigDecimal.ROUND_HALF_UP),1) + "USDX"));   //本人分佣
            listIncome.add(mapDetailOrder("superiorCommissionRate",bigDecimalStr(userFinancingSettlement.getBcb2usdx().multiply(userFinancingSettlement.getSuperiorCommission()),1) + "USDX"));  //资金担保机构分佣
            listIncome.add(mapDetailOrder("foundationCommission",bigDecimalStr(userFinancingSettlement.getBcb2usdx().multiply(userFinancingSettlement.getFoundationCommission()),1) + "USDX"));  //基金公司绩效佣金
            list.add(listIncome);
            List<Map<String,String>> listfinancing = new ArrayList<>();
            listfinancing.add(mapDetailOrder("financingName",financingBaseInfo.getTitle())); // 项目名称
            listfinancing.add(mapDetailOrder("userId",userFinancingRecord.getUserId()));  //获取用户id
            listfinancing.add(mapDetailOrder("userName",userAccountInfo.getDisplayName()));  //用户昵称
            listfinancing.add(mapDetailOrder("fromAddress",userFinancingRecord.getFromAddress())); //用户钱包地址
            listfinancing.add(mapDetailOrder("toAddress",userFinancingRecord.getToAddress()));  //平台钱包地址
            listfinancing.add(mapDetailOrder("incomAmount",investMentStr(userFinancingRecord.getCoinName(), userFinancingRecord.getAmount(), userFinancingRecord.getUsdxAmount())));
            listfinancing.add(mapDetailOrder("recordCreateTime",dateStr(userFinancingRecord.getRecordCreateTime().toString(),"yyyy-MM-dd HH:mm:ss")));
            listfinancing.add(mapDetailOrder("txId",userFinancingRecord.getTxId()));
            list.add(listfinancing);
            return list;
        }catch (Exception e){
            throw new Exception("获取已完成订单详情异常",e);
        }

    }

    //失败订单详情
    public List<List<Map<String,String>>> orderFaildDetailService(Integer recordId) throws Exception {
        List<List<Map<String,String>>> list = new ArrayList<>();
        try{
            UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(recordId);
            FinancingBaseInfo financingBaseInfo = financingBaseInfoRepository.findOneByFinancingUuid(userFinancingRecord.getFinancingUuid());
            UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(userFinancingRecord.getUserId());
            if (ObjectUtils.isEmpty(userAccountInfo) || ObjectUtils.isEmpty(financingBaseInfo) || ObjectUtils.isEmpty(userAccountInfo))
                throw new Exception("数据完整性异常");
            List<Map<String,String>> listrecord = new ArrayList<>();
            listrecord.add(mapDetailOrder("recordId",userFinancingRecord.getId()));   //订单id
            listrecord.add(mapDetailOrder("recordStatus","已失败"));    //订单状态
            listrecord.add(mapDetailOrder("recordCreateTime","-"));   //订单计息时间
            listrecord.add(mapDetailOrder("expireTime","-")); //订单到期时间
            listrecord.add(mapDetailOrder("paymentModes","-"));   //回款方式
            listrecord.add(mapDetailOrder("paymentCoinType","-"));  //计算币种
            listrecord.add(mapDetailOrder("detalSatus",failOrderStatusStr(userFinancingRecord.getFailOrderStatus())));  //计算币种
            list.add(listrecord);
            List<Map<String,String>> listTotal = new ArrayList<>();
            listTotal.add(mapDetailOrder("totalIncomeUsdx","-"));  //总收益usdx
            listTotal.add(mapDetailOrder("fixedUsdxAmount","-"));   //固定收益
            listTotal.add(mapDetailOrder("extraUsdxAmount","-"));   //超额收益分佣、本人分佣
            list.add(listTotal);
            List<Map<String,String>> listIncome = new ArrayList<>();
            list.add(listIncome);
            List<Map<String,String>> listfinancing = new ArrayList<>();
            listfinancing.add(mapDetailOrder("financingName",financingBaseInfo.getTitle())); // 项目名称
            listfinancing.add(mapDetailOrder("userId",userFinancingRecord.getUserId()));  //获取用户id
            listfinancing.add(mapDetailOrder("userName",userAccountInfo.getDisplayName()));  //用户昵称
            listfinancing.add(mapDetailOrder("fromAddress",userFinancingRecord.getFromAddress())); //用户钱包地址
            listfinancing.add(mapDetailOrder("toAddress",userFinancingRecord.getToAddress()));  //平台钱包地址
            listfinancing.add(mapDetailOrder("incomAmount",investMentStr(userFinancingRecord.getCoinName(), userFinancingRecord.getAmount(), userFinancingRecord.getUsdxAmount())));
            listfinancing.add(mapDetailOrder("recordCreateTime",dateStr(userFinancingRecord.getRecordCreateTime().toString(),"yyyy-MM-dd HH:mm:ss")));
            listfinancing.add(mapDetailOrder("txId",userFinancingRecord.getTxId()));
            list.add(listfinancing);
            return list;
        }catch (Exception e){
            throw new Exception("获取失败订单详情异常",e);
        }

    }

    public List<List<Map<String,String>>> orderEveryDayDetailService(Integer recordId) throws Exception {

        UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(recordId);
        if (userFinancingRecord.getRedeemptionStatus() != 0){
            return orderRedeemptionDetailService(recordId);
        }else if(userFinancingRecord.getRecordStatus() == -1){
            return orderFaildDetailService(recordId);
        }else if(userFinancingRecord.getRecordStatus() == 2){
            return orderGoingDetailService(recordId);
        }else if(userFinancingRecord.getRecordStatus() == 3 || userFinancingRecord.getRecordStatus() == 4){
            return  orderFinishDetailService(recordId);
        }else {
            return null;
        }

    }

    //改变失败订单处理状态
    @Transactional
    public String failOrderStatusChangeService(Integer failOrderStatus,Integer recordId ) throws Exception {

            UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(recordId);
            if(userFinancingRecord.getFailOrderStatus() == failOrderStatus){
                throw  new Exception("修改状态与原状态一致");
            }
           userFinancingRecord.setFailOrderStatus(failOrderStatus);
            userFinancingRecordRepository.save(userFinancingRecord);
            if(failOrderStatus == 0){
                return "未处理";
            }else if(failOrderStatus == 1){
                return  "已处理";
            }else{
                throw  new Exception("不存在该状态序号：" + failOrderStatus);
            }

    }


    //返回数据Map数据，构建返回参数列表
    public Map<String,String> mapDetailOrder(String key,Object value){
        Map<String,String> map = new HashMap<>();
        if(StringUtils.isEmpty(value)){
            value = "-";
        }
        map.put("key",key);
        map.put("value",value.toString());
        return map;
    }



    //转化返餐格式，如果取值为空则返回“-”，如果返回时数值型参数需要保留4小数。market表示是否需要转化精度true:需要 false：不需要
    public String resultStrFormate(Object object,Boolean market){
        Pattern pattern = Pattern.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$");
        if (ObjectUtils.isEmpty(object)){
            return "-";
        }else if(pattern.matcher(object.toString()).matches() && market){
            return new BigDecimal(object.toString()).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
        }else {
            return object.toString();
        }
    }

    public String redeemptionStatusStr(Object object){
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (ObjectUtils.isEmpty(object) || !pattern.matcher(object.toString()).matches()){
            return "-";
        }else{
            if(object.toString().equals("1")){  //本金赎回
                return "已赎回本金";
            } else if(object.toString().equals("2")){   //固定收益赎回
                return "已赎回利息";
            } else if(object.toString().equals("3")){  //本金及固定收益赎回
                return "已赎回利息及本金";
            }
        }
        return "-";
    }

    public String failOrderStatusStr(Object object){
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (ObjectUtils.isEmpty(object) || !pattern.matcher(object.toString()).matches()){
            return "-";
        }else{
            if(object.toString().equals("1")){  //本金赎回
                return "已处理";
            } else if(object.toString().equals("0")){   //固定收益赎回
                return "未处理";
            }
        }
        return "-";
    }

    public String investMentStr(Object coinName,Object amount,Object usdxAmount){
        Pattern pattern = Pattern.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$");
        if(ObjectUtils.isEmpty(coinName) || ObjectUtils.isEmpty(amount) || ObjectUtils.isEmpty(usdxAmount)){
            return "-";
        }else{
            if(!pattern.matcher(amount.toString()).matches() || !pattern.matcher(usdxAmount.toString()).matches()){
                return "-";
            }
            if(coinName.toString().equals("ETH")){
                return   new BigDecimal(amount.toString()).setScale(3,BigDecimal.ROUND_HALF_UP) + "ETH=" + new BigDecimal(usdxAmount.toString()).setScale(3,BigDecimal.ROUND_HALF_UP) + "USDX";
            }else if(coinName.toString().equals("USDX")){
                return new BigDecimal(usdxAmount.toString()).setScale(3,BigDecimal.ROUND_HALF_UP).toString() + "USDX";
            }else{
                return new BigDecimal(amount.toString()).setScale(3,BigDecimal.ROUND_HALF_UP).toString() + coinName.toString();
            }

        }
    }

    public String surplusDay(Object recordCreateTime,Object expireTime){
        try{
            if(ObjectUtils.isEmpty(recordCreateTime) || ObjectUtils.isEmpty(expireTime)){
                return "-";
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            double dateTime = (formatter.parse(expireTime.toString()).getTime() - formatter.parse(recordCreateTime.toString()).getTime());
            double days =  Math.ceil ( (dateTime / (1000*3600*24)));
            return  (int)days + "天";
        }catch (Exception e){
            log.error("时间格式不正确");
        }
        return "-";
    }

    //返回数字精度设置
    public BigDecimal bigDecimalStr(Object object,long rate){
        if(ObjectUtils.isEmpty(object)){
            return BigDecimal.ZERO;
        }else{
            return new BigDecimal(object.toString()).multiply(BigDecimal.valueOf(rate)).setScale(3,BigDecimal.ROUND_HALF_UP);
        }
    }

    //字符串转时间格式，DateStr为时间字符串，formate为转化的时间格式  "yyyy-MM-dd HH:mm:ss"
    public String dateStr(String dateStr,String formate) throws Exception {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(formate);
            return formatter.format(formatter.parse(dateStr));
        }catch (Exception e){
            throw new Exception("时间转字符串，转化格式不正确",e);
        }
    }

    //计算订单固定收益
    public String sumFixedUsdxAmount(Object usdxAmount,Object annualRate,Object serviceRate,Object freezeNumber,Object freezeUnit){
        if (ObjectUtils.isEmpty(usdxAmount) || ObjectUtils.isEmpty(annualRate) || ObjectUtils.isEmpty(serviceRate) || ObjectUtils.isEmpty(freezeNumber) || ObjectUtils.isEmpty(freezeUnit)){
            return "-";
        }
        if(freezeUnit.toString().equals("年")){
            BigDecimal fixedRate = (new BigDecimal(annualRate.toString()).subtract(new BigDecimal(serviceRate.toString()))).multiply(new BigDecimal(freezeNumber.toString()));
            return new BigDecimal(usdxAmount.toString()).multiply(fixedRate).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
        }else if(freezeUnit.toString().equals("月")){
            BigDecimal fixedRate = (new BigDecimal(annualRate.toString()).subtract(new BigDecimal(serviceRate.toString()))).multiply(new BigDecimal(freezeNumber.toString()).divide(new BigDecimal("12"),3,BigDecimal.ROUND_HALF_UP));
            return new BigDecimal(usdxAmount.toString()).multiply(fixedRate).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
        }else if(freezeUnit.toString().equals("天")){
            BigDecimal fixedRate = (new BigDecimal(annualRate.toString()).subtract(new BigDecimal(serviceRate.toString()))).multiply(new BigDecimal(freezeNumber.toString()).divide(new BigDecimal("365"),3,BigDecimal.ROUND_HALF_UP));
            return new BigDecimal(usdxAmount.toString()).multiply(fixedRate).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
        }else{
            return "-";
        }
    }

    //计算当前固定收益率
    public String sumFixedUsdxRate(Object annualRate,Object serviceRate,Object freezeNumber,Object freezeUnit){
        if (ObjectUtils.isEmpty(annualRate) || ObjectUtils.isEmpty(serviceRate) || ObjectUtils.isEmpty(freezeNumber) || ObjectUtils.isEmpty(freezeUnit)){
            return "-";
        }
        if(freezeUnit.toString().equals("年")){
            BigDecimal fixedRate = (new BigDecimal(annualRate.toString()).subtract(new BigDecimal(serviceRate.toString()))).multiply(new BigDecimal(freezeNumber.toString())).multiply(BigDecimal.valueOf(100));
            return fixedRate.setScale(3,BigDecimal.ROUND_HALF_UP).toString();
        }else if(freezeUnit.toString().equals("月")){
            BigDecimal fixedRate = (new BigDecimal(annualRate.toString()).subtract(new BigDecimal(serviceRate.toString()))).multiply(new BigDecimal(freezeNumber.toString()).divide(new BigDecimal("12"),3,BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
            return fixedRate.setScale(3,BigDecimal.ROUND_HALF_UP).toString();
        }else if(freezeUnit.toString().equals("天")){
            BigDecimal fixedRate = (new BigDecimal(annualRate.toString()).subtract(new BigDecimal(serviceRate.toString()))).multiply(new BigDecimal(freezeNumber.toString()).divide(new BigDecimal("365"),3,BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
            return fixedRate.setScale(3,BigDecimal.ROUND_HALF_UP).toString();
        }else{
            return "-";
        }
    }

    //当前汇率
    public String bcb2UsdxPrice(){
        Pattern pattern = Pattern.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$");
        String bcb2Usdx = redisClient.get("BCB_USDX");
        if(pattern.matcher(bcb2Usdx).matches()){
            return bcb2Usdx;
        }else {
            return "0.00";
        }
    }

    //当前收益率
    public String currentYield(Object bcbUsdx){
        if(ObjectUtils.isEmpty(bcbUsdx)){
            return "-";
        }
        return new BigDecimal(bcb2UsdxPrice()).divide( new BigDecimal(bcbUsdx.toString()),5,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
    }

    //超额收益率
    public String extraYield(Object usdxAmount,Object extraUsdx){
        if (ObjectUtils.isEmpty(usdxAmount) || ObjectUtils.isEmpty(extraUsdx)){
            return "-";
        }
        return new BigDecimal(extraUsdx.toString()).divide(new BigDecimal(usdxAmount.toString()),3,BigDecimal.ROUND_HALF_UP).toString();
    }

    //合并订单记录
    public UserFinancingSettlement sumUserFinancingSettlement(List<UserFinancingSettlement> list){
        UserFinancingSettlement userFinancingSettlement = new UserFinancingSettlement();
        for(int i = 0; i < list.size(); i++){
            if(!StringUtils.isEmpty(list.get(i).getPrincipalRate())){
                userFinancingSettlement.setPrincipalUsdxAmount(list.get(i).getPrincipalUsdxAmount());
                userFinancingSettlement.setPrincipalBcbAmount(list.get(i).getPrincipalBcbAmount());
                userFinancingSettlement.setPrincipalRate(list.get(i).getPrincipalRate());
            }

            if(!StringUtils.isEmpty(list.get(i).getFixedUsdxAmount())){
                userFinancingSettlement.setFixedUsdxAmount(list.get(i).getFixedUsdxAmount());
                userFinancingSettlement.setFixedBcbAmount(list.get(i).getFixedBcbAmount());
                userFinancingSettlement.setFixedRate(list.get(i).getFixedRate());
            }

            if(!StringUtils.isEmpty(list.get(i).getServiceAmount())){
                userFinancingSettlement.setId(list.get(i).getId());
                userFinancingSettlement.setBcb2usdx(list.get(i).getBcb2usdx());
                userFinancingSettlement.setSuperiorCommission(list.get(i).getSuperiorCommission());
                userFinancingSettlement.setExtraBcbAmount(list.get(i).getExtraBcbAmount());
                userFinancingSettlement.setExtraUsdxAmount(list.get(i).getExtraUsdxAmount());
                userFinancingSettlement.setFoundationCommission(list.get(i).getFoundationCommission());
                userFinancingSettlement.setModifyTime(list.get(i).getModifyTime());
                userFinancingSettlement.setRemark(list.get(i).getRemark());
                userFinancingSettlement.setServiceAmount(list.get(i).getServiceAmount());
                userFinancingSettlement.setServiceRate(list.get(i).getServiceRate());
                userFinancingSettlement.setSettlementDate(list.get(i).getSettlementDate());
                userFinancingSettlement.setSuperiorCommissionTax(list.get(i).getSuperiorCommissionTax());
                userFinancingSettlement.setUserFinancingRecordId(list.get(i).getUserFinancingRecordId());
            }

        }
        return userFinancingSettlement;
    }


}
