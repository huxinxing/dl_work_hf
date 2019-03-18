package com.bcb.domain.dao;

import java.util.HashMap;
import java.util.Map;

import com.bcb.domain.entity.FinancingIncomeUnknown;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FinancingIncomeUnknownDao extends BaseDao{

    public Map<String, Object> queryFinancingIncomeUnknowns(Integer start, Integer length, Integer status, String search) {
        Map<String,Object> map = new HashMap<>();
        // 获取分页记录内容
        StringBuilder sb = new StringBuilder();

        sb.append("select Id, CreateTime, FinancingUuid, FromToken, ToToken, PaymentAmount,paymentType, ");
        sb.append("        TsId, CoinType, CoinAmount, ConfirmNum, TradeConfirmTime, ");
        sb.append("        Status, Coin2Bcb, Bcb2Usdx, Usdx2Cny, ");
        sb.append("        DATE_FORMAT(CreateTime,'%Y-%m-%d %H:%i:%s') as createTimeStr, ");
        sb.append("        DATE_FORMAT(TradeConfirmTime,'%Y-%m-%d %H:%i:%s') as tradeConfirmTimeStr ");
        sb.append("from financing_income_unknown ");
//        sb.append("select * from financing_income_unknown ");
        sb.append(" where status = "+status+" ");
        if(StringUtils.isNotBlank(search)){
            sb.append(" and FromToken like '%"+search+"%' ");
        }
        sb.append("limit ?,?");
        List<FinancingIncomeUnknown> list = this.jdbcTemplate.query(sb.toString(), new Object[]{start, length},
                new BeanPropertyRowMapper<>(FinancingIncomeUnknown.class));
        map.put("items", list);

        // 获取记录总数
        sb.delete(0, sb.length());
        sb.append("select count(Id) ")
                .append("from financing_income_unknown ")
                .append("where status = '"+status+"' ");
        if(StringUtils.isNotBlank(search)){
            sb.append(" and FromToken like '%"+search+"%' ");
        }
        Integer num = this.jdbcTemplate.queryForObject(sb.toString(), new Object[]{}, Integer.class);
        map.put("num", num);

        return map;
    }
}
