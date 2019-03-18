package com.bcb.domain.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bcb.domain.entity.Whitelist;
import com.bcb.domain.enums.FinancingBaseStatusEnum;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Repository
public class StatisticsDao extends BaseDao{


	public Map<String, Object> GainOverView(List<Whitelist> whitelist, String beginTime, String endTime) {
		// TODO Auto-generated method stub
		
		String whiteSql = "";
		if(whitelist != null && whitelist.size() > 0){
			for(int i = 0; i < whitelist.size(); i++){
				whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
			}
		}
		
		final StringBuilder sb = new StringBuilder("");

		sb.append("SELECT\n" +
				"\tfinancingCount as financingCount,\n" +
				"\tCAST(SUM(totalInvest) as DECIMAL(20,4)) as receivedPrincipal,\n" +
				"\tCAST(SUM(receivedPaymentFee) as DECIMAL(20,4)) as receivedPaymentFee,\n" +
				"\tCAST(SUM(receivedServiceFee) as DECIMAL(20,4)) as receivedServiceFee,\n" +
				"\tCAST(SUM(receivedAchievements) as DECIMAL(20,4)) as receivedAchievements,\n" +
				"\tCAST(SUM(receivedTax) as DECIMAL(20,4)) as receivedTax,\n" +
				"\tCAST(SUM(receivedWithdrawFee) as DECIMAL(20,4)) as receivedWithdrawFee,\n" +
				"\tCAST(SUM(paidPrincipal) as DECIMAL(20,4)) as paidPrincipal,"+
				"\tCAST(SUM(paidFixedProfit) as DECIMAL(20,4)) as paidFixedProfit,\n" +
				"\tCAST(SUM(paidAdditionalProfit) as DECIMAL(20,4)) as paidAdditionalProfit,\n" +
				"\tCAST(SUM(paidAgentRebeat) as DECIMAL(20,4)) as paidAgentRebeat,\n" +
				"\tCAST(SUM(BcbAmount) as DECIMAL(20,4)) as BcbAmount\n" +
				"FROM(\n" +
				"\n" +
				"\tselect * from(\n" +
				"\n" +
				"\t\tselect COUNT(FinancingUuid) as financingCount,0 as totalInvest,0 as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,0 as receivedTax,0 as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,0 as paidAgentRebeat,0 as BcbAmount from financing_base_info where (CreateTime BETWEEN '"+beginTime+"' and '"+endTime+"') and (Status BETWEEN 1 and 4)\n" +
				"\t\t\n" +
				"\t\tUNION ALL\n" +
				"\t\t\n" +
				"\t\tselect 0 as financingCount,IFNULL(SUM(IFNULL(usdx_amount,0)),0) as totalInvest,IFNULL(SUM(IFNULL(subscription_fee,0)),0) as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,0 as receivedTax,0 as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,0 as paidAgentRebeat,0 as BcbAmount from user_financing_record where(record_create_time BETWEEN '"+beginTime+"' and '"+endTime+"') and (record_status BETWEEN 1 and 4)  "+whiteSql+"\n" +
				"\t\t\n" +
				"\t\tUNION ALL\n" +
				"\n" +
				"\t\tselect 0 as financingCount,0 as totalInvest,0 as receivedPaymentFee,IFNULL(SUM(IFNULL(a.service_amount,0) * IFNULL(a.bcb2usdx,0) ),0) as receivedServiceFee,IFNULL(SUM(IFNULL(a.foundation_commission,0) * IFNULL(a.bcb2usdx,0) ),0) as receivedAchievements,IFNULL(SUM(IFNULL(a.superior_commission_tax,0) * IFNULL(a.bcb2usdx,0) ),0) as receivedTax,0 as receivedWithdrawFee,IFNULL(SUM(IFNULL(a.principal_usdx_amount,0)),0) as paidPrincipal,IFNULL(SUM(IFNULL(a.fixed_usdx_amount,0)),0) as paidFixedProfit,IFNULL(SUM(IFNULL(a.extra_usdx_amount,0)) - SUM(IFNULL(a.foundation_commission*a.bcb2usdx,0)) - SUM(IFNULL(a.superior_commission_tax*a.bcb2usdx,0)),0) as paidAdditionalProfit,0 as paidAgentRebeat,IFNULL(SUM(IFNULL(a.extra_bcb_amount,0)) - SUM(IFNULL(a.foundation_commission,0)) - SUM(IFNULL(a.superior_commission_tax,0)) + SUM(IFNULL(a.fixed_bcb_amount,0)) + SUM(IFNULL(a.principal_bcb_amount,0)) ,0) as BcbAmount from(\n" +
				"\t\t\tselect * from user_financing_settlement\n" +
				"\t\t)a\n" +
				"\t\tJOIN(\n" +
				"\t\t\tselect id,from_address from user_financing_record where (record_create_time BETWEEN '"+beginTime+"' and '"+endTime+"')  "+whiteSql+" \n" +
				"\t\t)b on a.user_financing_record_id = b.id\n" +
				"\t\t\n" +
				"\t\tUNION ALL\n" +
				"\n" +
				"\t\tselect 0 as financingCount,0 as totalInvest,0 as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,IFNULL(SUM(IFNULL(a.tax,0) * IFNULL(b.bcb2usdx,0)),0) as receivedTax,0 as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,IFNULL(SUM(IFNULL(a.amount,0) * IFNULL(b.bcb2usdx,0)),0) as paidAgentRebeat,IFNULL(SUM(IFNULL(a.amount,0)),0) as BcbAmount  from(\n" +
				"\t\t\tselect record_id,amount,tax from user_agents_financing_bill \n" +
				"\t\t)a \n" +
				"\t\tRIGHT JOIN(\n" +
				"\t\t\tselect id,bcb2usdx from user_financing_record where (record_create_time BETWEEN '"+beginTime+"' and '"+endTime+"')  "+whiteSql+"\n" +
				"\t\t)b on a.record_id = b.id and !ISNULL(a.tax) and !ISNULL(a.amount)\n" +
				"\n" +
				"\t\tUNION ALL\n" +
				"\t\t\n" +
				"\t\tselect 0 as financingCount,0 as totalInvest,0 as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,0 as receivedTax,IFNULL(SUM(IFNULL(handling_charge,0)),0) as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,0 as paidAgentRebeat,0 as BcbAmount from user_balance_withdraw  where (create_time BETWEEN '"+beginTime+"' and '"+endTime+"')  "+whiteSql+"\n" +
				"\t\t\n" +
				"\t)a\n" +
				"\n" +
				"\n" +
				"\n" +
				")aaaa");

		Map<String, Object> map = this.jdbcTemplate.queryForMap(sb.toString());
	
		return map;
	}
	

	public String ReceivedWithdrawFee() {
		// TODO Auto-generated method stub
		final StringBuilder sb = new StringBuilder("");
		sb.append("select count(*) from user_agents_financing_withdraw");
		return this.jdbcTemplate.queryForObject(sb.toString(), String.class);
	}
	

	public List<Map<String, Object>> GainCoinNum(List<Whitelist> whitelist, String beiginTime, String endTime) {
		// TODO Auto-generated method stub
		String whiteSql = "";
		if(whitelist != null && whitelist.size() > 0){
			for(int i = 0; i < whitelist.size(); i++){
				whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
			}
		}
		
		final StringBuilder sb = new StringBuilder();
		sb.append("select sum(PaymentAmount) as PaymentAmount, PaymentType from user_financing_relation where CreateTime > '"+beiginTime+"' and CreateTime < '"+endTime+"' "+whiteSql+" group by PaymentType" );
		return this.jdbcTemplate.queryForList(sb.toString());
	}


