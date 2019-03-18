package com.bcb.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Repository
public class OrderDao  extends BaseDao{

    public List<Map<String, Object>> OrderList(Integer flag,Integer recordId, String financingName, String orderCollection, String beginTime, String endTime,String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer start,Integer length) {

        final StringBuilder sb = new StringBuilder();
        sb.append("select * from ( ");
        sb.append("select\n" +
                "\ta.recordId,\n" +
                "\tb.financingName,\n" +
                "\ta.userId,\n" +
                "\tc.disPlayName,\n" +
                "\ta.address,\n" +
                "\ta.coinName,\n" +
                "\ta.amount,\n" +
                "\ta.usdxAmount,\n" +
                "\ta.recordStatus,\n" +
                "\ta.redeemptionStatus,\n" +
                "\ta.orderFaildStatus,\n" +
                "\ta.recordCreateTime,\n" +
                "\ta.expireTime\n" +
                "from(\n" +
                "\tselect id as recordId,financing_uuid as financingUuid,user_id as userId,from_address as address,coin_name as coinName,amount as amount,usdx_amount as usdxAmount,record_create_time as recordCreateTime,expire_time as expireTime,record_status as recordStatus,redeemption_status as redeemptionStatus,fail_order_status as orderFaildStatus from user_financing_record\n" +
                ")a \n" +
                "LEFT JOIN(\n" +
                "\tselect FinancingUuid as financingUuid,Title as financingName from financing_base_info\n" +
                ")b on a.financingUuid = b.financingUuid\n" +
                "LEFT JOIN(\n" +
                "\tselect UserId as userId,DisplayName as disPlayName from user_account_info\n" +
                ")c on a.userId = c.userId");
        sb.append(" )aaaaa where 1=1 ");

        if(flag == 1){   //进行中，不包含已赎回订单
            sb.append(" and recordStatus = 2 and redeemptionStatus = 0 ");
        }else if(flag == 2){  //已赎回，且包含进行中的订单
            sb.append(" and (redeemptionStatus between 1 and 3) ");
        }else if(flag == 3){  //已完成,不包含已赎回订单
            sb.append(" and ( (recordStatus between 3 and 4) and redeemptionStatus = 0 ) ");
        }else if(flag == 4){   //失败订单
            sb.append(" and recordStatus = -1 ");
        }else if(flag == 5){
            String startTime = searchTime + " 00:00:00";
            String endsTime = searchTime + " 23:59:59";
            sb.append(" and expireTime between '"+startTime+"' and '"+endsTime+"' ");
        }

        if (!StringUtils.isEmpty(recordId)){
            sb.append(" and recordId like '%"+recordId+"%'");
        }

        if (!StringUtils.isEmpty(financingName)){
            sb.append(" and financingName like '%"+financingName+"%'");
        }

        if (!StringUtils.isEmpty(orderCollection)){
            sb.append(" and ( userId like '%"+orderCollection+"%' or  disPlayName like '%"+orderCollection+"%' or  address like '%"+orderCollection+"%' )");
        }

        if (!StringUtils.isEmpty(orderFaildStatus)){
            sb.append(" and orderFaildStatus like '%"+orderFaildStatus+"%'");
        }

        if (!StringUtils.isEmpty(redeemptionStatus)){
            sb.append(" and redeemptionStatus like '%"+redeemptionStatus+"%'");
        }
        sb.append(" and (recordCreateTime Between '"+beginTime+"' and '"+endTime+"') ");

        sb.append(" ORDER BY recordCreateTime DESC  LIMIT ?,?");

        return this.jdbcTemplate.queryForList(
                sb.toString(),
                new Object[] {start,length});
    }

    public Integer
    OrderListNum(Integer flag,Integer recordId, String financingName, String orderCollection, String beginTime, String endTime, String searchTime, Integer orderFaildStatus, Integer redeemptionStatus, Integer start, Integer length) {

        final StringBuilder sbNum = new StringBuilder();
        sbNum.append("select count(*) from ( ");
        sbNum.append("select\n" +
                "\ta.recordId,\n" +
                "\tb.financingName,\n" +
                "\ta.userId,\n" +
                "\tc.disPlayName,\n" +
                "\ta.address,\n" +
                "\ta.coinName,\n" +
                "\ta.amount,\n" +
                "\ta.usdxAmount,\n" +
                "\ta.recordStatus,\n" +
                "\ta.redeemptionStatus,\n" +
                "\ta.orderFaildStatus,\n" +
                "\ta.recordCreateTime,\n" +
                "\ta.expireTime\n" +
                "from(\n" +
                "\tselect id as recordId,financing_uuid as financingUuid,user_id as userId,from_address as address,coin_name as coinName,amount as amount,usdx_amount as usdxAmount,record_create_time as recordCreateTime,expire_time as expireTime,record_status as recordStatus,redeemption_status as redeemptionStatus,fail_order_status as orderFaildStatus from user_financing_record\n" +
                ")a \n" +
                "LEFT JOIN(\n" +
                "\tselect FinancingUuid as financingUuid,Title as financingName from financing_base_info\n" +
                ")b on a.financingUuid = b.financingUuid\n" +
                "LEFT JOIN(\n" +
                "\tselect UserId as userId,DisplayName as disPlayName from user_account_info\n" +
                ")c on a.userId = c.userId");
        sbNum.append(" )aaaaa where 1=1 ");

        if(flag == 1){   //进行中，不包含已赎回订单
            sbNum.append(" and recordStatus = 2 and redeemptionStatus = 0 ");
        }else if(flag == 2){  //已赎回，且包含进行中的订单
            sbNum.append(" and (redeemptionStatus between 1 and 3) ");
        }else if(flag == 3){  //已完成,不包含已赎回订单
            sbNum.append(" and ( (recordStatus between 3 and 4) and redeemptionStatus = 0 ) ");
        }else if(flag == 4){   //失败订单
            sbNum.append(" and recordStatus = -1 ");
        }else if(flag == 5){
            String startTime = searchTime + " 00:00:00";
            String endsTime = searchTime + " 23:59:59";
            sbNum.append(" and expireTime between '"+startTime+"' and '"+endsTime+"' ");
        }

        if (!StringUtils.isEmpty(recordId)){
            sbNum.append(" and recordId like '%"+recordId+"%'");
        }

        if (!StringUtils.isEmpty(financingName)){
            sbNum.append(" and financingName like '%"+financingName+"%'");
        }

        if (!StringUtils.isEmpty(orderCollection)){
            sbNum.append(" and ( userId like '%"+orderCollection+"%' or  disPlayName like '%"+orderCollection+"%' or  address like '%"+orderCollection+"%' )");
        }

        if (!StringUtils.isEmpty(orderFaildStatus)){
            sbNum.append(" and orderFaildStatus like '%"+orderFaildStatus+"%'");
        }

        if (!StringUtils.isEmpty(redeemptionStatus)){
            sbNum.append(" and redeemptionStatus like '%"+redeemptionStatus+"%'");
        }
        sbNum.append(" and (recordCreateTime Between '"+beginTime+"' and '"+endTime+"') ");


        return this.jdbcTemplate.queryForObject(sbNum.toString(),Integer.class);
    }

}
