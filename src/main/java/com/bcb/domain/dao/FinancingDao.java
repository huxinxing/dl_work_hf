package com.bcb.domain.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bcb.bean.dto.*;
import com.bcb.bean.dto.statistics.FinancingWithDrawDto;
import com.bcb.domain.entity.FinancingBaseInfo;
import com.bcb.domain.entity.UserBalanceWithdraw;
import com.bcb.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FinancingDao extends BaseDao{

	/**
	 * @description  查询交易管理信息
	 */
	public Map<String,Object> queryFinancingTradeManagerList(FinancingTradeVO obj, Integer start, Integer length){
		Map<String,Object> mapResult = new HashMap<>();
		StringBuffer sb = new StringBuffer();
		sb.append("select ifnull(trade.tx_id,'') AS tsId,ifnull(trade.id,'') AS tradeId,ifnull(trade.from_address,'') as FromToken,ifnull(trade.to_address,'') as ToToken,ifnull(pro.Title,'') AS projectName, ifnull(uinfo.DisplayName,'') AS userName,ifnull(trade.coin_name,'') as PaymentType,ifnull(trade.amount,'0.00') as PaymentAmount,ifnull(trade.usdx_amount,'0.00') as UsdxAmount,ifnull(pro.SerialNum,'') as SerialNum,ifnull(trade.subscription_fee_rate,'0.00') as SuperiorRate,")
		.append("ifnull(pro.FinancingUuid,'') as FinancingUuid,DATE_FORMAT(trade.record_create_time,'%Y-%m-%d %H:%i:%s') AS confirmTime from user_financing_record trade LEFT JOIN financing_base_info pro ON trade.financing_uuid = pro.FinancingUuid ")
		.append("LEFT JOIN user_account_info uinfo ON uinfo.UserId = trade.user_id where 1=1 ");
		if (obj.getStatus() != null) {
			sb.append(" and trade.record_status = '" + obj.getStatus()+"'");
		}
		if (!StringUtils.isEmpty(obj.getFromToken())) {
			sb.append(" and trade.from_address like '%" + obj.getFromToken() + "%'");
		}
		sb.append(" ORDER BY trade.record_create_time DESC  LIMIT ?,?");
		List<FinancingTradeVO> listResult= this.jdbcTemplate.query(
				sb.toString(),
				new Object[] {start,length},
				new BeanPropertyRowMapper<>(
						FinancingTradeVO.class));
		mapResult.put("items", listResult);

		StringBuffer sbCount = new StringBuffer();
		sbCount.append("select count(1) from user_financing_record trade LEFT JOIN financing_base_info pro ON trade.financing_uuid = pro.FinancingUuid LEFT JOIN user_account_info uinfo " +
				"ON uinfo.UserId = trade.user_id where 1 = 1");
		if (obj.getStatus() != null) {
			sbCount.append(" and trade.record_status = '" + obj.getStatus()+"'");
		}
		if (!StringUtils.isEmpty(obj.getFromToken())) {
			sbCount.append(" and trade.from_address like '%" + obj.getFromToken() + "%'");
		}
		Integer count = this.jdbcTemplate.queryForObject(sbCount.toString(),new Object[]{},Integer.class);
		mapResult.put("num",count);
		return mapResult;
	}
	/**
	 * @description  查询理财交易流水信息
	 */
	public Map<String,Object> queryFinancingTradeFlowList(FinancingTradeFlowVO obj, Integer start, Integer length){
		Map<String,Object> map = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ifnull(bill.`id`,'') as agentBillId,ifnull(trade.`id`,'') as tradeId,ifnull(uinfo.`DisplayName`,'') as userName,ifnull(ucode.`Code`,'') as userId,ifnull(trade.subscription_fee_rate,'0.00') as superiorRate,ifnull(trade.annual_rate,'0.00') as totalRate,ifnull(trade.financing_uuid,'') as financingUuid,");
		sb.append(" ifnull(trade.amount,'0.00') as paymentAmount,ifnull(trade.coin_name,'') as paymentType,ifnull(invest.`CoinAmount`,'0.00') as investAmount,ifnull(invest.`CoinType`,'') as investCoinType,ifnull(base.SerialNum,'') as serialNum,ifnull(base.Title,'') as title, ");
		sb.append(" ifnull(bill.`amount`,'0.00') as agentRateAmount,ifnull('BCB','') as agentCoinType,DATE_FORMAT(bill.`create_time`,'%Y-%m-%d %H:%i:%s') as createTime ");
		sb.append(" FROM user_agents_financing_bill bill ");
		sb.append(" LEFT JOIN user_financing_record trade ON trade.`id`=bill.`record_id` ").append(" LEFT JOIN financing_base_info base on trade.financing_uuid = base.FinancingUuid ");
		sb.append(" LEFT JOIN invest_incom_info invest ON invest.`UserFinancingRelationId`=trade.`id` ");
		sb.append(" LEFT JOIN user_account_info uinfo ON uinfo.`UserId`=trade.`user_id`  ");
		sb.append(" LEFT JOIN user_invit_code ucode ON uinfo.`UserId`=ucode.`UserId`  where trade.id is not null ");
		if (StringUtils.isNotBlank(obj.getPaymentType())) {//
			sb.append(" and trade.coin_name = '" + obj.getPaymentType() + "'");
		}
		if(StringUtils.isNotBlank(obj.getStartTime())) {
			sb.append(" and  DATE_FORMAT(bill.`create_time`,'%Y-%m-%d %H:%i:%s') >= '" + obj.getStartTime()+ "'");
		}
		if(StringUtils.isNotBlank(obj.getEndTime())) {
			sb.append(" and  DATE_FORMAT(bill.`create_time`,'%Y-%m-%d %H:%i:%s') <= '" + obj.getEndTime()+ "'");
		}
		if(StringUtils.isNotBlank(obj.getWalletAddr())) {
			sb.append(" and   ( trade.from_address = '" + obj.getWalletAddr() + "' or  trade.to_address = '" + obj.getWalletAddr() + "')");
		}
		sb.append(" ORDER BY bill.`create_time` DESC ");
		sb.append(" limit ?,?");
		List<FinancingTradeFlowVO> list = this.jdbcTemplate.query(
				sb.toString(),
				new Object[] {start,length},
				new BeanPropertyRowMapper<>(
						FinancingTradeFlowVO.class));
		map.put("items",list);
		StringBuilder countSql = new StringBuilder();
		countSql.append(" SELECT count(bill.id) ");
		countSql.append(" FROM user_agents_financing_bill bill ");
		countSql.append(" LEFT JOIN user_financing_record trade ON trade.`id`=bill.`record_id` ");
		countSql.append(" LEFT JOIN invest_incom_info invest ON invest.`UserFinancingRelationId`=trade.`id` ");
		countSql.append(" LEFT JOIN user_account_info uinfo ON uinfo.`UserId`=trade.`user_id`  ");
		countSql.append(" LEFT JOIN user_invit_code ucode ON uinfo.`UserId`=ucode.`UserId`  where trade.id is not null ");
		if (StringUtils.isNotBlank(obj.getPaymentType())) {//暂时写死成ALl，回头查看项目统一处理方式
			countSql.append(" and trade.coin_name = '" + obj.getPaymentType() + "'");
		}
		if(StringUtils.isNotBlank(obj.getStartTime())) {
			countSql.append(" and  DATE_FORMAT(bill.`create_time`,'%Y-%m-%d %H:%i:%s') >= '" + obj.getStartTime()+ "'");
		}
		if(StringUtils.isNotBlank(obj.getEndTime())) {
			countSql.append(" and  DATE_FORMAT(bill.`create_time`,'%Y-%m-%d %H:%i:%s') <= '" + obj.getEndTime()+ "'");
		}
		if(StringUtils.isNotBlank(obj.getWalletAddr())) {
			countSql.append(" and   ( trade.from_address = '" + obj.getWalletAddr() + "' or  trade.to_address = '" + obj.getWalletAddr() + "')");
		}

		Integer count = this.jdbcTemplate.queryForObject(countSql.toString(),new Object[]{},Integer.class);
		map.put("num",count);
		return map;
	}
	/**
	 * @description  代理收益提币审核信息
	 */
	public Map<String, Object> queryUserFinancingAgentsWithdrawList( Integer type,String user,Integer start, Integer length) {
		Map<String,Object> map = new HashMap<>();
		// 获取分页记录内容 DATE_FORMAT(bill.`create_time`,'%Y-%m-%d %H:%i:%s')
		StringBuilder sb = new StringBuilder();
		sb.append("select * from( ");
		sb.append("select \n" +
				"\ta.id as id,\n" +
				"\ta.user_id as userId,\n" +
				"\tb.DisplayName as userName,\n" +
				"\td.name as coinName,\n" +
				"\ta.coin_amount as amount,\n" +
				"\ta.to_address as address,\n" +
				"\tDATE_FORMAT(a.create_time,'%Y-%m-%d %H:%i:%s') as createTime,\n" +
				"\ta.status as confirmType,\n" +
				"\tDATE_FORMAT(a.review_time,'%Y-%m-%d %H:%i:%s') as reviewTime,\n" +
				"\tc.login_name as reviewer,\n" +
				"\ta.remark as remark\n" +
				"from(\n" +
				"\tselect * from user_balance_withdraw\n" +
				")a \n" +
				"LEFT JOIN(\n" +
				"\tselect * from user_account_info\n" +
				")b on a.user_id = b.UserId\n" +
				"LEFT JOIN(\n" +
				"\tselect * from system_login_account\n" +
				")c on a.reviewer_id = c.id\n" +
				"LEFT JOIN(\n" +
				"\tselect * from system_coin\n" +
				")d on a.coin_id = d.id");
		sb.append(") aaaaa ");

		if(type.toString().equals("1")){
			sb.append(" where (confirmType = 1 or confirmType = 2) ");
		}else{
			sb.append(" where confirmType = '" + type + "' ");
		}

		if (!StringUtils.isEmpty(user)){
			sb.append(" and (userName like '%" + user + "%' or userId like '%" + user + "%')");
		}

		sb.append("  ORDER BY createTime DESC  limit ?,?");
		List<FinancingWithDrawDto> list = this.jdbcTemplate.query(sb.toString(), new Object[]{start, length},
				new BeanPropertyRowMapper<>(FinancingWithDrawDto.class));
		map.put("items", list);

		// 获取记录总数
		StringBuilder countSb = new StringBuilder();
		countSb.append("select count(*) from( ");
		countSb.append("select \n" +
				"\ta.user_id as userId,\n" +
				"\tb.DisplayName as userName,\n" +
				"\td.name as coinName,\n" +
				"\ta.coin_amount as amount,\n" +
				"\ta.to_address as address,\n" +
				"\tDATE_FORMAT(a.create_time,'%Y-%m-%d %H:%i:%s') as createTime,\n" +
				"\ta.status as confirmType,\n" +
				"\tDATE_FORMAT(a.review_time,'%Y-%m-%d %H:%i:%s') as reviewTime,\n" +
				"\tc.login_name as reviewer,\n" +
				"\ta.remark as remark\n" +
				"from(\n" +
				"\tselect * from user_balance_withdraw\n" +
				")a \n" +
				"LEFT JOIN(\n" +
				"\tselect * from user_account_info\n" +
				")b on a.user_id = b.UserId\n" +
				"LEFT JOIN(\n" +
				"\tselect * from system_login_account\n" +
				")c on a.reviewer_id = c.id\n" +
				"LEFT JOIN(\n" +
				"\tselect * from system_coin\n" +
				")d on a.coin_id = d.id");
		countSb.append(") aaaaa ");
		if(type.toString().equals("1")){
			countSb.append(" where (confirmType = 1 or confirmType = 2) ");
		}else{
			countSb.append(" where confirmType = '" + type + "' ");
		}

		if (!StringUtils.isEmpty(user)){
			countSb.append(" and (userName like '%" + user + "%' or userId like '%" + user + "%')");
		}
 		Integer num = this.jdbcTemplate.queryForObject(countSb.toString(), new Object[]{}, Integer.class);
		map.put("num", num);

		return map;
	}

	/**
	 * @author qiang wen
	 * @description    查询用户投资收益
	 * @createDate   5/21/2018 5:27 PM
	 * @return java.util.List<com.bcb.bean.dto.UserInvestRateDto>
	 */
	public List<UserInvestRateDto> queryAgentTradeList(Integer tradeId){
		final StringBuilder sb = new StringBuilder("");
		sb.append(" select ucode.Code as code,uinfo.DisplayName as userName,");
		sb.append(" gainAgentLevel(uinfo.UserId) as agentLevel,CONCAT(bill.coin_amount,'') as bcbAgentAmount ");
		sb.append(" from user_agents_financing_bill bill ");
		sb.append(" left join user_account_info uinfo on bill.user_id=uinfo.UserId ");
		sb.append(" left join user_invit_code ucode on ucode.UserId=uinfo.UserId ");
		sb.append(" where bill.record_id=?");
		sb.append(" order by gainAgentLevel(uinfo.UserId) asc ");
		return  this.jdbcTemplate.query(
				sb.toString(),
				new Object[] {tradeId},
				new BeanPropertyRowMapper<>(
						UserInvestRateDto.class));
	}

}