	public List<Map<String, Object>> GainAccountOrder(String beginTime, String endTime,List<Whitelist> whitelist) {
		final StringBuilder sb = new StringBuilder();  
		String whiteSql = "";
		if(whitelist != null && whitelist.size() > 0){
			for(int i = 0; i < whitelist.size(); i++){
				whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
			}
		}
		sb.append("SELECT\n" +
				"\tCAST(SUM(receivedPrincipal) as DECIMAL(20,4)) as receivedPrincipal,\n" +
				"\tCAST(SUM(receivedPaymentFee) as DECIMAL(20,4)) as receivedPaymentFee,\n" +
				"\tCAST(SUM(receivedServiceFee) as DECIMAL(20,4)) as receivedServiceFee,\n" +
				"\tCAST(SUM(receivedAchievements) as DECIMAL(20,4)) as receivedAchievements,\n" +
				"\tCAST(SUM(receivedTax) as DECIMAL(20,4)) as receivedTax,\n" +
				"\tCAST(SUM(receivedWithdrawFee) as DECIMAL(20,4)) as receivedWithdrawFee,\n" +
                "\tCAST(SUM(paidPrincipal) as DECIMAL(20,4)) as paidPrincipal,\n"+
				"\tCAST(SUM(paidFixedProfit) as DECIMAL(20,4)) as paidFixedProfit,\n" +
				"\tCAST(SUM(paidAdditionalProfit) as DECIMAL(20,4)) as paidAdditionalProfit,\n" +
				"\tCAST(SUM(paidAgentRebeat) as DECIMAL(20,4)) as paidAgentRebeat,\t\n" +
				"\tCAST(SUM(paidGe) as DECIMAL(20,4)) as paidGe,\n" +
				"\tCAST(SUM(paidIc) as DECIMAL(20,4)) as paidIc,"+
				"\tCAST(SUM(BcbAmount) as DECIMAL(20,4)) as BcbAmount,\n" +
				"\tTime as Time\n" +
				"FROM(\n" +
				"\t\t\t\n" +
				"\t\tselect IFNULL(SUM(IFNULL(usdx_amount,0)),0) as receivedPrincipal,IFNULL(SUM(IFNULL(subscription_fee,0)),0) as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,0 as receivedTax,0 as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,0 as paidAgentRebeat,0 as paidGe,0 as paidIc,0 as BcbAmount,date_format(record_create_time, '%Y-%m-%d') as Time from user_financing_record where 1=1 "+whiteSql+" and (record_status BETWEEN 1 and 4) GROUP BY Time\n" +
				"\t\t\n" +
				"\t\tUNION ALL\n" +
				"\n" +
				"\t\t\tselect 0 as receivedPrincipal,0 as receivedPaymentFee,IFNULL(SUM(IFNULL(a.service_amount,0) * IFNULL(a.bcb2usdx,0) ),0) as receivedServiceFee,IFNULL(SUM(IFNULL(a.foundation_commission,0) * IFNULL(a.bcb2usdx,0) ),0) as receivedAchievements,IFNULL(SUM(IFNULL(a.superior_commission_tax,0) * IFNULL(a.bcb2usdx,0) ),0) as receivedTax,0 as receivedWithdrawFee,IFNULL(SUM(IFNULL(a.principal_usdx_amount,0)),0) as paidPrincipal,IFNULL(SUM(IFNULL(a.fixed_usdx_amount,0)),0) as paidFixedProfit,IFNULL(SUM(IFNULL(a.extra_usdx_amount,0) - IFNULL(a.foundation_commission*a.bcb2usdx,0) - IFNULL(a.superior_commission_tax*a.bcb2usdx,0)) ,0) as paidAdditionalProfit,0 as paidAgentRebeat,0 as paidGe,0 as paidIc,IFNULL(SUM(IFNULL(a.extra_bcb_amount,0)) - SUM(IFNULL(a.foundation_commission,0)) - SUM(IFNULL(a.superior_commission_tax,0)) + SUM(IFNULL(a.fixed_bcb_amount,0))  + SUM(IFNULL(a.principal_bcb_amount,0))  ,0) as BcbAmount ,date_format(a.settlement_date, '%Y-%m-%d') as Time from(\n" +
				"\t\t\tselect * from user_financing_settlement \n" +
				"\t\t)a\n" +
				"\t\tJOIN(\n" +
				"\t\t\tselect id,from_address from user_financing_record where 1=1 "+whiteSql+" \n" +
				"\t\t)b on a.user_financing_record_id = b.id  GROUP BY Time\n" +
				"\t\t\n" +
				"\t\tUNION ALL\n" +
				"\n" +
				"\t\tselect 0 as receivedPrincipal,0 as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,IFNULL(SUM(IFNULL(a.tax,0) * IFNULL(b.bcb2usdx,0)),0) as receivedTax,0 as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,IFNULL(SUM(IFNULL(a.amount,0) * IFNULL(b.bcb2usdx,0)),0) as paidAgentRebeat,0 as paidGe,0 as paidIc,IFNULL(SUM(IFNULL(a.amount,0)),0) as BcbAmount ,date_format(a.create_time, '%Y-%m-%d') as Time from(\n" +
				"\t\t\tselect record_id,amount,tax,create_time from user_agents_financing_bill \n" +
				"\t\t)a \n" +
				"\t\tRIGHT JOIN(\n" +
				"\t\t\tselect id,bcb2usdx from user_financing_record where 1=1 "+whiteSql+" \n" +
				"\t\t)b on a.record_id = b.id and !ISNULL(a.tax) and !ISNULL(a.amount) and !ISNULL(a.create_time) GROUP BY Time\n" +
				"\n" +
				"\t\tUNION ALL\n" +
				"\t\t\n" +
				"\t\tselect 0 as receivedPrincipal,0 as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,0 as receivedTax,IFNULL(SUM(IFNULL(handling_charge,0)),0) as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,0 as paidAgentRebeat,0 as paidGe,0 as paidIc,0 as BcbAmount,date_format(create_time, '%Y-%m-%d') as Time from user_balance_withdraw where 1=1 "+whiteSql+" GROUP BY Time\n" +
				"\t\t\n" +
				"UNION ALL\n" +
				"\n" +
				"\t\tselect 0 as receivedPrincipal,0 as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,0 as receivedTax,0 as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,0 as paidAgentRebeat,0 as paidGe,SUM(IFNULL(a.ic_seletment,0)) as paidIc,SUM(IFNULL(a.ic_seletment/a.bcb2usdx,0)) as BcbAmount,\n" +
				"\t\t\tCASE\n" +
				"\t\t\t\tWHEN a.ic_end_time > b.expire_time THEN date_format(b.expire_time, '%Y-%m-%d')\n" +
				"\t\t\t\tWHEN a.ic_end_time <= b.expire_time THEN date_format(a.ic_end_time, '%Y-%m-%d')\n" +
				"\t\t\tEND\n" +
				"\t\t\t\tTime\n" +
				"\t\t  from(\n" +
				"\t\t\tselect ic_seletment,bcb2usdx,record_id,ic_end_time from user_interst_coupons_relation where  !ISNULL(bcb2usdx) and ic_status = 2\n" +
				"\t\t)a\n" +
				"\t\tJOIN(\n" +
				"\t\t\tselect id,expire_time from user_financing_record  where 1=1 "+whiteSql+"\n" +
				"\t\t)b on a.record_id = b.id  GROUP BY Time\n" +
				"\n" +
				"\t\tUNION ALL\n" +
				"\n" +
				"\t\tselect 0 as receivedPrincipal,0 as receivedPaymentFee,0 as receivedServiceFee,0 as receivedAchievements,0 as receivedTax,0 as receivedWithdrawFee,0 as paidPrincipal,0 as paidFixedProfit,0 as paidAdditionalProfit,0 as paidAgentRebeat,SUM(IFNULL(ge_amount,0)*bcb2usdx) as paidGe,0 as paidIc,SUM(IFNULL(ge_amount,0)) as BcbAmount,date_format(experience_end_time, '%Y-%m-%d') as Time from user_goldexperience_relation where experience_status = '2'\n" +
				"\t\t"+
				")aaaaa WHERE !ISNULL(Time) and (Time BETWEEN '"+beginTime+"' and '"+endTime+"') GROUP BY Time ORDER BY Time DESC ");
		return this.jdbcTemplate.queryForList(sb.toString());
	}


	public List<Map<String, Object>> GainFinancingProject(String beginTime,String endTime,String Status,String financing,Integer start,Integer length,List<Whitelist> whitelist) {
		// TODO Auto-generated method stub
		String whiteSql = "";
		if(!CollectionUtils.isEmpty(whitelist)){
			for(int i = 0; i < whitelist.size(); i++){
				whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
			}
		}else{
			whiteSql = "and 1 = 1 ";
		}
		final StringBuilder sb = new StringBuilder();

		sb.append("SELECT\n" +
				"\tSerialNum as SerialNum,\n" +
				"\tTitle as Title,\n" +
				"\tCreateTime as CreateTime,\n" +
				"\tStatus as Status,\n" +
				"\tCAST(Amount as DECIMAL(20,4)) as Amount,\n" +
				"\tschedules as schedules,\n" +
				"\tCAST(reciviceTotal as DECIMAL(20,5)) as received,\n" +
				"\tCAST(paidTotal as DECIMAL(20,5)) as paid\n" +
				"FROM(\n" +
				"\t\n" +
				"\tselect\n" +
				"\ta.SerialNum,\n" +
				"\ta.FinancingUuid,\n" +
				"\ta.Title,\n" +
				"\ta.CreateTime,\n" +
				"\tcase \n" +
				"\twhen a.Status = '0' then \"待开启\"\n" +
				"\twhen a.Status = '1' then \"募集中\"\n" +
				"\twhen a.Status = '2' then \"募集暂停\"\n" +
				"\twhen a.Status = '3' then \"募集结束\"\n" +
				"\tend 'Status',\n" +
				"\ta.Amount as Amount,\n" +
				"\tCONCAT(IFNULL(ROUND(b.amountBCB/a.Amount*100,2),0),\"%\") as schedules,\n" +
				"\tIFNULL(b.paidTotal,0) as paidTotal,\n" +
				"\t(IFNULL(b.reciviceTotal,0) + IFNULL(b.amountUSDX,0)) as reciviceTotal\n" +
				"\t\n" +
				"\tfrom (\n" +
				"\t\tselect FinancingUuid,SerialNum,Title,CreateTime,STATUS,Amount from financing_base_info \n" +
				"\t)a\n" +
				"\tLEFT JOIN(\n" +
				"\t\n" +
				"\t\tselect financing_uuid,SUM(IFNULL(amountUSDX,0)) as amountUSDX,SUM(IFNULL(amountBCB,0)) as amountBCB,SUM(IFNULL(paidTotal,0)) as paidTotal,SUM(IFNULL(reciviceTotal,0)) as reciviceTotal from(\n" +
				"\t\t\n" +
				"\t\tselect ba.financing_uuid,SUM(IFNULL(ba.amountUSDX,0)) as amountUSDX,SUM(IFNULL(ba.amountBCB,0)) as amountBCB,SUM(IFNULL(paidTotal,0)) as paidTotal,SUM(IFNULL(reciviceTotal,0)) as reciviceTotal from(\n" +
				"\t\t\t\tselect financing_uuid,id,SUM(IFNULL(usdx_amount,0)) as amountUSDX,SUM(IFNULL(bcb_amount,0)) as amountBCB from user_financing_record where 1=1 "+whiteSql+" and record_status BETWEEN 1 AND 4 group by id \n" +
				"\t\t)ba \n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect record_id,SUM(IFNULL(paidTotal,0)) as paidTotal,SUM(IFNULL(reciviceTotal,0)) as reciviceTotal FROM(\n" +
				"\n" +
				"\t\t\t\tselect user_financing_record_id as record_id,(fixed_usdx_amount + extra_usdx_amount - foundation_commission*bcb2usdx - superior_commission_tax*bcb2usdx + principal_usdx_amount) as paidTotal,(foundation_commission + service_amount  + superior_commission_tax)*bcb2usdx as reciviceTotal from user_financing_settlement \n" +
				"\t\t\t\tUNION ALL\n" +
				"\t\t\t\tselect i.record_id as record_id,i.amount*j.bcb2usdx as paidTotal,i.tax*j.bcb2usdx as reciviceTotal from user_agents_financing_bill i join user_financing_record j on i.record_id = j.id \n" +
				"\t\t\t\tUNION ALL\n" +
				"\t\t\t\tselect record_id,ic_seletment as paidTotal,0 as reciviceTotal from user_interst_coupons_relation where ic_status = 2 and !ISNULL(bcb2usdx)\n" +
				"\n" +
				"\t\t\t)baa GROUP BY record_id\n" +
				"\t\t)bb on bb.record_id = ba.id group by ba.financing_uuid\n" +
				"\t\t\t\n" +
				"\t\tUNION ALL\n" +
				"\t\t\n" +
				"\t\tselect financing_uuid,0 as amountUSDX,0 as amountBCB,SUM(ge_amount*bcb2usdx) as paidTotal,0 as reciviceTotal from user_goldexperience_relation where experience_status = 2 GROUP BY financing_uuid\n" +
				"\t\t\n" +
				"\t\t)cccc GROUP BY financing_uuid\n" +
				"\t\t\n" +
				"\t)b on a.FinancingUuid = b.financing_uuid\n" +
				"\t\n" +
				")aaaaa");

		sb.append(" where Status = '"+FinancingBaseStatusEnum.getByValue(Integer.parseInt(Status)).getDisplay() +"' and (CreateTime Between '"+beginTime+"' and '"+endTime+"') and (SerialNum like '%"+financing+"%' or Title like '%"+financing+"%')");
		sb.append(" ORDER BY CreateTime DESC  LIMIT ?,?");

		return this.jdbcTemplate.queryForList(
				sb.toString(),
				new Object[] {start,length});
	}
	
	public Integer GainCountProjectNum(String beginTime,String endTime,String Status,String financing){

		final StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from financing_base_info where (CreateTime Between '"+beginTime+"' and '"+endTime+"') ");
		if(Status != null && Status.length() > 0){
			if(Status.equals("4")){
				sb.append(" and (Status = '" + 1 + "' or Status = '" + 2 + "' or Status = '" + 3 + "' or Status = '" + 0 + "' )");
			}else{
				sb.append(" and Status = '" + Status + "'");
			}
		}
		if(financing != null && financing.length() > 0){
			sb.append("and (SerialNum like '%"+financing+"%' or Title like '%"+financing+"%')");
		}
		
		return this.jdbcTemplate.queryForObject(sb.toString(),Integer.class);
	}

