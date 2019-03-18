package com.bcb.manager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.bcb.bean.dto.OperationsManager.InterstCouponsUserDto;
import com.bcb.bean.dto.statistics.*;
import com.bcb.domain.dao.StatisticsDao;
import com.bcb.domain.entity.*;
import com.bcb.domain.repository.*;
import com.bcb.service.StatisticsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Slf4j
@Service
public class StatisticsManager {

	@Autowired
	StatisticsService statisticsService;

	@Autowired
	WhitelistRepository whitelistRepository;

	@Autowired
	FinancingBaseInfoRepository financingBaseInfoRepository;

	@Autowired
	UserFinancingRecordRepository userFinancingRecordRepository;

	@Autowired
	UserTransferRepository userTransferRepository;

	@Autowired
	UserFinancingSettlementRepository userFinancingSettlementRepository;

	@Autowired
	SystemConfigurationRepository systemConfigurationRepository;


	@Autowired
	StatisticsDao statisticsDao;


	public StatOverviewDto OverviewManager(Integer flag, String beginTime, String endTime) throws Exception {
		StatOverviewDto statOverviewDto = new StatOverviewDto();
		try{

			//获取白名单
			List<Whitelist> whitelist = new ArrayList<>();
			if (flag == 1) {
				whitelist = whitelistRepository.findByState(true);
			}

			Map<String,Object> map = statisticsDao.GainOverView(whitelist,beginTime,endTime);

			List<UserFinancingRecord> userFinancingRecordList = userFinancingRecordRepository.findByRecordCreateTimeBetweenAndRecordStatusBetween(dateFormate(beginTime,"yyyy-MM-dd HH:mm:ss"),dateFormate(endTime,"yyyy-MM-dd HH:mm:ss"),1,4);
			BigDecimal amountUSDX = BigDecimal.ZERO;
			BigDecimal amountETH = BigDecimal.ZERO;
			BigDecimal amountTotalUSDX = BigDecimal.ZERO;
			for(int i = 0; i < userFinancingRecordList.size(); i++){
				if(StringUtils.isEmpty(userFinancingRecordList.get(i).getCoinName()))
					continue;

				if(userFinancingRecordList.get(i).getCoinName().equals("USDX")){
					amountUSDX = amountUSDX.add(userFinancingRecordList.get(i).getAmount());
					amountTotalUSDX = amountTotalUSDX.add(userFinancingRecordList.get(i).getUsdxAmount());
				}

				if(userFinancingRecordList.get(i).getCoinName().equals("ETH")){
					amountETH = amountETH.add(userFinancingRecordList.get(i).getAmount());
					amountTotalUSDX = amountTotalUSDX.add(userFinancingRecordList.get(i).getUsdxAmount());
				}
			}
			statOverviewDto.setFinancingCount(Integer.parseInt(map.get("financingCount").toString()));
			statOverviewDto.setTotalInvestUSDX(new BigDecimal(bigDecimalFormate(amountUSDX)));
			statOverviewDto.setTotalInvestETH(new BigDecimal(bigDecimalFormate(amountETH)));
			statOverviewDto.setTotalInvest(new BigDecimal(bigDecimalFormate(amountTotalUSDX)));

			statOverviewDto.setReceivedPrincipal(new BigDecimal(map.get("receivedPrincipal").toString()));
			statOverviewDto.setReceivedPaymentFee(new BigDecimal(map.get("receivedPaymentFee").toString()));
			statOverviewDto.setReceivedServiceFee(new BigDecimal(map.get("receivedServiceFee").toString()));
			statOverviewDto.setReceivedAchievements(new BigDecimal(map.get("receivedAchievements").toString()));
			statOverviewDto.setReceivedTax(new BigDecimal(map.get("receivedTax").toString()));
			statOverviewDto.setReceivedWithdrawFee(new BigDecimal(map.get("receivedWithdrawFee").toString()).toString() + "BCB");
			statOverviewDto.setReceivedTotal(statOverviewDto.getReceivedPrincipal().add(statOverviewDto.getReceivedPaymentFee()).add(statOverviewDto.getReceivedServiceFee()).add(statOverviewDto.getReceivedAchievements()).add(statOverviewDto.getReceivedTax()));

			statOverviewDto.setPaidFixedProfit(new BigDecimal(map.get("paidFixedProfit").toString()));
			statOverviewDto.setPaidAdditionalProfit(new BigDecimal(map.get("paidAdditionalProfit").toString()));
			statOverviewDto.setPaidAgentRebeat(new BigDecimal(map.get("paidAgentRebeat").toString()));
			statOverviewDto.setPaidTotalBCB(new BigDecimal(map.get("BcbAmount").toString()));
			statOverviewDto.setPaidTotal(statOverviewDto.getPaidFixedProfit().add(statOverviewDto.getPaidAdditionalProfit()).add(statOverviewDto.getPaidAgentRebeat()));
			return statOverviewDto;

		}catch (Exception e){
			throw new Exception("资金统计-资金概览统计异常",e);
		}
	}