	public Map<String, Object>  GainFinancingProjectDetail(String projectUuid,List<Whitelist> whitelist) {
		// TODO Auto-generated method stub
		String whiteSql = "";
		if(!CollectionUtils.isEmpty(whitelist)){
			for(int i = 0; i < whitelist.size(); i++){
				whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
			}
		}else{
			whiteSql = "and 1 = 1 ";
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT\n" +
                "\tFinancingUuid as FinancingUuid,\n" +
                "\tTitle as Title,\n" +
                "\tCreateTime as CreateTime,\n" +
                "\tStatus as Status,\n" +
                "\tCAST(IFNULL(Amount,0) as DECIMAL(20,4)) as Amount,\n" +
                "\n" +
                "\tCAST(IFNULL(amountBCB,0) as DECIMAL(20,4)) as CoinAmount,\n" +
                "\tschedules as schedules,\n" +
                "\tIFNULL(humanNum,0) as humanNum,\n" +
                "\tCAST(IFNULL(amountUSDX,0) as DECIMAL(20,4)) as totalInvest,\n" +
                "\n" +
                "\tCAST(IFNULL(received,0) as DECIMAL(20,4)) as received,\n" +
                "\tCAST(IFNULL(paid,0) as DECIMAL(20,4)) as paid,\n" +
                "\tCAST(IFNULL(paidBCB,0) as DECIMAL(20,4)) as paidBCB,\n" +
                "\n" +
                "\tCAST(IFNULL(receivedPaymentFee,0) as DECIMAL(20,4)) as receivedPaymentFee,\n" +
                "\tCAST(IFNULL(receivedServiceFee,0) as DECIMAL(20,4)) as receivedServiceFee,\n" +
                "\tCAST(IFNULL(receivedAchievements,0) as DECIMAL(20,4)) as receivedAchievements,\n" +
                "\tCAST(IFNULL(receivedTax,0) as DECIMAL(20,4)) as receivedTax,\n" +
                "\tCAST(IFNULL(paidPrincipal,0) as DECIMAL(20,4)) as paidPrincipal,\n" +
                "\tCAST(IFNULL(paidAgentRebeat,0) as DECIMAL(20,4)) as paidAgentRebeat,\n" +
                "\tCAST(IFNULL(paidFixedProfit,0) as DECIMAL(20,4)) as paidFixedProfit,\n" +
                "\tCAST(IFNULL(paidAdditionalProfit,0) as DECIMAL(20,4)) as paidAdditionalProfit,\n" +
                "\tCAST(IFNULL(geAmount,0) as DECIMAL(20,4)) as paidGeAmount,\n" +
                "\tCAST(IFNULL(icAmount,0) as DECIMAL(20,4)) as paidIcAmount\n" +
                "FROM(\n" +
                "\tselect \n" +
                "\t\ta.FinancingUuid as FinancingUuid,\n" +
                "\t\ta.Title as Title,\n" +
                "\t\ta.CreateTime as CreateTime,\n" +
                "\t\ta.Status as Status,\n" +
                "\t\ta.Amount as Amount,\n" +
                "\t\tb.amountBCB as amountBCB,\n" +
                "\t\tCONCAT(IFNULL(ROUND((b.amountBCB/a.Amount)*100,2),0),\"%\") as schedules,\n" +
                "\t\tb.humanNum,\n" +
                "\t\tb.amountUSDX,\n" +
                "\t\tb.receivedPaymentFee,\n" +
                "\t\tb.receivedServiceFee,\n" +
                "\t\tb.receivedAchievements,\n" +
                "\t\t(b.reatAgentTax + b.subTax) as receivedTax,\n" +
                "\t\tb.principalUSDX as paidPrincipal,\n" +
                "\t\tb.paidAgentRebeat,\n" +
                "\t\tb.fixedAmountUSDX as paidFixedProfit,\n" +
                "\t\tb.extraAmountUSDX as paidAdditionalProfit,\n" +
                "\t\t(b.receivedPaymentFee + b.receivedServiceFee + b.receivedAchievements + b.reatAgentTax + b.subTax + b.amountUSDX) as received,\n" +
                "\t\t(b.paidAgentRebeat + b.fixedAmountUSDX + b.extraAmountUSDX + b.principalUSDX) as paid,\n" +
                "\t\tb.geAmount as geAmount,\n" +
                "\t\tb.icAmount as icAmount,\n" +
                "\t\tb.paidBCB as paidBCB\n" +
                "\t\t\n" +
                "\tfrom(\n" +
                "\t\tselect FinancingUuid,Title,Status,Amount,CreateTime from financing_base_info \n" +
                "\t)a \n" +
                "\tLEFT JOIN(\n" +
                "\n" +
                "\n" +
                "\t\tSELECT\n" +
                "\t\t\tSUM(humanNum) as humanNum,\n" +
                "\t\t\tfinancing_uuid as financing_uuid,\n" +
                "\t\t\tSUM(IFNULL(amountBCB,0)) as amountBCB,\n" +
                "\t\t\tSUM(IFNULL(amountUSDX,0)) as amountUSDX,\n" +
                "\t\t\tSUM(IFNULL(receivedPaymentFee,0)) as receivedPaymentFee,\n" +
                "\t\t\tSUM(IFNULL(reatAgentTax,0)) as reatAgentTax,\n" +
                "\t\t\tSUM(IFNULL(fixedAmountUSDX,0)) as fixedAmountUSDX,\n" +
                "\t\t\tSUM(IFNULL(extraAmountUSDX,0)) as extraAmountUSDX,\n" +
                "\t\t\tSUM(IFNULL(receivedAchievements,0)) as receivedAchievements,\n" +
                "\t\t\tSUM(IFNULL(receivedServiceFee,0)) as receivedServiceFee,\n" +
                "\t\t\tSUM(IFNULL(subTax,0)) as subTax,\n" +
                "\t\t\tSUM(IFNULL(principal,0)) as principalUSDX,\n" +
                "\t\t\tSUM(IFNULL(paidAgentRebeat,0)) as paidAgentRebeat,\n" +
                "\t\t\tSUM(IFNULL(icAmount,0)) as icAmount,\n" +
                "\t\t\tSUM(IFNULL(geamountUSDX,0)) as geamount,\n" +
                "\t\t\tSUM(IFNULL(paidBCB,0) + IFNULL(geamountBCB,0)) as paidBCB\n" +
                "\t\tFROM(\n" +
                "\t\t\n" +
                "\t\tselect \n" +
                "\t\t\tCOUNT(DISTINCT(user_id)) as humanNum,\n" +
                "\t\t\tfinancing_uuid as financing_uuid,\n" +
                "\t\t\tSUM(IFNULL(bcb_amount,0)) as amountBCB,\n" +
                "\t\t\tSUM(IFNULL(usdx_amount,0)) as amountUSDX,\n" +
                "\t\t\tSUM(IFNULL(subscription_fee,0)) as receivedPaymentFee,\n" +
                "\t\t\tSUM(IFNULL(tax,0)*bcb2usdx) as reatAgentTax,\n" +
                "\t\t\tSUM(IFNULL(principal_usdx_amount,0)) as principal,\n" +
                "\t\t\tSUM(IFNULL(fixed_usdx_amount,0)) as fixedAmountUSDX,\n" +
                "\t\t\tSUM(IFNULL(extra_usdx_amount,0)) as extraAmountUSDX,\n" +
                "\t\t\tSUM(IFNULL(foundation_commission,0)) as receivedAchievements,\n" +
                "\t\t\tSUM(IFNULL(service_amount,0)) as receivedServiceFee,\n" +
                "\t\t\tSUM(IFNULL(superior_commission_tax,0)) as subTax,\n" +
                "\t\t\tSUM(IFNULL(coin_amount,0)*bcb2usdx) as paidAgentRebeat,\n" +
                "\t\t\tSUM(IFNULL(ic_seletment,0)) as icAmount,\n" +
                "\t\t\t0 as geamountBCB,\n" +
                "\t\t\t0 as geamountUSDX,\n" +
                "\t\t\tSUM(IFNULL(paidBCB,0)) as paidBCB\n" +
                "\t\tfrom(\n" +
                "\t\t\t\n" +
                "\n" +
                "\t\t\t\n" +
                "\t\t\tselect \n" +
                "\t\t\t\tba.user_id as user_id,\n" +
                "\t\t\t\tba.financing_uuid as financing_uuid,\n" +
                "\t\t\t\tba.bcb_amount as bcb_amount,\n" +
                "\t\t\t\tba.usdx_amount as usdx_amount,\n" +
                "\t\t\t\tba.subscription_fee as subscription_fee,\n" +
                "\t\t\t\tbb.tax as tax,\n" +
                "\t\t\t\tba.bcb2usdx as bcb2usdx,\n" +
                "\t\t\t\tbc.principal_usdx_amount as principal_usdx_amount,\n" +
                "\t\t\t\tbc.fixed_usdx_amount as fixed_usdx_amount,\n" +
                "\t\t\t\tbc.extra_usdx_amount as extra_usdx_amount,\n" +
                "\t\t\t\tbc.foundation_commission as foundation_commission,\n" +
                "\t\t\t\tbc.service_amount as service_amount,\n" +
                "\t\t\t\tbc.superior_commission_tax as superior_commission_tax,\n" +
                "\t\t\t\tbb.amount as coin_amount,\n" +
                "\t\t\t\tbe.ic_seletment as ic_seletment,\n" +
                "\t\t\t\t(IFNULL(bc.fixed_bcb_amount,0) + IFNULL(bc.selfExtraBCBamount,0) + IFNULL(bb.amount,0) +  IFNULL(be.ic_seletment/be.bcb2usdx,0) + IFNULL(principal_bcb_amount,0) ) as paidBCB\n" +
                "\t\t\t\t\n" +
                "\t\t\tfrom(\n" +
                "\t\t\t\tselect financing_uuid,id,user_id,bcb_amount,usdx_amount, subscription_fee,bcb2usdx from user_financing_record where 1=1 "+whiteSql+" and record_status BETWEEN 1 AND 4 \n" +
                "\t\t\t)ba\n" +
                "\t\t\tLEFT JOIN(\n" +
                "\t\t\t\tselect record_id,SUM(IFNULL(tax,0)) as tax,SUM(IFNULL(amount,0)) as amount from user_agents_financing_bill GROUP BY record_id\n" +
                "\t\t\t)bb on ba.id = bb.record_id\n" +
                "\t\t\tLEFT JOIN(\n" +
                "\t\t\t\tselect user_financing_record_id,SUM(IFNULL(principal_usdx_amount,0)) as principal_usdx_amount,SUM(IFNULL(principal_bcb_amount,0)) as principal_bcb_amount, SUM(IFNULL(fixed_bcb_amount,0)) as fixed_bcb_amount,SUM(IFNULL(extra_bcb_amount,0) - IFNULL(foundation_commission,0) - IFNULL(superior_commission_tax,0)) as selfExtraBCBamount,SUM(IFNULL(fixed_usdx_amount,0)) as fixed_usdx_amount,SUM(IFNULL(extra_usdx_amount,0) - IFNULL(foundation_commission*bcb2usdx,0) - IFNULL(superior_commission_tax*bcb2usdx,0)) as extra_usdx_amount, sum(IFNULL(foundation_commission,0)*bcb2usdx) as foundation_commission,sum(IFNULL(service_amount,0)*bcb2usdx) as service_amount,sum(IFNULL(superior_commission_tax,0)*bcb2usdx) as superior_commission_tax from user_financing_settlement GROUP BY user_financing_record_id\n" +
                "\t\t\t)bc on bc.user_financing_record_id = ba.id \n" +
                "\t\t\tLEFT JOIN(\n" +
                "\t\t\t\tselect ic_seletment,bcb2usdx,record_id from user_interst_coupons_relation where !ISNULL(bcb2usdx) and ic_status = 2\n" +
                "\t\t\t)be on be.record_id = ba.id\n" +
                "\t\t)bd GROUP BY financing_uuid\n" +
                "\n" +
                "\t\tUNION ALL\n" +
                "\t\t\n" +
                "\t\tselect 0 as humanNum,financing_uuid,0 as amountBCB,0 as amountUSDX,0 as receivedPaymentFee,0 as reatAgentTax,0 as principal,0 as fixedAmountUSDX,0 as extraAmountUSDX,0 as receivedAchievements,0 as receivedServiceFee,0 as subTax, 0 as paidAgentRebeat, 0 as icAmount, SUM(IFNULL(ge_amount,0)) as geamountBCB,SUM(IFNULL(ge_amount*bcb2usdx,0)) as geamountUSDX,0 as paidBCB from user_goldexperience_relation where experience_status = 2 GROUP BY financing_uuid\n" +
                "\t\t)bbbbb GROUP BY financing_uuid\n" +
                "\t)b on a.FinancingUuid = b.financing_uuid\n" +
                "\n" +
                "\t\n" +
                ")aaaaa");
		sb.append(" where FinancingUuid = '"+projectUuid+"'");
		return this.jdbcTemplate.queryForMap(sb.toString());
	}

	public List<Map<String, Object>> GainUserFinancingRelation(String financingUuid, Integer start, Integer length,List<Whitelist> whitelist) {
		// TODO Auto-generated method stub
		String whiteSql = "";
		if(whitelist != null && whitelist.size() > 0){
			for(int i = 0; i < whitelist.size(); i++){
				whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
			}
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT\n" +
                "\tuserId,\n" +
                "\tdisplayName as displayName,\n" +
                "\tfromToken as fromToken,\n" +
                "\ttoToken as toToken,\n" +
                "\tCAST(paymentAmount as DECIMAL(10,4)) as paymentAmount,\n" +
                "\tpaymentType as paymentType,\n" +
                "\tCAST(usdxAmount as DECIMAL(10,4)) as usdxAmount,\n" +
                "\tbcb2usdx as bcb2usdx,\n" +
                "\tcreateTime as createTime,\n" +
                "\texpireTime as expireTime,\n" +
                "  financingUuid as financingUuid\n" +
                "FROM(\n" +
                "\n" +
                "SELECT\n" +
                "\tIFNULL(b.userId,\"\") as userId,\n" +
                "\tIFNULL(b.financingUuid,\"\") as financingUuid,\n" +
                "\tIFNULL(a.displayName,\"\") as displayName,\n" +
                "\tIFNULL(b.fromToken,\"\") as fromToken,\n" +
                "\tIFNULL(b.toToken,\"\") as toToken,\n" +
                "\tIFNULL(b.paymentType,\"\") as paymentType,\n" +
                "\tIFNULL(b.paymentAmount,\"\") as paymentAmount,\n" +
                "\tIFNULL(b.usdxAmount,\"\") as usdxAmount,\n" +
                "\tIFNULL(b.createTime,\"\")as createTime,\n" +
                "\tIFNULL(b.expireTime,\"\") as expireTime,\n" +
                "\tIFNULL(b.bcb2usdx,\"\")as bcb2usdx\n" +
                "FROM(\n" +
                "\tselect \n" +
                "\t\tDisplayName as displayName,\n" +
                "\t\tUserId as userId\n" +
                "\tfrom user_account_info\n" +
                "\t)a\n" +
                "RIGHT JOIN(\n" +
                "\tselect \n" +
                "\t\trecord_create_time as createTime,\n" +
                "\t\tfinancing_uuid as financingUuid,\n" +
                "\t\tfrom_address as fromToken,\n" +
                "\t\tto_address as toToken,\n" +
                "\t\tuser_id as userId, \n" +
                "\t\tcoin_name as paymentType,\n" +
                "\t\tamount as paymentAmount,\n" +
                "\t\tusdx_amount as usdxAmount,\n" +
                "\t\texpire_time as expireTime,\n" +
                "\t\tbcb2usdx as bcb2usdx\n" +
                "\tfrom user_financing_record where 1=1 "+whiteSql+"\n" +
                ")b on b.UserId = a.UserId\n" +
                ")aaaa where financingUuid = '"+financingUuid+"' \n" +
                "\n");
		sb.append(" ORDER BY createTime DESC  LIMIT ?,?");
		return this.jdbcTemplate.queryForList(
				sb.toString(),
				new Object[] {start,length});
	}
	