	public StatDiagramsDto StatDiagramsDtoManager(String length, Integer flag) throws Exception {
		StatDiagramsDto statDiagramsDto = new StatDiagramsDto(new ArrayList<BigDecimal>(),new ArrayList<BigDecimal>(),new ArrayList<BigDecimal>(),new ArrayList<String>());
		try{
			List<Whitelist> whitelist = null;
			if (flag == 0) {
				whitelist = whitelistRepository.findByState(true); // 获取白名单列表
			}
			String endTime = StrTime(0);
			String beginTime = StrTime(Integer.parseInt(length)-1);

			List<Map<String,Object>> list = statisticsDao.GainAccountOrder(beginTime,endTime,whitelist);
			int listSize = 0;
			if(!ObjectUtils.isEmpty(list))
				listSize = list.size();
			for(int i = Integer.parseInt(length); i > 0; i--){

				String TimeStr = StrTime(i-1);
				if (ObjectUtils.isEmpty(list) || listSize <= 0){
					statDiagramsDto.getReceivedTotals().add(BigDecimal.ZERO);
					statDiagramsDto.getPaidTotals().add(BigDecimal.ZERO);
					statDiagramsDto.getTime().add(TimeStr);
				}else{
					if(list.get(listSize-1).get("Time").toString().equals(TimeStr)){
						statDiagramsDto.getReceivedTotals().add(new BigDecimal(list.get(listSize-1).get("receivedPrincipal").toString()).add(new BigDecimal(list.get(listSize-1).get("receivedPaymentFee").toString())).add(new BigDecimal(list.get(listSize-1).get("receivedServiceFee").toString())).add(new BigDecimal(list.get(listSize-1).get("receivedAchievements").toString())).add(new BigDecimal(list.get(listSize-1).get("receivedTax").toString())).add(new BigDecimal(list.get(listSize-1).get("receivedWithdrawFee").toString())));
						statDiagramsDto.getPaidTotals().add(new BigDecimal(list.get(listSize-1).get("paidFixedProfit").toString()).add(new BigDecimal(list.get(listSize-1).get("paidAdditionalProfit").toString())).add(new BigDecimal(list.get(listSize-1).get("paidAgentRebeat").toString())));
						statDiagramsDto.getTime().add(list.get(listSize-1).get("Time").toString());
						listSize--;
					}else{
						statDiagramsDto.getReceivedTotals().add(BigDecimal.ZERO);
						statDiagramsDto.getPaidTotals().add(BigDecimal.ZERO);
						statDiagramsDto.getTime().add(TimeStr);
					}
				}
			}
			return statDiagramsDto;
		}catch (Exception e){
			throw new Exception("按照日期获取理财数据统计失败!",e);
		}
	}