	public Integer GainUserFinancingRelationNumByUuid(String financingUuid,List<Whitelist> whitelist){
		String whiteSql = "";
		if(whitelist != null && whitelist.size() > 0){
			for(int i = 0; i < whitelist.size(); i++	){
				whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
			}
		}
		final StringBuilder sb = new StringBuilder();

		sb.append("select count(*) from user_financing_record ");
		if(financingUuid != null && financingUuid.length() > 0){
			sb.append(" where financing_uuid = '"+financingUuid+"' ");
		}else{
			sb.append(" where 1 = 1 ");
		}
		
		sb.append(whiteSql);
		
		return this.jdbcTemplate.queryForObject(sb.toString(),Integer.class);
	}
    public List<Map<String, Object>> GainUserFinancingRelationAll(String financingUuid,List<Whitelist> whitelist){
        String whiteSql = "";
        if(whitelist != null && whitelist.size() > 0){
            for(int i = 0; i < whitelist.size(); i++){
                whiteSql = whiteSql + "and from_address!='" + whitelist.get(i).getAddress() + "' ";
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT\n" +
                "\tuserId,\n" +
                "\tdisplayName as displayName,\n" +
                "\tfromToken as fromToken,\n" +
                "\ttoToken as toToken,\n" +
                "\tCAST(paymentAmount as DECIMAL(10,4)) as paymentAmount,\n" +
                "\tpaymentType as paymentType,\n" +
                "\tCAST(usdxAmount as DECIMAL(10,4)) as usdxAmount,\n" +
                "\tbcb2usdx as bcb2usdx,\n" +
                "\tcreateTime as createTime,\n" +
                "\texpireTime as expireTime,\n" +
                "  financingUuid as financingUuid\n" +
                "FROM(\n" +
                "\n" +
                "SELECT\n" +
                "\tIFNULL(b.userId,\"\") as userId,\n" +
                "\tIFNULL(b.financingUuid,\"\") as financingUuid,\n" +
                "\tIFNULL(a.displayName,\"\") as displayName,\n" +
                "\tIFNULL(b.fromToken,\"\") as fromToken,\n" +
                "\tIFNULL(b.toToken,\"\") as toToken,\n" +
                "\tIFNULL(b.paymentType,\"\") as paymentType,\n" +
                "\tIFNULL(b.paymentAmount,\"\") as paymentAmount,\n" +
                "\tIFNULL(b.usdxAmount,\"\") as usdxAmount,\n" +
                "\tIFNULL(b.createTime,\"\")as createTime,\n" +
                "\tIFNULL(b.expireTime,\"\") as expireTime,\n" +
                "\tIFNULL(b.bcb2usdx,\"\")as bcb2usdx\n" +
                "FROM(\n" +
                "\tselect \n" +
                "\t\tDisplayName as displayName,\n" +
                "\t\tUserId as userId\n" +
                "\tfrom user_account_info\n" +
                "\t)a\n" +
                "RIGHT JOIN(\n" +
                "\tselect \n" +
                "\t\trecord_create_time as createTime,\n" +
                "\t\tfinancing_uuid as financingUuid,\n" +
                "\t\tfrom_address as fromToken,\n" +
                "\t\tto_address as toToken,\n" +
                "\t\tuser_id as userId, \n" +
                "\t\tcoin_name as paymentType,\n" +
                "\t\tamount as paymentAmount,\n" +
                "\t\tusdx_amount as usdxAmount,\n" +
                "\t\texpire_time as expireTime,\n" +
                "\t\tbcb2usdx as bcb2usdx\n" +
                "\tfrom user_financing_record where 1=1 "+whiteSql+"\n" +
                ")b on b.UserId = a.UserId\n" +
                ")aaaa where financingUuid = '"+financingUuid+"'\n" +
                "\n");
		sb.append(" ORDER BY createTime DESC");

        return this.jdbcTemplate.queryForList(sb.toString());
    }


	public List<Map<String, Object>> GainFinancingUserList(Integer start, Integer length, String UserId,String iSortCol_0,String sSortDir_0) {
		// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
		sb.append("SELECT \n" +
                "\tUserId as UserId,\n" +
                "\tDisplayName as DisplayName,\n" +
                "\tcase\n" +
                "\t\twhen  ISNULL(FinancingNum) then '-'\n" +
                "\t\telse FinancingNum\n" +
                "\tend FinancingNum,\n" +
                "\tcase\n" +
                "\t\twhen  ISNULL(UsdxAmount) then '-'\n" +
                "\t\telse UsdxAmount\n" +
                "\tend UsdxAmount,\n" +
                "\tcase\n" +
                "\t\twhen  ISNULL(reviceIncome) then '-'\n" +
                "\t\telse reviceIncome\n" +
                "\t\tend reviceIncome,\n" +
                "\tcase\n" +
                "\t\twhen ISNULL(AgentIncome) then '-'\n" +
                "\t\telse AgentIncome\n" +
                "\tend AgentIncome\n" +
                "FROM(\n" +
                "\t\n" +
                "\tselect a.UserId,a.DisplayName,c.FinancingNum as FinancingNum,c.amountUSDX as UsdxAmount,c.reviceIncome as reviceIncome,(c.AgentIncomeRebeat + b.superior_commission) as AgentIncome from(\n" +
                "\t\tselect UserId,DisplayName from user_account_info\n" +
                "\t)a\n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect ba.ParentId as userid,SUM(IFNULL(bc.superior_commission,0)*IFNULL(bcb2usdx,0)) as superior_commission from(\n" +
                "\t\t\tselect userid,ParentId from user_account_info where !ISNULL(ParentId)\n" +
                "\t\t)ba\n" +
                "\t\tLEFT JOIN(\n" +
                "\t\t\tselect id,user_id from user_financing_record\n" +
                "\t\t)bb on bb.user_id = ba.userid \n" +
                "\t\tLEFT JOIN(\n" +
                "\t\t\tselect user_financing_record_id,bcb2usdx,superior_commission from user_financing_settlement\n" +
                "\t\t)bc on bb.id = bc.user_financing_record_id GROUP BY ba.ParentId\n" +
                "\t)b on a.UserId = b.userid \n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect \n" +
                "\t\t\tuserid,\n" +
                "\t\t\tsum(IFNULL(FinancingNum,0)) as FinancingNum,\n" +
                "\t\t\tSUM(IFNULL(amountUSDX,0)) as amountUSDX,\n" +
                "\t\t\tSUM(IFNULL(reviceIncome,0)) as reviceIncome,\n" +
                "\t\t\tSUM(IFNULL(AgentIncomeRebeat,0)) as AgentIncomeRebeat\n" +
                "\t\tfrom(\n" +
                "\t\t\tselect ca.user_id as userid,COUNT(DISTINCT(ca.FinancingNum)) as FinancingNum,SUM(IFNULL(ca.amountUSDX,0)) as amountUSDX,SUM(IFNULL(cb.reviceIncome,0)) as reviceIncome,cb.AgentIncomeRebeat as AgentIncomeRebeat from(\n" +
                "\t\t\t\tselect user_id,financing_uuid as FinancingNum,usdx_amount as amountUSDX,bcb2usdx as bcb2usdx,id from user_financing_record \n" +
                "\t\t\t)ca  \n" +
                "\t\t\tLEFT JOIN(\n" +
                "\t\t\t\tselect sum((IFNULL(fixed_usdx_amount,0)+IFNULL(extra_usdx_amount,0) - IFNULL(superior_commission*bcb2usdx,0) - IFNULL(superior_commission_tax*bcb2usdx,0) - IFNULL(foundation_commission*bcb2usdx,0) )) as reviceIncome,sum(IFNULL(superior_commission*bcb2usdx,0)) as AgentIncomeRebeat,user_financing_record_id from user_financing_settlement group by  user_financing_record_id\n" +
                "\t\t\t)cb on cb.user_financing_record_id = ca.id GROUP BY ca.user_id\n" +
                "\t\t\t\n" +
                "\t\t\tUNION ALL\n" +
                "\n" +
                "\t\t\tSELECT cc.user_id as userid,0 as FinancingNum,'0.00' as amountUSDX,'0.00' as reviceIncome,(cc.amount*ce.bcb2usdx) as AgentIncomeRebeat from(\n" +
                "\t\t\t\tselect amount,user_id,record_id from user_agents_financing_bill\n" +
                "\t\t\t)cc \n" +
                "\t\t\tLEFT JOIN(\n" +
                "\t\t\t\t select id,bcb2usdx from user_financing_record\n" +
                "\t\t\t)ce on cc.record_id = ce.id GROUP BY cc.user_id\n" +
                "\n" +
                "\t\t\tUNION ALL\n" +
                "\t\t\t\n" +
                "\t\t\tselect user_id as userId,0 as FinancingNum,'0.00' as amountUSDX,ge_amount*bcb2usdx as reviceIncome,'0.00' as AgentIncomeRebeat  from user_goldexperience_relation where experience_status = 2\n" +
                "\t\t\t\n" +
                "\t\t\tUNION ALL\n" +
                "\n" +
                "\t\t\tselect user_id as userId,0 as FinancingNum,'0.00' as amountUSDX,ic_seletment as reviceIncome, '0.00' as AgentIncomeRebeat from user_interst_coupons_relation WHERE !ISNULL(bcb2usdx) and ic_status = 2\n" +
                "\n" +
                "\t\t)cd GROUP BY userid \n" +
                "\t)c on c.userid = a.UserId GROUP BY a.UserId \n" +
                ")aaaa ");
		if(UserId == null) UserId = "";
		if(UserId.length() > 0 ){
			sb.append(" where (UserId like '%"+UserId+"%' or DisplayName like '%"+UserId+"%')");
		}
		if(iSortCol_0.equals("6")){
			sb.append(" ORDER BY reviceIncome "+sSortDir_0+"  LIMIT ?,?");
		}else if(iSortCol_0.equals("7")){
			sb.append(" ORDER BY AgentIncome "+sSortDir_0+"  LIMIT ?,?");
		}else{
			sb.append(" ORDER BY UserId DESC  LIMIT ?,?");
		}
		
		return this.jdbcTemplate.queryForList(
				sb.toString(),
				new Object[] {start,length});
	}

	public Integer GainFinancingUserListNum(String UserId) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from (");
		sb.append("SELECT \n" +
                "\tUserId as UserId,\n" +
                "\tDisplayName as DisplayName,\n" +
                "\tcase\n" +
                "\t\twhen  ISNULL(FinancingNum) then '-'\n" +
                "\t\telse FinancingNum\n" +
                "\tend FinancingNum,\n" +
                "\tcase\n" +
                "\t\twhen  ISNULL(UsdxAmount) then '-'\n" +
                "\t\telse UsdxAmount\n" +
                "\tend UsdxAmount,\n" +
                "\tcase\n" +
                "\t\twhen  ISNULL(reviceIncome) then '-'\n" +
                "\t\telse reviceIncome\n" +
                "\t\tend reviceIncome,\n" +
                "\tcase\n" +
                "\t\twhen ISNULL(AgentIncome) then '-'\n" +
                "\t\telse AgentIncome\n" +
                "\tend AgentIncome\n" +
                "FROM(\n" +
                "\t\n" +
                "\tselect a.UserId,a.DisplayName,c.FinancingNum as FinancingNum,c.amountUSDX as UsdxAmount,c.reviceIncome as reviceIncome,(c.AgentIncomeRebeat + b.superior_commission) as AgentIncome from(\n" +
                "\t\tselect UserId,DisplayName from user_account_info\n" +
                "\t)a\n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect ba.ParentId as userid,SUM(IFNULL(bc.superior_commission,0)*IFNULL(bcb2usdx,0)) as superior_commission from(\n" +
                "\t\t\tselect userid,ParentId from user_account_info where !ISNULL(ParentId)\n" +
                "\t\t)ba\n" +
                "\t\tLEFT JOIN(\n" +
                "\t\t\tselect id,user_id from user_financing_record\n" +
                "\t\t)bb on bb.user_id = ba.userid \n" +
                "\t\tLEFT JOIN(\n" +
                "\t\t\tselect user_financing_record_id,bcb2usdx,superior_commission from user_financing_settlement\n" +
                "\t\t)bc on bb.id = bc.user_financing_record_id GROUP BY ba.ParentId\n" +
                "\t)b on a.UserId = b.userid \n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect \n" +
                "\t\t\tuserid,\n" +
                "\t\t\tsum(IFNULL(FinancingNum,0)) as FinancingNum,\n" +
                "\t\t\tSUM(IFNULL(amountUSDX,0)) as amountUSDX,\n" +
                "\t\t\tSUM(IFNULL(reviceIncome,0)) as reviceIncome,\n" +
                "\t\t\tSUM(IFNULL(AgentIncomeRebeat,0)) as AgentIncomeRebeat\n" +
                "\t\tfrom(\n" +
                "\t\t\tselect ca.user_id as userid,COUNT(DISTINCT(ca.FinancingNum)) as FinancingNum,SUM(IFNULL(ca.amountUSDX,0)) as amountUSDX,SUM(IFNULL(cb.reviceIncome,0)) as reviceIncome,cb.AgentIncomeRebeat as AgentIncomeRebeat from(\n" +
                "\t\t\t\tselect user_id,financing_uuid as FinancingNum,usdx_amount as amountUSDX,bcb2usdx as bcb2usdx,id from user_financing_record \n" +
                "\t\t\t)ca  \n" +
                "\t\t\tLEFT JOIN(\n" +
                "\t\t\t\tselect sum((IFNULL(fixed_usdx_amount,0)+IFNULL(extra_usdx_amount,0) - IFNULL(superior_commission*bcb2usdx,0) - IFNULL(superior_commission_tax*bcb2usdx,0) - IFNULL(foundation_commission*bcb2usdx,0) )) as reviceIncome,sum(IFNULL(superior_commission*bcb2usdx,0)) as AgentIncomeRebeat,user_financing_record_id from user_financing_settlement group by  user_financing_record_id\n" +
                "\t\t\t)cb on cb.user_financing_record_id = ca.id GROUP BY ca.user_id\n" +
                "\t\t\t\n" +
                "\t\t\tUNION ALL\n" +
                "\n" +
                "\t\t\tSELECT cc.user_id as userid,0 as FinancingNum,'0.00' as amountUSDX,'0.00' as reviceIncome,(cc.amount*ce.bcb2usdx) as AgentIncomeRebeat from(\n" +
                "\t\t\t\tselect amount,user_id,record_id from user_agents_financing_bill\n" +
                "\t\t\t)cc \n" +
                "\t\t\tLEFT JOIN(\n" +
                "\t\t\t\t select id,bcb2usdx from user_financing_record\n" +
                "\t\t\t)ce on cc.record_id = ce.id GROUP BY cc.user_id\n" +
                "\n" +
                "\t\t\tUNION ALL\n" +
                "\t\t\t\n" +
                "\t\t\tselect user_id as userId,0 as FinancingNum,'0.00' as amountUSDX,ge_amount*bcb2usdx as reviceIncome,'0.00' as AgentIncomeRebeat  from user_goldexperience_relation where experience_status = 2\n" +
                "\t\t\t\n" +
                "\t\t\tUNION ALL\n" +
                "\n" +
                "\t\t\tselect user_id as userId,0 as FinancingNum,'0.00' as amountUSDX,ic_seletment as reviceIncome, '0.00' as AgentIncomeRebeat from user_interst_coupons_relation WHERE !ISNULL(bcb2usdx) and ic_status = 2\n" +
                "\n" +
                "\t\t)cd GROUP BY userid \n" +
                "\t)c on c.userid = a.UserId GROUP BY a.UserId \n" +
                ")aaaa  ");
		sb.append(") aaaaaa");

		if(UserId == null) UserId = "";
		if(UserId.length() > 0 ){
			sb.append(" where (UserId like '%"+UserId+"%' or DisplayName like '%"+UserId+"%')");
		}
		return this.jdbcTemplate.queryForObject(sb.toString(),Integer.class);
	}

	public List<Map<String, Object>> GainFinancingUserInvestmentIncomeList(String beginTime, String endTime,
			Integer start, Integer length, String UserId) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");
		
		sb.append("SELECT\n" +
				"\tifnull(UserId,'-')  as UserId,\n" +
				"\tifnull(Title,'-')  as Title,\n" +
				"\tifnull(Time,'-') as Time,\n" +
				"\tIFNULL(CAST(totalInvest as DECIMAL(20,4)),\"-\") as totalInvest,\n" +
				"\tIFNULL(CAST(receivedPaymentFee as DECIMAL(20,4)),\"-\") as receivedPaymentFee,\n" +
				"\tIFNULL(CAST(paidFixedProfit as DECIMAL(20,4)),\"-\") as paidFixedProfit,\n" +
				"\tIFNULL(CAST(TrueExtraUsdxAmount as DECIMAL(20,4)),\"-\") as TrueExtraUsdxAmount,\n" +
				"\tIFNULL(CAST(SuperiorUsdxAmount as DECIMAL(20,4)),\"-\") as SuperiorUsdxAmount,\n" +
				"\tIFNULL(CAST(SuperiorUsdxAmountTax as DECIMAL(20,4)),\"-\") as SuperiorUsdxAmountTax,\n" +
				"\tIFNULL(CAST(receivedAchievements as DECIMAL(20,4)),\"-\") as receivedAchievements,\n" +
				"\tIFNULL(CAST(paidAdditionalProfit as DECIMAL(20,4)),\"-\") as paidAdditionalProfit,\n" +
				"\tIFNULL(CAST(geAmount as DECIMAL(20,4)),\"-\") as geAmount,\n" +
				"\tIFNULL(CAST(icAmount as DECIMAL(20,4)),\"-\") as icAmount\n" +
				"FROM(\n" +
				"\t\n" +
				"\tselect \n" +
				"\t\ta.user_id as UserId,\n" +
				"\t\td.Title as  Title,\n" +
				"\t\ta.record_create_time as Time,\n" +
				"\t\tSUM(IFNULL(a.usdx_amount,0)) as totalInvest,\n" +
				"\t\tSUM(IFNULL(a.subscription_fee,0)) as receivedPaymentFee,\n" +
				"\t\tSUM(IFNULL(c.fixed_usdx_amount,0)) as paidFixedProfit,\n" +
				"\t\tSUM(IFNULL((IFNULL(c.extra_usdx_amount,0)),0)) as TrueExtraUsdxAmount,\n" +
				"\t\tSUM(IFNULL(c.superior_commission,0)) as SuperiorUsdxAmount,\n" +
				"\t\tSUM(IFNULL(c.superior_commission_tax,0)) as SuperiorUsdxAmountTax,\n" +
				"\t\tSUM(IFNULL(c.foundation_commission,0)) as receivedAchievements,\n" +
				"\t\tSUM(IFNULL(c.extra_usdx_amount - c.foundation_commission - c.superior_commission - c.superior_commission_tax,'-')) as paidAdditionalProfit,\n" +
				"\t\tSUM(IFNULL(e.geAmount,'-')) as geAmount,\n" +
				"\t\tSUM(IFNULL(f.ic_seletment,'-')) as icAmount\n" +
				"\tfrom(\n" +
				"\t\tselect id,user_id,financing_uuid,usdx_amount,subscription_fee,record_create_time from user_financing_record where record_status BETWEEN 3 and 4\n" +
				"\t)a\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect sum(ifnull(fixed_usdx_amount,0)) as fixed_usdx_amount,sum(ifnull(extra_usdx_amount,0)) as extra_usdx_amount,sum(ifnull(foundation_commission,0)*bcb2usdx) as foundation_commission,user_financing_record_id,sum(ifnull(superior_commission,0)*bcb2usdx) as superior_commission,sum(ifnull(superior_commission_tax,0)*bcb2usdx) as superior_commission_tax from user_financing_settlement group by   user_financing_record_id\n" +
				"\t)c on c.user_financing_record_id = a.id\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect FinancingUuid,Title from financing_base_info\n" +
				"\t)d on d.FinancingUuid = a.financing_uuid \n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect user_id,ge_amount*bcb2usdx as geAmount,financing_uuid from user_goldexperience_relation where experience_status = 2\n" +
				"\t)e on a.user_id = e.user_id and a.financing_uuid = e.financing_uuid\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect user_id,record_id,ic_seletment from user_interst_coupons_relation where !ISNULL(bcb2usdx) and ic_status=2\n" +
				"\t)f on f.user_id = a.user_id and f.record_id = a.id GROUP BY UserId,Title,Time\n" +
				"\t\n" +
				"\tUNION ALL\n" +
				"\n" +
				"\tselect \n" +
				"\t\ta.user_id as UserId,\n" +
				"\t\td.Title as  Title,\n" +
				"\t\ta.record_create_time as Time,\n" +
				"\t\tSUM(IFNULL(a.usdx_amount,0)) as totalInvest,\n" +
				"\t\tSUM(IFNULL(a.subscription_fee,0)) as receivedPaymentFee,\n" +
				"\t\tnull as paidFixedProfit,\n" +
				"\t\tnull as TrueExtraUsdxAmount,\n" +
				"\t\tnull as SuperiorUsdxAmount,\n" +
				"\t\tnull as SuperiorUsdxAmountTax,\n" +
				"\t\tnull as receivedAchievements,\n" +
				"\t\tnull as paidAdditionalProfit,\n" +
				"\t\tSUM(IFNULL(b.geAmount,'-')) as geAmount,\n" +
				"\t\tnull as icAmount\n" +
				"\tFROM(\n" +
				"\t\tselect id,user_id,financing_uuid,usdx_amount,subscription_fee,record_create_time from user_financing_record where record_status = 2\n" +
				"\t)a\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect user_id,ge_amount*bcb2usdx as geAmount,financing_uuid from user_goldexperience_relation where experience_status = 2\n" +
				"\t)b on a.user_id = b.user_id and a.financing_uuid = b.financing_uuid\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect FinancingUuid,Title from financing_base_info\n" +
				"\t)d on d.FinancingUuid = a.financing_uuid GROUP BY UserId,Title,Time \n" +
				"\n" +
				")aaaaaa");
		sb.append(")zyx where Time > '"+beginTime+"' and Time < '"+endTime+"' and UserId = '"+UserId+"'");
		
		sb.append(" ORDER BY Time DESC  LIMIT ?,?");
		
		return this.jdbcTemplate.queryForList(
				sb.toString(),
				new Object[] {start,length});
	}

	public Integer GainFinancingUserInvestmentIncomeListNum(String beginTime, String endTime, String UserId) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from (");
		sb.append("SELECT\n" +
                "\tifnull(UserId,'-') as UserId,\n" +
                "\tifnull(Title,'-') as Title,\n" +
                "\tifnull(Time,'-') as Time,\n" +
                "\tCAST(totalInvest as DECIMAL(20,4)) as totalInvest,\n" +
                "\tCAST(receivedPaymentFee as DECIMAL(20,4)) as receivedPaymentFee,\n" +
                "\tCAST(paidFixedProfit as DECIMAL(20,4)) as paidFixedProfit,\n" +
                "\tCAST(TrueExtraUsdxAmount as DECIMAL(20,4)) as TrueExtraUsdxAmount,\n" +
                "\tCAST(SuperiorUsdxAmount as DECIMAL(20,4)) as SuperiorUsdxAmount,\n" +
                "\tCAST(SuperiorUsdxAmountTax as DECIMAL(20,4)) as SuperiorUsdxAmountTax,\n" +
                "\tCAST(receivedAchievements as DECIMAL(20,4)) as receivedAchievements,\n" +
                "\tCAST(paidAdditionalProfit as DECIMAL(20,4)) as paidAdditionalProfit,\n" +
                "\tCAST(geAmount as DECIMAL(20,4)) as geAmount,\n" +
                "\tCAST(icAmount as DECIMAL(20,4)) as icAmount\n" +
                "FROM(\n" +
                "\t\n" +
                "\tselect \n" +
                "\t\ta.user_id as UserId,\n" +
                "\t\td.Title as  Title,\n" +
                "\t\ta.record_create_time as Time,\n" +
                "\t\tSUM(IFNULL(a.usdx_amount,0)) as totalInvest,\n" +
                "\t\tSUM(IFNULL(a.subscription_fee,0)) as receivedPaymentFee,\n" +
                "\t\tSUM(IFNULL(c.fixed_usdx_amount,0)) as paidFixedProfit,\n" +
                "\t\tSUM(IFNULL((IFNULL(c.extra_usdx_amount,0)),0)) as TrueExtraUsdxAmount,\n" +
                "\t\tSUM(IFNULL(c.superior_commission,0)) as SuperiorUsdxAmount,\n" +
                "\t\tSUM(IFNULL(c.superior_commission_tax,0)) as SuperiorUsdxAmountTax,\n" +
                "\t\tSUM(IFNULL(c.foundation_commission,0)) as receivedAchievements,\n" +
                "\t\tSUM(IFNULL(c.extra_usdx_amount,0) - IFNULL(c.foundation_commission,0) - IFNULL(c.superior_commission,0) - IFNULL(c.superior_commission_tax,0)) as paidAdditionalProfit,\n" +
                "\t\tSUM(IFNULL(e.geAmount,0)) as geAmount,\n" +
                "\t\tSUM(IFNULL(f.ic_seletment,0)) as icAmount\n" +
                "\tfrom(\n" +
                "\t\tselect id,user_id,financing_uuid,usdx_amount,subscription_fee,record_create_time from user_financing_record\n" +
                "\t)a\n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect sum(ifnull(fixed_usdx_amount,0)) as fixed_usdx_amount,sum(ifnull(extra_usdx_amount,0)) as extra_usdx_amount,sum(ifnull(foundation_commission,0)*bcb2usdx) as foundation_commission,user_financing_record_id,sum(ifnull(superior_commission,0)*bcb2usdx) as superior_commission,sum(ifnull(superior_commission_tax,0)*bcb2usdx) as superior_commission_tax from user_financing_settlement group by   user_financing_record_id\n" +
                "\t)c on c.user_financing_record_id = a.id\n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect FinancingUuid,Title from financing_base_info\n" +
                "\t)d on d.FinancingUuid = a.financing_uuid \n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect user_id,ge_amount*bcb2usdx as geAmount,financing_uuid from user_goldexperience_relation where experience_status = 2\n" +
                "\t)e on a.user_id = e.user_id and a.financing_uuid = e.financing_uuid\n" +
                "\tLEFT JOIN(\n" +
                "\t\tselect user_id,record_id,ic_seletment from user_interst_coupons_relation where !ISNULL(bcb2usdx) and ic_status=2\n" +
                "\t)f on f.user_id = a.user_id and f.record_id = a.id GROUP BY UserId,Title,Time\n" +
                "\t \n" +
                "\n" +
                ")aaaaaa");
		sb.append(")zyx where Time > '"+beginTime+"' and Time < '"+endTime+"' and UserId = '"+UserId+"'");
		
		return this.jdbcTemplate.queryForObject(sb.toString(),Integer.class);
	}

	public List<Map<String, Object>> GainFinancingUserAgent(String beginTime, String endTime, Integer start,
			Integer length, String UserId) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");

		sb.append("SELECT\n" +
				"\tifnull(UserId,'-')  as UserId,\n" +
				"\tifnull(Time,'-')  as Time,\n" +
				"\tifnull(RebeatType,'-')  as RebeatType,\n" +
				"\tifnull(Juniorid,'-')  as Juniorid,\n" +
				"\tifnull(TsId,'-')  as TsId,\n" +
				"\tifnull(CoinType,'-') as CoinType,\n" +
				"\tifnull(CAST(CoinAmount as DECIMAL(10,4)),'-') as CoinAmount,\n" +
				"\tifnull(CAST(taxBCB as DECIMAL(10,4)),'-') as taxBCB,\n" +
				"\tifnull(CAST(receiveBCB as DECIMAL(10,4)),'-')  as receiveBCB\n" +
				"FROM(\n" +
				"\n" +
				"select \n" +
				"\tUserId,     #用户id\n" +
				"\tTime,       #时间\n" +
				"\tRebeatType,  #返回类型\n" +
				"\tJuniorid,    #下级代理id\n" +
				"\tTsId,\n" +
				"\tCoinType,    #收益币种\n" +
				"\tSUM(CoinAmount) as CoinAmount,   #收益数量\n" +
				"\tSUM(tax) as taxBCB,   #税费\n" +
				"\tsum(CoinAmount - tax) as receiveBCB   #应得代理收益（BCB）\n" +
				"from(\n" +
				"\n" +
				"\tselect\n" +
				"\t\tUserId,\n" +
				"\t\tTime,\n" +
				"\t\t\"项目返点\" as RebeatType,\n" +
				"\t\tJuniorid,\n" +
				"\t\tTsId,\n" +
				"\t\tIFNULL(CoinAmount,'0.00') as CoinAmount,\n" +
				"\t\tCoinType,\n" +
				"\t\ttax\n" +
				"\tfrom(\n" +
				"\t\tselect \n" +
				"\t\t\ta.UserId,\n" +
				"\t\t\ta.CreateTime as Time,\n" +
				"\t\t\tb.Juniorid,\n" +
				"\t\t\tb.TsId,\n" +
				"\t\t\ta.CoinAmount,\n" +
				"\t\t\ta.CoinType,\n" +
				"\t\t\ta.tax\n" +
				"\t\tfrom(\n" +
				"\t\t\tselect\n" +
				"\t\t\t\tcreate_time as CreateTime,\n" +
				"\t\t\t\trecord_id as  RelationId,\n" +
				"\t\t\t\tuser_id as UserId,\n" +
				"\t\t\t\tamount as CoinAmount,\n" +
				"\t\t\t\t'BCB' as CoinType,\n" +
				"\t\t\t\ttax as tax\n" +
				"\t\t\tfrom \n" +
				"\t\t\tuser_agents_financing_bill\n" +
				"\t\t)a\n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect \n" +
				"\t\t\t\tid,\n" +
				"\t\t\t\ttx_id as TsId,\n" +
				"\t\t\t\tuser_id as Juniorid \n" +
				"\t\t\tfrom \n" +
				"\t\t\t\tuser_financing_record\n" +
				"\t\t)b on a.RelationId = b.id\n" +
				"\t) aa\n" +
				"\n" +
				"\tUNION ALL\n" +
				"\n" +
				"\tselect \n" +
				"\t\tParentId as UserId,\n" +
				"\t\tsettlement_date as Time,\n" +
				"\t\t\"下级分佣\" as RebeatType,\n" +
				"\t\tuserid as Juniorid,\n" +
				"\t\ttx_id as TsId,\n" +
				"\t\t(superior_commission+superior_commission_tax) as CoinAmount,\n" +
				"\t\t\"BCB\" as Coin,\n" +
				"\t\tsuperior_commission_tax as tax\n" +
				"\tfrom(\n" +
				"\t\tselect ba.ParentId,bc.settlement_date,ba.userid,bb.tx_id,IFNULL(bc.superior_commission,0) as superior_commission,superior_commission_tax from(\n" +
				"\t\t\tselect userid,ParentId from user_account_info where !ISNULL(ParentId)\n" +
				"\t\t)ba\n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect id,user_id,tx_id from user_financing_record\n" +
				"\t\t)bb on bb.user_id = ba.userid \n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect user_financing_record_id,bcb2usdx,superior_commission,settlement_date,superior_commission_tax from user_financing_settlement where superior_commission != 0\n" +
				"\t\t)bc on bb.id = bc.user_financing_record_id\n" +
				"\t)cc where  !ISNULL(ParentId) and !ISNULL(UserId) and superior_commission != 0\n" +
				"\n" +
				")dd GROUP BY CoinType,UserId,Juniorid\n" +
				")aaaaa");
		