	public String StatReceivedFlowsDtoManager(String length, Integer flag, String sEcho) {

		List<Whitelist> whitelist = null;
		if (flag == 1) {
			whitelist = whitelistRepository.findByState(true); // 获取白名单列表
		}
		String endTime = StrTime(0);
		String beginTime = StrTime(Integer.parseInt(length)-1);

		List<Map<String,Object>> list = statisticsDao.GainAccountOrder(beginTime,endTime,whitelist);
		List<StatReceivedFlowsDto> statReceivedFlowsDtoList = new ArrayList<>();
		if(!ObjectUtils.isEmpty(list)){
			for(int i = 0; i < list.size(); i++){
				StatReceivedFlowsDto statReceivedFlowsDto = new StatReceivedFlowsDto();
				statReceivedFlowsDto.setCreateTime(list.get(i).get("Time").toString());
				statReceivedFlowsDto.setReceivedPrincipal(new BigDecimal(list.get(i).get("receivedPrincipal").toString()));
				statReceivedFlowsDto.setReceivedPaymentFee(new BigDecimal(list.get(i).get("receivedPaymentFee").toString()));
				statReceivedFlowsDto.setReceivedServiceFee(new BigDecimal(list.get(i).get("receivedServiceFee").toString()));
				statReceivedFlowsDto.setReceivedAchievements(new BigDecimal(list.get(i).get("receivedAchievements").toString()));
				statReceivedFlowsDto.setReceivedTax(new BigDecimal(list.get(i).get("receivedTax").toString()));
				statReceivedFlowsDto.setReceivedTotal(statReceivedFlowsDto.getReceivedPrincipal().add(statReceivedFlowsDto.getReceivedPaymentFee()).add(statReceivedFlowsDto.getReceivedServiceFee()).add(statReceivedFlowsDto.getReceivedAchievements()).add(statReceivedFlowsDto.getReceivedTax()));
				statReceivedFlowsDtoList.add(statReceivedFlowsDto);
			}
		}
		StringBuilder stringJson = null;
		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + statReceivedFlowsDtoList.size()
				+ ",\"iTotalDisplayRecords\":" + statReceivedFlowsDtoList.size() + ",\"aaData\":[");
		StatReceivedFlowsDto obj = null;
		for (int i = 0; i < statReceivedFlowsDtoList.size(); i++) {
			obj = statReceivedFlowsDtoList.get(i);
			stringJson.append("[");
			stringJson.append("\"" + obj.getCreateTime() + "\",");
			stringJson.append("\"" + obj.getReceivedPrincipal() + "\",");
			stringJson.append("\"" + obj.getReceivedPaymentFee() + "\",");
			stringJson.append("\"" + obj.getReceivedServiceFee() + "\",");
			stringJson.append("\"" + obj.getReceivedAchievements() + "\",");
			stringJson.append("\"" + obj.getReceivedTax() + "\",");
			stringJson.append("\"" + obj.getReceivedTotal() + "\"");
			stringJson.append("],");
		}
		if (statReceivedFlowsDtoList.size() > 0) {
			stringJson.deleteCharAt(stringJson.length() - 1);
		}
		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;
	}

	public String StatPaidFlowsDtoManager(String length, Integer flag, String sEcho) {
		List<Whitelist> whitelist = null;
		if (flag == 1) {
			whitelist = whitelistRepository.findByState(true); // 获取白名单列表
		}
		String endTime = StrTime(0);
		String beginTime = StrTime(Integer.parseInt(length)-1);

		List<Map<String,Object>> list = statisticsDao.GainAccountOrder(beginTime,endTime,whitelist);
		List<StatPaidFlowsDto> statPaidFlowsDtoList = new ArrayList<>();
		if(!ObjectUtils.isEmpty(list)){
			for(int i = 0; i < list.size(); i++){
				StatPaidFlowsDto statPaidFlowsDto = new StatPaidFlowsDto();
				statPaidFlowsDto.setCreateTime(list.get(i).get("Time").toString());
				statPaidFlowsDto.setPaidFixedProfit(new BigDecimal(list.get(i).get("paidFixedProfit").toString()));
				statPaidFlowsDto.setPaidAdditionalProfit(new BigDecimal(list.get(i).get("paidAdditionalProfit").toString()));
				statPaidFlowsDto.setPaidAgentRebeat(new BigDecimal(list.get(i).get("paidAgentRebeat").toString()));
				statPaidFlowsDto.setPaidUSDX(statPaidFlowsDto.getPaidFixedProfit().add(statPaidFlowsDto.getPaidAdditionalProfit()).add(statPaidFlowsDto.getPaidAgentRebeat()));
				statPaidFlowsDto.setPaidBCB(new BigDecimal(list.get(i).get("BcbAmount").toString()));
				statPaidFlowsDtoList.add(statPaidFlowsDto);
			}
		}

		StringBuilder stringJson = null;
		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + statPaidFlowsDtoList.size()
				+ ",\"iTotalDisplayRecords\":" + statPaidFlowsDtoList.size() + ",\"aaData\":[");
		StatPaidFlowsDto obj = null;
		for (int i = 0; i < statPaidFlowsDtoList.size(); i++) {
			obj = statPaidFlowsDtoList.get(i);
			stringJson.append("[");
			stringJson.append("\"" + obj.getCreateTime() + "\",");
			stringJson.append("\"" + obj.getPaidFixedProfit() + "\",");
			stringJson.append("\"" + obj.getPaidAdditionalProfit() + "\",");
			stringJson.append("\"" + obj.getPaidAgentRebeat() + "\",");
			stringJson.append("\"" + obj.getPaidUSDX() + "\",");
			stringJson.append("\"" + obj.getPaidBCB() + "\"");
			stringJson.append("],");
		}
		if (statPaidFlowsDtoList.size() > 0) {
			stringJson.deleteCharAt(stringJson.length() - 1);
		}
		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;

	}



	public String FinancingProjectManager(String sEcho, String beginTime, String endTime, String projectState,
			String projectUuid, Integer start, Integer length, Integer flag) throws Exception {
		List<Whitelist> whitelist = null;
		if (flag == 1) {
			whitelist = whitelistRepository.findByState(true);; // 获取白名单列表
		}
		List<StatFinancingProjectDto> list = statisticsService.GainStatFinancingProjectDto(beginTime, endTime,
				projectState, projectUuid, start, length, whitelist);
		StringBuilder stringJson = null;
		Integer renum = statisticsService.GainCountProjectNum(beginTime, endTime, projectState, projectUuid);
		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + renum
				+ ",\"iTotalDisplayRecords\":" + renum + ",\"aaData\":[");
		StatFinancingProjectDto obj = null;
		for (int i = 0; i < list.size(); i++) {
			obj = list.get(i);
			stringJson.append("[");
			stringJson.append("\"" + obj.getFinancingUuid() + "\",");
			stringJson.append("\"" + obj.getTitle() + "\",");
			stringJson.append("\"" + dateStr(obj.getCreateTime().toString(),"yyyy-MM-dd HH:mm:ss") + "\",");
			stringJson.append("\"" + obj.getStatus() + "\",");
			stringJson.append("\"" + obj.getAmount() + "\",");
			stringJson.append("\"" + obj.getSchedules() + "\",");
			stringJson.append("\"" + obj.getReceived().setScale(4,BigDecimal.ROUND_HALF_DOWN).toEngineeringString() + "\",");
			stringJson.append("\"" + obj.getPaid().setScale(4,BigDecimal.ROUND_HALF_DOWN) + "\",");
			stringJson.append("\"" + "" + "\"");
			stringJson.append("],");
		}
		if (list.size() > 0) {
			stringJson.deleteCharAt(stringJson.length() - 1);
		}
		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;
	}

	public StatFinancingProjectDetailDto FinancingProjectDetailManager(String serialNum, Integer flag) throws Exception {
		List<Whitelist> whitelist = null;
		if (flag == 1) {
			whitelist = whitelistRepository.findByState(true); // 获取白名单列表
		}

		FinancingBaseInfo financingBaseInfo =  financingBaseInfoRepository.findBySerialNum(serialNum);

		return statisticsService.GainStatFinancingProjectDetailDto(financingBaseInfo.getFinancingUuid(), whitelist);
	}

	public String FinancingProjectDetailUserManager(String sEcho, Integer start, Integer length, String serialNum,
			Integer flag) throws Exception {
		// TODO Auto-generated method stub
		List<Whitelist> whitelist = null;
		if (flag == 1) {
			whitelist = whitelistRepository.findByState(true); // 获取白名单列表
		}
		FinancingBaseInfo financingBaseInfo =  financingBaseInfoRepository.findBySerialNum(serialNum);
		List<Map<String, Object>> list = statisticsService.GainUserFinancingRelation(financingBaseInfo.getFinancingUuid(), start, length,
				whitelist);

		StringBuilder stringJson = null;
		Integer renum = statisticsService.GainUserFinancingRelationNumByUuid(financingBaseInfo.getFinancingUuid(), whitelist);
		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + renum
				+ ",\"iTotalDisplayRecords\":" + renum + ",\"aaData\":[");
		Map<String, Object> obj = null;
		for (int i = 0; i < list.size(); i++) {
			obj = list.get(i);
			stringJson.append("[");
			stringJson.append("\"" + (i + 1) + "\",");
			stringJson.append("\"" + obj.get("UserId").toString() + "\",");
			stringJson.append("\"" + obj.get("DisplayName").toString() + "\",");
			stringJson.append("\"" + obj.get("FromToken").toString() + "\",");
			stringJson.append("\"" + obj.get("PaymentAmount").toString() + obj.get("PaymentType").toString() + "\",");
			stringJson.append("\"" + new BigDecimal(obj.get("UsdxAmount").toString()) + "\",");
			stringJson.append("\"" + dateStr(obj.get("CreateTime").toString(),"yyyy-MM-dd HH:mm:ss") + "\"");
			stringJson.append("],");
		}
		if (list.size() > 0) {
			stringJson.deleteCharAt(stringJson.length() - 1);
		}
		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;
	}

	public String FinancingUserManager(String User, String sEcho, Integer iDisplayStart, Integer iDisplayLength,
			String iSortCol_0, String sSortDir_0) {
		StringBuilder stringJson = null;

		String beginTime = "1970-00-00 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


		List<Map<String, Object>> list = statisticsService.GainFinancingUser(User, iDisplayStart, iDisplayLength,
				iSortCol_0, sSortDir_0);
		Integer renum = statisticsService.GainFinancingUserNum(User);

		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + renum
				+ ",\"iTotalDisplayRecords\":" + renum + ",\"aaData\":[");

		for (int i = 0; i < list.size(); i++) {
			
			Map<String, Object> obj = list.get(i);

			stringJson.append("[");
			stringJson.append("\"" + obj.get("UserId").toString() + "\",");
			stringJson.append("\"" + obj.get("DisplayName").toString() + "\",");
			stringJson.append("\"" + (statisticsService.GainAgentStep(obj.get("UserId").toString()) + 1) + "\",");
			stringJson.append("\"" + statisticsService.GainJuniorAgentNum(obj.get("UserId").toString()) + "\",");
			stringJson.append("\"" + obj.get("FinancingNum").toString() + "\",");
			stringJson.append("\"" + bigDecimalFormate(obj.get("UsdxAmount")) + "\",");
			stringJson.append("\"" + bigDecimalFormate(obj.get("reviceIncome")) + "\",");
			stringJson.append("\"" + bigDecimalFormate(obj.get("AgentIncome"))+ "\",");
			stringJson.append("\"" + " " + "\"");
			stringJson.append("],");
		}

		if (list.size() > 0) {
			stringJson.deleteCharAt(stringJson.length() - 1);
		}

		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;
	}

	public StatUserDto FinancingUserMessageManager(String Userid, Integer step) {
		// TODO Auto-generated method stub
		
		return statisticsService.GainFinancingUserMessage(Userid,step);
	}

	public String FinancingUserInvestmentIncomeManager(String userid, String sEcho, Integer start, Integer length,
			String beginTime, String endTime) throws Exception {

		StringBuilder stringJson = null;

		List<Map<String, Object>> list = statisticsService.GainFinancingUserInvestmentIncome(userid, start, length,
				beginTime, endTime);

		Integer renum = statisticsService.GainFinancingUserInvestmentIncomeNum(userid, beginTime, endTime);

		BigDecimal totalInvest = new BigDecimal("0.00");
		BigDecimal FinancingNum = new BigDecimal("0.00");
		BigDecimal IncomeTotal = new BigDecimal("0.00");

		FinancingNum = new BigDecimal(statisticsService.GainFinancingNumByUserId(Integer.parseInt(userid)).size());

		StringBuilder tempStr = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> obj = list.get(i);
			tempStr.append("[");
			tempStr.append("\"" + (i + 1) + "\",");
			tempStr.append("\"" + obj.get("Title").toString() + "\",");
			tempStr.append("\"" + dateStr(obj.get("Time").toString(),"yyyy-MM-dd HH:mm:ss") + "\",");
			tempStr.append("\"" + obj.get("totalInvest").toString() + "\",");
			tempStr.append("\"" + obj.get("receivedPaymentFee").toString() + "\",");
			tempStr.append("\"" + obj.get("paidFixedProfit").toString() + "\",");
			tempStr.append("\"" + obj.get("TrueExtraUsdxAmount").toString() + "\",");
			tempStr.append("\"" + obj.get("SuperiorUsdxAmount").toString() + "\",");
			tempStr.append("\"" + obj.get("receivedAchievements").toString() + "\",");
			tempStr.append("\"" + obj.get("paidAdditionalProfit").toString() + "\"");
			tempStr.append("],");
			totalInvest = totalInvest.add(new BigDecimal(obj.get("totalInvest").toString()));
			IncomeTotal = IncomeTotal.add(new BigDecimal(obj.get("paidFixedProfit").toString()))
					.add(new BigDecimal(obj.get("paidAdditionalProfit").toString()));
		}
		
		if (list.size() > 0) {
			tempStr.deleteCharAt(tempStr.length() - 1);
		}

		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + renum
				+ ",\"iTotalDisplayRecords\":" + renum + ",\"projectNum\":" + FinancingNum + ",\"InvestmentUSDX\":"
				+ totalInvest + ",\"InComeUSDX\":" + IncomeTotal + ",\"aaData\":[");

		stringJson.append(tempStr.toString());

		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;
	}

	public String FinancingUserAgentManager(Integer userid, String sEcho, Integer start, Integer length,
			String beginTime, String endTime) throws Exception {
		StringBuilder stringJson = null;
		Integer renum = 0;

		List<Map<String, Object>> list = statisticsService.GainFinancingUserAgent(beginTime,endTime,start,length,userid+"");
		StringBuilder tempStr = new StringBuilder();
		BigDecimal totalInvest = new BigDecimal("0.00");
		if(list != null){
			renum = list.size();
			for (int i = list.size() - 1 - start; i >= 0 && length > 0; i--) {
				length --;
				Map<String, Object> obj = list.get(i);
				tempStr.append("[");
				tempStr.append("\"" + dateStr(obj.get("Time").toString(),"yyyy-MM-dd HH:mm:ss") + "\",");
				tempStr.append("\"" + obj.get("RebeatType").toString() + "\",");
				tempStr.append("\"" + obj.get("Juniorid").toString() + "\",");
				tempStr.append("\"" + obj.get("TsId").toString() + "\",");
				tempStr.append("\"" + obj.get("CoinAmount").toString() + "\",");
				tempStr.append("\"" + obj.get("taxBCB").toString() + "\",");
				tempStr.append("\"" + obj.get("receiveBCB").toString() + "\"");
				tempStr.append("],");
				totalInvest = totalInvest.add(new BigDecimal(obj.get("receiveBCB").toString()));
			}
			if (list.size() > 0) {
				tempStr.deleteCharAt(tempStr.length() - 1);
			}
		}
		
		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + renum
				+ ",\"iTotalDisplayRecords\":" + renum + ",\"InComeTotal\":" + totalInvest + ",\"aaData\":[");
		stringJson.append(tempStr.toString());

		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;
	}

	public String FinancingUserJuniorManager(String userid, String Junior, String sEcho, Integer start, Integer length,
			String iSortCol_0, String sSortDir_0, Integer step) {
		// TODO Auto-generated method stub
		StringBuilder stringJson = null;
		
		
		String beginTime = "1970-00-00 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		List<Map<String, Object>> list = statisticsService.FinancingUserJunior(userid, Junior, start, length,
				iSortCol_0, sSortDir_0);
		Integer renum = statisticsService.GainFinancingUserJuniorNum(userid, Junior);
		stringJson = new StringBuilder("{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + renum
				+ ",\"iTotalDisplayRecords\":" + renum + ",\"aaData\":[");

		for (int i = 0; i < list.size(); i++) {
			
			Map<String, Object> obj = list.get(i);
			Integer JuniorNum = statisticsService.GainJuniorAgentNum(obj.get("Juniorid").toString());

			stringJson.append("[");
			stringJson.append("\"" + obj.get("Juniorid").toString() + "\",");
			stringJson.append("\"" + obj.get("DisplayName").toString() + "\",");
			stringJson.append("\"" + (step + 1) + "级" + "\",");
			stringJson.append("\"" + JuniorNum + "\",");
			// stringJson.append("\"" + obj.get("RebeatBcbAmount").toString() +
			// "\",");
			// stringJson.append("\"" + obj.get("JunioridBcbAmount").toString()
			// + "\",");
			// stringJson.append("\"" + obj.get("taxBCB").toString() + "\",");
			stringJson.append("\"" + bigDecimalFormate(obj.get("reviceBCB")) + "\",");
			stringJson.append("\"" + "" + "\"");
			stringJson.append("],");
		}

		if (list.size() > 0) {
			stringJson.deleteCharAt(stringJson.length() - 1);
		}

		stringJson.append("]");
		stringJson.append("}");
		String result = stringJson.toString();
		return result;
	}
	
	
	public List<String> financingTranferTimeManager(String Time){
		SimpleDateFormat formatter = new SimpleDateFormat("HH");
		List<UserFinancingRecord> list = statisticsService.GainFinancingTranferTimeManager(Time);
		Map<String,Long> map = new TreeMap<>();
		String key;
		for(int i = 0; i < 24; i++){
			List<Long> tempList = new ArrayList<>();
			if(i < 10){
				key = "0" + i;
			}else{
				key = i+"";
			}
			for(int j = 0; j < list.size(); j++){
				if(formatter.format(list.get(j).getRecordCreateTime()).equals(key)){
					Long min = (list.get(j).getRecordCreateTime().getTime() - list.get(j).getBlockCreateTime().getTime())/(1000 * 60);
					tempList.add(min);
				}
			}
			Long tempCount = (long) 0;
			for(int k = 0; k < tempList.size(); k++){
				tempCount = tempCount + tempList.get(k);
			}
			if(tempList.size() == 0){
				map.put(key, (long) 0);
			}else{
				map.put(key, tempCount/tempList.size());
			}
						
		}
		
		List<String> resultList = new ArrayList<>();
		for(Map.Entry<String, Long> entry : map.entrySet()){
			resultList.add(entry.getValue().toString());
		}
		
		
		return resultList;
		
	}

	public PageInfo<FinancingTranferRecord>  FinancingTranferRecord(Integer tranferId, String user, String beginTime, String endTime, Integer type, Integer pageSize, Integer pageNum) throws Exception {
		PageInfo<FinancingTranferRecord> pageInfo = new PageInfo<>();
		try{

			List<Map<String,Object>> listDao = statisticsDao.GainFinancingTransferList(tranferId,user,type,beginTime,endTime,pageSize,pageNum);
			Integer num = statisticsDao.GainFinancingTransferNum(tranferId,user,type,beginTime,endTime,pageSize,pageNum);
			List<FinancingTranferRecord> resultList = new ArrayList<>();
			for (int i = 0; i < listDao.size(); i++){
				FinancingTranferRecord financingTranferRecord = new FinancingTranferRecord();
				Map<String,Object> map = listDao.get(i);
				financingTranferRecord.setId(resultStrFormate(map.get("id"),false));
				financingTranferRecord.setType(changeTransferStatus(Integer.parseInt(resultStrFormate(map.get("type"),false))));
				financingTranferRecord.setUserId(resultStrFormate(map.get("userId"),false));
				financingTranferRecord.setUserName(resultStrFormate(map.get("displayName"),false));
				financingTranferRecord.setFromAddress(resultStrFormate(map.get("fromAddress"),false));
				financingTranferRecord.setToAddress(resultStrFormate(map.get("toAddress"),false));
				financingTranferRecord.setCoinType(resultStrFormate(map.get("coinId"),false));
				financingTranferRecord.setAmount(resultStrFormate(map.get("amount"),false));
				financingTranferRecord.setTransferTime(dateStr(resultStrFormate(map.get("modifyTime"),false),"yyyy-MM-dd HH:mm:ss"));
				resultList.add(financingTranferRecord);
			}
			pageInfo.setTotal(num);
			pageInfo.setList(resultList);
		}catch (Exception e){
			log.error("获取交流流水失败");
			throw new Exception("获取交流流水失败");
		}
		return pageInfo;
	}


	public String StrTime(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}

	//字符串转时间格式，DateStr为时间字符串，formate为转化的时间格式  "yyyy-MM-dd HH:mm:ss"
	public Date dateFormate(String DateStr,String formate) throws Exception {
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(formate);
			Date date = formatter.parse(DateStr);
			return date;
		}catch (ParseException e){
			throw new Exception("时间格式解析不正确");
		}
	}

	public String dateStr(String str,String formate) throws Exception {
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(formate);
			return formatter.format(formatter.parse(str));
		}catch (Exception e){
			throw new Exception("时间转字符串，转化格式不正确",e);
		}
	}

	public String bigDecimalFormate(Object object){
		if(StringUtils.isEmpty(object)){
			return "-";
		}else if(object.toString().equals("-")){
			return "0";
		}else{
			return new BigDecimal(object.toString()).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
		}
	}

	//处理为空的字符串
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




	//修改流水状态
	public String changeTransferStatus(Integer type){
		if(1 == type){
			return "提币";
		}else if(2 == type){
			return "充币";
		}else if(3 == type){
			return "投资";
		}else if(4 == type){
			return "代理返点";
		}else if(5 == type){
			return "归还本息";
		}else if(6 == type){
			return "额外收益本人分拥";
		}else if(7 == type){
			return "资金担保机构分拥";
		}else if(8 == type){
			return "基金公司分拥";
		}else if(9 == type){
			return "赎回本金";
		}else if(10 == type){
			return "赎回利息";
		}else if(11 == type){
			return "体验金收益";
		}else if(12 == type){
			return "加息收益";
		}else if(13 == type){
			return "税费";
		}else if(14 == type){
			return "服务费";
		}else {
			return "-";
		}
	}

}