		sb.append(")xyz where UserId = '"+UserId+"' and Time > '"+beginTime+"' and Time < '"+endTime+"'");
		
		sb.append(" ORDER BY Time DESC  LIMIT ?,?");
		
		return this.jdbcTemplate.queryForList(
				sb.toString(),
				new Object[] {start,length});
	}

	public Integer GainFinancingUserAgentNum(String beginTime, String endTime, String UserId) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("Select count(*) from ( ");
		sb.append("SELECT\n" +
				"\tifnull(UserId,'-')  as UserId,\n" +
				"\tifnull(Time,'-')  as Time,\n" +
				"\tifnull(RebeatType,'-')  as RebeatType,\n" +
				"\tifnull(Juniorid,'-')  as Juniorid,\n" +
				"\tifnull(TsId,'-')  as TsId,\n" +
				"\tifnull(CoinType,'-') as CoinType,\n" +
				"\tifnull(CAST(CoinAmount as DECIMAL(10,4)),'-') as CoinAmount,\n" +
				"\tifnull(CAST(taxBCB as DECIMAL(10,4)),'-') as taxBCB,\n" +
				"\tifnull(CAST(receiveBCB as DECIMAL(10,4)),'-')  as receiveBCB\n" +
				"FROM(\n" +
				"\n" +
				"select \n" +
				"\tUserId,     #用户id\n" +
				"\tTime,       #时间\n" +
				"\tRebeatType,  #返回类型\n" +
				"\tJuniorid,    #下级代理id\n" +
				"\tTsId,\n" +
				"\tCoinType,    #收益币种\n" +
				"\tSUM(CoinAmount) as CoinAmount,   #收益数量\n" +
				"\tSUM(tax) as taxBCB,   #税费\n" +
				"\tsum(CoinAmount - tax) as receiveBCB   #应得代理收益（BCB）\n" +
				"from(\n" +
				"\n" +
				"\tselect\n" +
				"\t\tUserId,\n" +
				"\t\tTime,\n" +
				"\t\t\"项目返点\" as RebeatType,\n" +
				"\t\tJuniorid,\n" +
				"\t\tTsId,\n" +
				"\t\tIFNULL(CoinAmount,'0.00') as CoinAmount,\n" +
				"\t\tCoinType,\n" +
				"\t\ttax\n" +
				"\tfrom(\n" +
				"\t\tselect \n" +
				"\t\t\ta.UserId,\n" +
				"\t\t\ta.CreateTime as Time,\n" +
				"\t\t\tb.Juniorid,\n" +
				"\t\t\tb.TsId,\n" +
				"\t\t\ta.CoinAmount,\n" +
				"\t\t\ta.CoinType,\n" +
				"\t\t\ta.tax\n" +
				"\t\tfrom(\n" +
				"\t\t\tselect\n" +
				"\t\t\t\tcreate_time as CreateTime,\n" +
				"\t\t\t\trecord_id as  RelationId,\n" +
				"\t\t\t\tuser_id as UserId,\n" +
				"\t\t\t\tamount as CoinAmount,\n" +
				"\t\t\t\t'BCB' as CoinType,\n" +
				"\t\t\t\ttax as tax\n" +
				"\t\t\tfrom \n" +
				"\t\t\tuser_agents_financing_bill\n" +
				"\t\t)a\n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect \n" +
				"\t\t\t\tid,\n" +
				"\t\t\t\ttx_id as TsId,\n" +
				"\t\t\t\tuser_id as Juniorid \n" +
				"\t\t\tfrom \n" +
				"\t\t\t\tuser_financing_record\n" +
				"\t\t)b on a.RelationId = b.id\n" +
				"\t) aa \n" +
				"\n" +
				"\tUNION ALL\n" +
				"\n" +
				"\tselect \n" +
				"\t\tParentId as UserId,\n" +
				"\t\tsettlement_date as Time,\n" +
				"\t\t\"下级分佣\" as RebeatType,\n" +
				"\t\tuserid as Juniorid,\n" +
				"\t\ttx_id as TsId,\n" +
				"\t\t(superior_commission+superior_commission_tax) as CoinAmount,\n" +
				"\t\t\"BCB\" as Coin,\n" +
				"\t\tsuperior_commission_tax as tax\n" +
				"\tfrom(\n" +
				"\t\tselect ba.ParentId,bc.settlement_date,ba.userid,bb.tx_id,IFNULL(bc.superior_commission,0) as superior_commission,superior_commission_tax from(\n" +
				"\t\t\tselect userid,ParentId from user_account_info where !ISNULL(ParentId)\n" +
				"\t\t)ba\n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect id,user_id,tx_id from user_financing_record\n" +
				"\t\t)bb on bb.user_id = ba.userid \n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect user_financing_record_id,bcb2usdx,superior_commission,settlement_date,superior_commission_tax from user_financing_settlement where superior_commission != 0\n" +
				"\t\t)bc on bb.id = bc.user_financing_record_id\n" +
				"\t)cc where  !ISNULL(ParentId) and !ISNULL(UserId) and superior_commission != 0\n" +
				"\n" +
				")dd GROUP BY CoinType,UserId,Juniorid\n" +
				")aaaaa");
		sb.append(")xyz where UserId = '"+UserId+"' and Time > '"+beginTime+"' and Time < '"+endTime+"'");
		return this.jdbcTemplate.queryForObject(sb.toString(),Integer.class);
	}

	public List<Map<String, Object>> GainFinancingUserJunior(String UserId,String Junior, Integer start, Integer length,String iSortCol_0,String sSortDir_0) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");
		sb.append("SELECT\n" +
				"\tifnull(UserId,'-') as UserId,\n" +
				"\tifnull(Juniorid,'-') as Juniorid,\n" +
				"\tifnull(DisplayName,'-') as DisplayName,\n" +
				"\tifnull(JuniorStep,'-') as JuniorStep,\n" +
				"\tifnull(CAST(RebeatBcbAmount as DECIMAL(10,4)),'-') as RebeatBcbAmount,\n" +
				"\tifnull(CAST(JunioridBcbAmount as DECIMAL(10,4)),'-') as JunioridBcbAmount,\n" +
				"\tifnull(CAST(taxBCB as DECIMAL(10,4)),'-') as taxBCB,\n" +
				"\tifnull(CAST(reviceBCB as DECIMAL(10,4)),'-') as reviceBCB\n" +
				"FROM(\n" +
				"select\n" +
				"\tUserId as UserId,    #用户id\n" +
				"\tJuniorid as Juniorid,   #2级代理id\n" +
				"\tDisplayName as DisplayName,   #2级代理昵称\n" +
				"\tJuniorStep as JuniorStep,   #代理等级\n" +
				"\tSUM(RebeatBcbAmount) as RebeatBcbAmount,   #项目返点\n" +
				"\tSUM(JunioridBcbAmount) as JunioridBcbAmount,   #代理分佣\n" +
				"\tSUM(taxBCB) as taxBCB,  #税费\n" +
				"\tSUM(reviceBCB) as reviceBCB   # 合计返佣BCB\n" +
				"from(\n" +
				"\tselect \n" +
				"\t\tal.UserId as UserId,    \n" +
				"\t\tal.Juniorid as Juniorid,   \n" +
				"\t\tal.DisplayName as DisplayName,   \n" +
				"\t\tal.JuniorStep as JuniorStep,   \n" +
				"\t\tbl.CoinAmount as RebeatBcbAmount,   \n" +
				"\t\tcl.CoinAmount as JunioridBcbAmount,   \n" +
				"\t\t(bl.rebeatTax + cl.subTax) as taxBCB,  \n" +
				"\t\t(bl.CoinAmount + cl.CoinAmount) as reviceBCB   \n" +
				"\tfrom(\n" +
				"\t\tselect \n" +
				"\t\t\tParentId as UserId,\n" +
				"\t\t\tUserId as Juniorid,\n" +
				"\t\t\tDisplayName,\n" +
				"\t\t\t'2级' as JuniorStep \n" +
				"\t\tfrom user_account_info where !ISNULL(ParentId)\n" +
				"\t)al\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect\n" +
				"\t\t\tbb.user_id as Juniorid,\n" +
				"\t\t\tIFNULL(ba.CoinAmount,0) as CoinAmount,\n" +
				"\t\t\ttax as rebeatTax\n" +
				"\t\tfrom(\n" +
				"\t\t\tselect \n" +
				"\t\t\t\tamount as CoinAmount,\n" +
				"\t\t\t\trecord_id,\n" +
				"\t\t\t\ttax\n" +
				"\t\t\tfrom user_agents_financing_bill\n" +
				"\t\t)ba\n" +
				"\t\tRIGHT JOIN(\n" +
				"\t\t\tselect id,user_id from user_financing_record\n" +
				"\t\t)bb on ba.record_id = bb.id\n" +
				"\t)bl on al.Juniorid = bl.Juniorid\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect IFNULL(xy.CoinAmount,0) as CoinAmount,xs.UserId as Juniorid,IFNULL(xy.subTax,0) as subTax FROM(\n" +
				"\t\t\tselect user_id as UserId ,id from user_financing_record\n" +
				"\t\t)xs\n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect user_financing_record_id,superior_commission as CoinAmount,superior_commission_tax as subTax from user_financing_settlement where superior_commission != 0\n" +
				"\t\t)xy on xs.id = xy.user_financing_record_id\n" +
				"\t)cl on al.Juniorid = cl.Juniorid \n" +
				")dl group by  Juniorid \n" +
				")aaaa");
		sb.append(")xyz where UserId = '"+UserId+"'");
		if(Junior == null) Junior = "";
		if(Junior.length() > 0 ){
			sb.append(" and ( Juniorid like '%"+Junior+"%' or DisplayName like '%"+Junior+"%')");
		}
		if(iSortCol_0.equals("6")){
			sb.append(" ORDER BY reviceBCB "+sSortDir_0+"  LIMIT ?,?");
		}else{
			sb.append(" ORDER BY Juniorid DESC  LIMIT ?,?");
		}

		return this.jdbcTemplate.queryForList(
				sb.toString(),
				new Object[] {start,length});
	}

	public Integer GainGainFinancingUserJuniorNum(String UserId,String Junior) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from (");
		sb.append("SELECT\n" +
				"\tifnull(UserId,'-') as UserId,\n" +
				"\tifnull(Juniorid,'-') as Juniorid,\n" +
				"\tifnull(DisplayName,'-') as DisplayName,\n" +
				"\tifnull(JuniorStep,'-') as JuniorStep,\n" +
				"\tifnull(CAST(RebeatBcbAmount as DECIMAL(10,4)),'-') as RebeatBcbAmount,\n" +
				"\tifnull(CAST(JunioridBcbAmount as DECIMAL(10,4)),'-') as JunioridBcbAmount,\n" +
				"\tifnull(CAST(taxBCB as DECIMAL(10,4)),'-') as taxBCB,\n" +
				"\tifnull(CAST(reviceBCB as DECIMAL(10,4)),'-') as reviceBCB\n" +
				"FROM(\n" +
				"select\n" +
				"\tUserId as UserId,    #用户id\n" +
				"\tJuniorid as Juniorid,   #2级代理id\n" +
				"\tDisplayName as DisplayName,   #2级代理昵称\n" +
				"\tJuniorStep as JuniorStep,   #代理等级\n" +
				"\tSUM(RebeatBcbAmount) as RebeatBcbAmount,   #项目返点\n" +
				"\tSUM(JunioridBcbAmount) as JunioridBcbAmount,   #代理分佣\n" +
				"\tSUM(taxBCB) as taxBCB,  #税费\n" +
				"\tSUM(reviceBCB) as reviceBCB   # 合计返佣BCB\n" +
				"from(\n" +
				"\tselect \n" +
				"\t\tal.UserId as UserId,    \n" +
				"\t\tal.Juniorid as Juniorid,   \n" +
				"\t\tal.DisplayName as DisplayName,   \n" +
				"\t\tal.JuniorStep as JuniorStep,   \n" +
				"\t\tbl.CoinAmount as RebeatBcbAmount,   \n" +
				"\t\tcl.CoinAmount as JunioridBcbAmount,   \n" +
				"\t\t(bl.rebeatTax + cl.subTax) as taxBCB,  \n" +
				"\t\t(bl.CoinAmount + cl.CoinAmount) as reviceBCB   \n" +
				"\tfrom(\n" +
				"\t\tselect \n" +
				"\t\t\tParentId as UserId,\n" +
				"\t\t\tUserId as Juniorid,\n" +
				"\t\t\tDisplayName,\n" +
				"\t\t\t'2级' as JuniorStep \n" +
				"\t\tfrom user_account_info where !ISNULL(ParentId)\n" +
				"\t)al\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect\n" +
				"\t\t\tbb.user_id as Juniorid,\n" +
				"\t\t\tIFNULL(ba.CoinAmount,0) as CoinAmount,\n" +
				"\t\t\ttax as rebeatTax\n" +
				"\t\tfrom(\n" +
				"\t\t\tselect \n" +
				"\t\t\t\tamount as CoinAmount,\n" +
				"\t\t\t\trecord_id,\n" +
				"\t\t\t\ttax\n" +
				"\t\t\tfrom user_agents_financing_bill\n" +
				"\t\t)ba\n" +
				"\t\tRIGHT JOIN(\n" +
				"\t\t\tselect id,user_id from user_financing_record\n" +
				"\t\t)bb on ba.record_id = bb.id\n" +
				"\t)bl on al.Juniorid = bl.Juniorid\n" +
				"\tLEFT JOIN(\n" +
				"\t\tselect IFNULL(xy.CoinAmount,0) as CoinAmount,xs.UserId as Juniorid,IFNULL(xy.subTax,0) as subTax FROM(\n" +
				"\t\t\tselect user_id as UserId ,id from user_financing_record\n" +
				"\t\t)xs\n" +
				"\t\tLEFT JOIN(\n" +
				"\t\t\tselect user_financing_record_id,superior_commission as CoinAmount,superior_commission_tax as subTax from user_financing_settlement where superior_commission != 0\n" +
				"\t\t)xy on xs.id = xy.user_financing_record_id\n" +
				"\t)cl on al.Juniorid = cl.Juniorid \n" +
				")dl group by  Juniorid \n" +
				")aaaa");
		sb.append(")xyz where UserId = '"+UserId+"'");
		if(Junior == null) Junior = "";
		if(Junior.length() > 0 ){
			sb.append(" and ( Juniorid like '%"+Junior+"%' or DisplayName like '%"+Junior+"%')");
		}
		return this.jdbcTemplate.queryForObject(sb.toString(),Integer.class);
	}

	public List<Map<String, Object>> GainStatUserDto(String userid) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select * from( ");
		sb.append("select DISTINCT(b.Userid) as Userid,	b.DisPlayName,IFNULL(a.Token,'-') as Token from( select UserId, Token from user_wallet_info )a RIGHT JOIN( 	select UserId,DisPlayName from user_account_info )b on a.UserId = b.UserId");
		sb.append(")xyz where Userid = '"+userid+"'");
		return   this.jdbcTemplate.queryForList(sb.toString());
	}

	public List<String> GainJuniorAgentNumList(String userId) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select userId from user_account_info where ParentId = '"+userId+"'");
		return this.jdbcTemplate.queryForList(sb.toString(), String.class);
	}

	public String GainParentId(String UserId) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select DISTINCT(ParentId) as ParentId from user_account_info where UserId = '"+UserId+"'");
		return this.jdbcTemplate.queryForObject(sb.toString(),String.class);
	}



	public List<Map<String, Object>> GainFinancingTransferList(Integer tranferId,String user,Integer type, String beginTime, String endTime,Integer pageSize,Integer pageNum) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");
		sb.append("select\n" +
				"\tifnull(a.id,'-') as id,\n" +
				"\tifnull(a.type,'-') as type,\n" +
				"\tifnull(a.user_id,'-') as userId,\n" +
				"\tifnull(b.DisplayName,'-') as displayName,\n" +
				"\tifnull(a.from_address,'-') as fromAddress,\n" +
				"\tifnull(a.to_address,'-') as toAddress,\n" +
				"\tifnull(c.name,'-') as coinId,\n" +
				"\tifnull(a.amount,'-') as amount,\n" +
				"\tifnull(a.modify_time,'-') as modifyTime\n" +
				"FROM(\n" +
				"\tselect * from user_transfer\n" +
				") a \n" +
				"LEFT JOIN(\n" +
				"\tselect * from user_account_info\n" +
				")b on a.user_id = b.UserId\n" +
				"LEFT JOIN(\n" +
				"\tselect * from system_coin\n" +
				")c on a.coin_id = c.id");
		sb.append(")xyz where 1=1 ");

		if(!StringUtils.isEmpty(tranferId)){
			sb.append(" and id like '%"+tranferId+"%' ");
		}

		if(!StringUtils.isEmpty(user)){
			sb.append(" and (userId like '%"+user+"%' or displayName like '%"+user+"%') ");
		}

		if(!StringUtils.isEmpty(type)){
			if(type != 0){
				sb.append(" and type = '"+type+"'");
			}
		}

		sb.append(" and (modifyTime BETWEEN '"+beginTime+"' and '"+endTime+"')");
		sb.append(" ORDER BY modifyTime DESC  LIMIT ?,?");

		return this.jdbcTemplate.queryForList(
				sb.toString(),
				new Object[] {(pageNum-1)*pageSize,pageSize});
	}

	public List<Map<String, Object>> GainFinancingTransferListAll(Integer tranferId,String user,Integer type, String beginTime, String endTime) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");
		sb.append("select\n" +
				"\tifnull(a.id,'-') as id,\n" +
				"\tifnull(a.type,'-') as type,\n" +
				"\tifnull(a.user_id,'-') as userId,\n" +
				"\tifnull(b.DisplayName,'-') as displayName,\n" +
				"\tifnull(a.from_address,'-') as fromAddress,\n" +
				"\tifnull(a.to_address,'-') as toAddress,\n" +
				"\tifnull(c.name,'-')  as coinId,\n" +
				"\tifnull(a.amount,'-') as amount,\n" +
				"\tifnull(a.modify_time,'-') as modifyTime\n" +
				"FROM(\n" +
				"\tselect * from user_transfer\n" +
				") a \n" +
				"LEFT JOIN(\n" +
				"\tselect * from user_account_info\n" +
				")b on a.user_id = b.UserId\n" +
				"LEFT JOIN(\n" +
				"\tselect * from system_coin\n" +
				")c on a.coin_id = c.id");
		sb.append(")xyz where 1=1 ");

		if(!StringUtils.isEmpty(tranferId)){
			sb.append(" and id like '%"+tranferId+"%' ");
		}

		if(!StringUtils.isEmpty(user)){
			sb.append(" and (userId like '%"+user+"%' or displayName like '%"+user+"%') ");
		}

		if(!StringUtils.isEmpty(type)){
			if(type != 0){
				sb.append(" and type = '"+type+"'");
			}
		}
		sb.append(" and (modifyTime BETWEEN '"+beginTime+"' and '"+endTime+"')");
		sb.append(" ORDER BY modifyTime DESC ");
		return this.jdbcTemplate.queryForList(sb.toString());
	}


	public Integer GainFinancingTransferNum(Integer tranferId,String user,Integer type, String beginTime, String endTime,Integer pageSize,Integer pageNum) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from (");
		sb.append("select\n" +
				"\tifnull(a.id,'-') as id,\n" +
				"\tifnull(a.type,'-') as type,\n" +
				"\tifnull(a.user_id,'-') as userId,\n" +
				"\tifnull(b.DisplayName,'-') as displayName,\n" +
				"\tifnull(a.from_address,'-') as fromAddress,\n" +
				"\tifnull(a.to_address,'-') as toAddress,\n" +
				"\tifnull(c.name,'-')  as coinId,\n" +
				"\tifnull(a.amount,'-') as amount,\n" +
				"\tifnull(a.modify_time,'-') as modifyTime\n" +
				"FROM(\n" +
				"\tselect * from user_transfer\n" +
				") a \n" +
				"LEFT JOIN(\n" +
				"\tselect * from user_account_info\n" +
				")b on a.user_id = b.UserId\n" +
				"LEFT JOIN(\n" +
				"\tselect * from system_coin\n" +
				")c on a.coin_id = c.id");
		sb.append(")xyz where 1=1 ");

		if(!StringUtils.isEmpty(tranferId)){
			sb.append(" and id like '%"+tranferId+"%' ");
		}

		if(!StringUtils.isEmpty(user)){
			sb.append(" and (userId like '%"+user+"%' or displayName like '%"+user+"%') ");
		}

		if(!StringUtils.isEmpty(type)){
			if(type != 0){
				sb.append(" and type = '"+type+"'");
			}
		}

		sb.append(" and (modifyTime BETWEEN '"+beginTime+"' and '"+endTime+"')");

		return this.jdbcTemplate.queryForObject(sb.toString(),Integer.class);
	}

}
