package com.bcb.service;

import com.bcb.bean.dto.statistics.*;
import com.bcb.domain.dao.StatisticsDao;
import com.bcb.domain.entity.*;
import com.bcb.domain.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class StatisticsService{

	@Autowired
	StatisticsDao statisticDao;

	@Autowired
	WhitelistRepository whitelistRepository;

	@Autowired
	UserAccountInfoRepository userAccountInfoRepository;

	@Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

	@Autowired
    FinancingBaseInfoRepository financingBaseInfoRepository;

	@Autowired
    UserAgentsFinancingBillRepository userAgentsFinancingBillRepository;

	public List<Whitelist> listWhitelist() {
		// TODO Auto-generated method stub
		return whitelistRepository.findAll();
	}

	public StatOverviewDto GainOverView(List<Whitelist> whitelist, String beginTime, String endTime) {
		// TODO Auto-generated method stub
		Map<String, Object> map = statisticDao.GainOverView(whitelist, beginTime, endTime);
		StatOverviewDto statOverviewDto = new StatOverviewDto();
		statOverviewDto.setFinancingCount(Integer.parseInt(map.get("financingCount").toString()));
		statOverviewDto.setTotalInvest(new BigDecimal(map.get("totalInvest").toString()));
		statOverviewDto.setReceivedPrincipal(statOverviewDto.getTotalInvest());
		statOverviewDto.setReceivedPaymentFee(new BigDecimal(map.get("receivedPaymentFee").toString()));
		statOverviewDto.setReceivedServiceFee(new BigDecimal(map.get("receivedServiceFee").toString()));
		statOverviewDto.setReceivedAchievements(new BigDecimal(map.get("receivedAchievements").toString()));
		statOverviewDto.setReceivedTax(new BigDecimal(map.get("receivedTax").toString()));
		statOverviewDto.setReceivedTotal(statOverviewDto.getTotalInvest().add(statOverviewDto.getReceivedPaymentFee())
				.add(statOverviewDto.getReceivedServiceFee()).add(statOverviewDto.getReceivedAchievements())
				.add(statOverviewDto.getReceivedTax()));
		statOverviewDto.setPaidFixedProfit(new BigDecimal(map.get("paidFixedProfit").toString()));
		statOverviewDto.setPaidAdditionalProfit(new BigDecimal(map.get("paidAdditionalProfit").toString()));
		statOverviewDto.setPaidAgentRebeat(new BigDecimal(map.get("paidAgentRebeat").toString()));
		statOverviewDto.setPaidTotalBCB(new BigDecimal(map.get("BcbAmount").toString()));
		statOverviewDto.setPaidTotal(statOverviewDto.getPaidFixedProfit().add(statOverviewDto.getPaidAdditionalProfit())
				.add(statOverviewDto.getPaidAgentRebeat()));
		statOverviewDto.setReceivedWithdrawFee((Double.parseDouble(statisticDao.ReceivedWithdrawFee())*0.1) + "BCB");
		return statOverviewDto;
	}

	public Map<String, String> CountOverView(List<Whitelist> whitelist, String beginTime, String endTime) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> listCoinPayType = statisticDao.GainCoinNum(whitelist, beginTime, endTime);
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < listCoinPayType.size(); i++) {
			Map<String, Object> innerMap = listCoinPayType.get(i);
			map.put(innerMap.get("PaymentType").toString(), innerMap.get("PaymentAmount").toString());
		}
		if (map.get("USDX") == null) {
			map.put("USDX", "0.00");
		}
		if (map.get("ETH") == null) {
			map.put("ETH", "0.00");
		}
		return map;

	}

	public StatDiagramsDto GainToPaidTotals(String beginTime, String endTime,List<Whitelist> whitelist) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> StatPaidCreateOrderList = statisticDao.GainAccountOrder(beginTime, endTime,whitelist);
		StatDiagramsDto statDiagramsDto = new StatDiagramsDto( new ArrayList<BigDecimal>(), new ArrayList<BigDecimal>(), new ArrayList<BigDecimal>(), new ArrayList<String>());
		for (int i = 0; i < StatPaidCreateOrderList.size(); i++) {
			
			Map<String, Object> map = StatPaidCreateOrderList.get(i);
			statDiagramsDto.getTime().add(map.get("Time").toString());
			statDiagramsDto.getReceivedTotals().add(new BigDecimal(map.get("totalInvest").toString()).add(new BigDecimal(map.get("receivedPaymentFee").toString())).add(new BigDecimal(map.get("receivedAchievements").toString())).add(new BigDecimal(map.get("receivedTax").toString())));
			statDiagramsDto.getPaidTotals().add(new BigDecimal(map.get("paidAgentRebeat").toString()).add(new BigDecimal(map.get("paidFixedProfit").toString())).add(new BigDecimal(map.get("paidAdditionalProfit").toString())));
	
		}
		return statDiagramsDto;
	}

	public List<StatReceivedFlowsDto> GainReceivedFlowsDtoList(String beginTime, String endTime,List<Whitelist> whitelist) {
		// TODO Auto-generated method stub

		List<Map<String, Object>> StatPaidCreateOrderList = statisticDao.GainAccountOrder(beginTime, endTime,
				whitelist);

		List<StatReceivedFlowsDto> list = new ArrayList<>();

		for (int i = 0; i < StatPaidCreateOrderList.size(); i++) {
			Map<String, Object> map = StatPaidCreateOrderList.get(i);
			StatReceivedFlowsDto statReceivedFlowsDto = new StatReceivedFlowsDto();
			statReceivedFlowsDto.setReceivedPaymentFee(new BigDecimal(map.get("receivedPaymentFee").toString()));
			statReceivedFlowsDto.setReceivedPrincipal(new BigDecimal(map.get("totalInvest").toString()));
			statReceivedFlowsDto.setReceivedServiceFee(financingServiceRate(map.get("Time").toString() + " 00:00:00", map.get("Time").toString() + " 23:59:59", null));
			statReceivedFlowsDto.setReceivedAchievements(new BigDecimal(map.get("receivedAchievements").toString()));
			statReceivedFlowsDto.setReceivedTax(new BigDecimal(map.get("receivedTax").toString()));
			statReceivedFlowsDto.setCreateTime(map.get("Time").toString());
			statReceivedFlowsDto.setReceivedTotal(statReceivedFlowsDto.getReceivedPrincipal()
					.add(statReceivedFlowsDto.getReceivedPaymentFee()).add(statReceivedFlowsDto.getReceivedServiceFee())
					.add(statReceivedFlowsDto.getReceivedAchievements()).add(statReceivedFlowsDto.getReceivedTax()));
			list.add(statReceivedFlowsDto);
		}

		return list;
	}


	public BigDecimal financingServiceRate(String beginTime, String endTime, String financingUuid){   //按照时间或者项目算服务费
		// todo：获取已结算理财记录的服务费，可以在结算表中直接获取后统计
        return BigDecimal.ZERO;
	}

	public List<StatPaidFlowsDto> GainStatPaidFlowsDtoList(String beginTime, String endTime,List<Whitelist> whitelist) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> StatPaidCreateOrderList = statisticDao.GainAccountOrder(beginTime, endTime,
				whitelist);
		List<StatPaidFlowsDto> list = new ArrayList<>();
		for (int i = 0; i < StatPaidCreateOrderList.size(); i++) {
			Map<String, Object> map = StatPaidCreateOrderList.get(i);
			StatPaidFlowsDto statPaidFlowsDto = new StatPaidFlowsDto();
			statPaidFlowsDto.setPaidAgentRebeat(financingAgentNum(map.get("Time").toString() + " 00:00:00", map.get("Time").toString() + " 23:59:59",null,"USDX"));
			statPaidFlowsDto.setPaidFixedProfit(new BigDecimal(map.get("paidFixedProfit").toString()));
			statPaidFlowsDto.setPaidAdditionalProfit(new BigDecimal(map.get("paidAdditionalProfit").toString()));
			statPaidFlowsDto.setPaidUSDX(statPaidFlowsDto.getPaidAdditionalProfit()
					.add(statPaidFlowsDto.getPaidAgentRebeat()).add(statPaidFlowsDto.getPaidFixedProfit()));
			statPaidFlowsDto.setPaidBCB(new BigDecimal(map.get("BcbAmount").toString()).add(financingAgentNum(map.get("Time").toString() + " 00:00:00", map.get("Time").toString() + " 23:59:59",null,"BCB")));
			statPaidFlowsDto.setCreateTime(map.get("Time").toString());
			list.add(statPaidFlowsDto);
		}
		return list;
	}

    public List<UserFinancingRecord> GainAgentUserId(String beginTime, String endTime) {
        // TODO Auto-generated method stub
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return userFinancingRecordRepository.findByBlockCreateTimeBetween(sdf.parse(beginTime), sdf.parse(endTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            log.error("日期格式不正确:" + beginTime + " " + endTime, e);
        }
        return null;
    }

    public BigDecimal financingAgentNum(String beginTime, String endTime, String financingUuid,String coinType) {

        BigDecimal RateNumTotal = new BigDecimal("0.00");

        List<UserFinancingRecord> list = null;
        if (!StringUtils.isEmpty(beginTime) && !StringUtils.isEmpty(endTime) && StringUtils.isEmpty(financingUuid)) {  //按照时间统计项目返点数量
            list = GainAgentUserId(beginTime, endTime);
        }else if(StringUtils.isEmpty(beginTime) && StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(financingUuid)){   //按照项目uuid统计代理收益
            list = userFinancingRecordRepository.findByFinancingUuid(financingUuid);
        }else{
            return RateNumTotal;
        }

        Map<Integer, BigDecimal> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i).getUserId()) == null) {
                map.put(list.get(i).getUserId(), new BigDecimal("0.00"));
            }
        }
        Map<Integer, BigDecimal> agentMap = GainAgentRateTotal(map);
        for (int i = 0; i < list.size(); i++) {
            if (agentMap.get(list.get(i).getUserId()) == null) {
                continue;
            }
            if(coinType.equals("BCB")){
                RateNumTotal = RateNumTotal.add(agentMap.get(list.get(i).getUserId()).multiply(list.get(i).getBcbAmount()).multiply(new BigDecimal("0.01")));
            }else if(coinType.equals("USDX")){
                RateNumTotal = RateNumTotal.add(agentMap.get(list.get(i).getUserId()).multiply(list.get(i).getUsdxAmount()).multiply(new BigDecimal("0.01")));
            }

        }

        return RateNumTotal.setScale(3, BigDecimal.ROUND_HALF_UP);

    }

    public Map<Integer, BigDecimal> GainAgentRateTotal(Map<Integer, BigDecimal> map) {
        // TODO Auto-generated method stub
        Map<Integer, BigDecimal> resultMap = new HashMap<>();
        try{
            for(Map.Entry<Integer, BigDecimal> entry : map.entrySet()){
                UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(entry.getKey());
                if(userAccountInfo.getFinancingScale().compareTo(BigDecimal.ZERO) == 0){   //为普通用户
                    continue;
                }
                if(userAccountInfo.getParentId() == null){  //为一级代理用户
                    resultMap.put(entry.getKey(), userAccountInfo.getFinancingScale());
                    continue;
                }   //为普通代理用户
                Integer UserId = userAccountInfo.getParentId();
                while(true){
                    UserAccountInfo parentAccountInfo = userAccountInfoRepository.findByUserId(UserId);
                    if (parentAccountInfo == null) {
                        break;
                    }
                    if(parentAccountInfo.getParentId() == null){
                        resultMap.put(entry.getKey(), parentAccountInfo.getFinancingScale());
                        break;
                    }
                    resultMap.put(entry.getKey(), parentAccountInfo.getFinancingScale());
                    UserId = parentAccountInfo.getParentId();
                }
            }
        }catch(Exception e){
            log.error("用户投资代理返点比例获取失败",e);
        }
        return resultMap;
    }

	public List<StatFinancingProjectDto> GainStatFinancingProjectDto(String beginTime, String endTime,
			String projectState, String projectUuid, Integer start, Integer length, List<Whitelist> whitelist) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = statisticDao.GainFinancingProject(beginTime, endTime, projectState,
				projectUuid, start, length, whitelist);
		List<StatFinancingProjectDto> resultList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			StatFinancingProjectDto statFinancingProjectDto = new StatFinancingProjectDto();
			statFinancingProjectDto.setFinancingUuid(list.get(i).get("SerialNum").toString());
			statFinancingProjectDto.setTitle(list.get(i).get("Title").toString());
			statFinancingProjectDto.setCreateTime(list.get(i).get("CreateTime").toString());
			statFinancingProjectDto.setStatus(list.get(i).get("Status").toString());
			statFinancingProjectDto.setAmount(new BigDecimal(list.get(i).get("Amount").toString()));
			statFinancingProjectDto.setSchedules(list.get(i).get("schedules").toString());
			statFinancingProjectDto.setReceived(new BigDecimal(list.get(i).get("received").toString()));
			statFinancingProjectDto.setPaid((new BigDecimal(list.get(i).get("paid").toString())));
			resultList.add(statFinancingProjectDto);
		}
		return resultList;
	}

	public StatFinancingProjectDetailDto GainStatFinancingProjectDetailDto(String projectUuid,List<Whitelist> whitelist) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> map = statisticDao.GainFinancingProjectDetail(projectUuid, whitelist);
		StatFinancingProjectDetailDto statFinancingProjectDetailDto = new StatFinancingProjectDetailDto();
		statFinancingProjectDetailDto.setTitle(map.get("Title").toString());
		statFinancingProjectDetailDto.setCreateTime(dateStr( map.get("CreateTime").toString(),"yyyy-MM-dd HH:mm:ss"));
		statFinancingProjectDetailDto.setStatus(map.get("Status").toString());
		statFinancingProjectDetailDto.setAmount(new BigDecimal(map.get("Amount").toString()));
		statFinancingProjectDetailDto.setFinancingUuid(map.get("FinancingUuid").toString());
		statFinancingProjectDetailDto.setCoinAmount(new BigDecimal(map.get("CoinAmount").toString()));
		statFinancingProjectDetailDto.setSchedules(map.get("schedules").toString());
		statFinancingProjectDetailDto.setHumanNum(map.get("humanNum").toString());
		statFinancingProjectDetailDto.setTotalInvest(new BigDecimal(map.get("totalInvest").toString()));

		statFinancingProjectDetailDto.setReceived(new BigDecimal(map.get("received").toString()));
		statFinancingProjectDetailDto.setPaid(new BigDecimal(map.get("paid").toString()));
		statFinancingProjectDetailDto.setPaidBCB(new BigDecimal(map.get("paidBCB").toString()));

		statFinancingProjectDetailDto.setReceivedPaymentFee(new BigDecimal(map.get("receivedPaymentFee").toString()));
		statFinancingProjectDetailDto.setReceivedServiceFee(new BigDecimal(map.get("receivedServiceFee").toString()));
		statFinancingProjectDetailDto.setReceivedAchievements(new BigDecimal(map.get("receivedAchievements").toString()));
		statFinancingProjectDetailDto.setReceivedTax(new BigDecimal(map.get("receivedTax").toString()));
		statFinancingProjectDetailDto.setReceivedTotal(new BigDecimal(map.get("totalInvest").toString()));

		statFinancingProjectDetailDto.setPaidPrincipal(new BigDecimal(map.get("paidPrincipal").toString()));
		statFinancingProjectDetailDto.setPaidAgentRebeat(new BigDecimal(map.get("paidAgentRebeat").toString()));
		statFinancingProjectDetailDto.setPaidFixedProfit(new BigDecimal(map.get("paidFixedProfit").toString()));
		statFinancingProjectDetailDto.setPaidAdditionalProfit(new BigDecimal(map.get("paidAdditionalProfit").toString()));
		statFinancingProjectDetailDto.setPaidGeAmount(new BigDecimal(map.get("paidGeAmount").toString()));
		statFinancingProjectDetailDto.setPaidIcAmount(new BigDecimal(map.get("paidIcAmount").toString()));

		return statFinancingProjectDetailDto;
	}

	public List<Map<String, Object>> GainUserFinancingRelation(String financingUuid, Integer start, Integer length,List<Whitelist> whitelist) {

		return statisticDao.GainUserFinancingRelation(financingUuid, start, length, whitelist);
	}

	public List<Map<String,Object>> GainUserFinancingRelationAll(String financingUuid,List<Whitelist> whitelists){
	    return statisticDao.GainUserFinancingRelationAll(financingUuid,whitelists);
    }

	public Integer GainCountProjectNum(String beginTime, String endTime, String projectState, String projectUuid) {

		return statisticDao.GainCountProjectNum(beginTime, endTime, projectState, projectUuid);
	}

	public Integer GainUserFinancingRelationNumByUuid(String financingUuid,List<Whitelist> whitelist) {
		// TODO Auto-generated method stub
		return statisticDao.GainUserFinancingRelationNumByUuid(financingUuid,whitelist);
	}

	public List<Map<String, Object>> GainFinancingUser(String user, Integer iDisplayStart, Integer iDisplayLength,String iSortCol_0,String sSortDir_0) {
		// TODO Auto-generated method stub
		return statisticDao.GainFinancingUserList(iDisplayStart, iDisplayLength, user,iSortCol_0,sSortDir_0);
	}

	public Integer GainFinancingUserNum(String user) {
		// TODO Auto-generated method stub
		return statisticDao.GainFinancingUserListNum(user);
	}

	public StatUserDto GainFinancingUserMessage(String userid,Integer step) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> objList = statisticDao.GainStatUserDto(userid);
		StatUserDto statUserDto = new StatUserDto();
		UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(Integer.parseInt(userid));
		List<String> list = new ArrayList<>();
		statUserDto.setReturnPointColumn(userAccountInfo.getFinancingScale().setScale(2,BigDecimal.ROUND_HALF_UP) + "%");
		statUserDto.setUserId(userAccountInfo.getUserId().toString());
		statUserDto.setDisPlayName(userAccountInfo.getDisplayName());
		if(!CollectionUtils.isEmpty(objList)){
            for (int i = 0; i < objList.size(); i++){
                if(!objList.get(i).get("Token").equals("-")){
                    String strAddressF = objList.get(i).get("Token").toString().substring(0,2);
                    if(strAddressF.equals("0x")){
                        list.add("ETH:" + objList.get(i).get("Token").toString());
                    }else if(strAddressF.equals("bc")){
                        list.add("USDX:" + objList.get(i).get("Token").toString());
                    }
                }else {
                    list.add("-");
                }
            }
        }
		statUserDto.setAddress(list);
		statUserDto.setGrade(step + "级");
		return statUserDto;
	}

	public List<Map<String, Object>> GainFinancingUserInvestmentIncome(String userid, Integer start, Integer length,
			String beginTime, String endTime) {
		// TODO Auto-generated method stub
		return statisticDao.GainFinancingUserInvestmentIncomeList(beginTime, endTime, start, length, userid);
	}

	public Integer GainFinancingUserInvestmentIncomeNum(String userid, String beginTime, String endTime) {
		// TODO Auto-generated method stub
		return statisticDao.GainFinancingUserInvestmentIncomeListNum(beginTime, endTime, userid);
	}

	public List<Map<String, Object>> GainFinancingUserAgent(String beginTime, String endTime, Integer start,
															Integer length, String UserId) {

		return statisticDao.GainFinancingUserAgent(beginTime, endTime,  start,
				 length, UserId);
	}

	public Integer GainFinancingUserAgentNum(String userid, String beginTime, String endTime) {
		// TODO Auto-generated method stub
		return statisticDao.GainFinancingUserAgentNum(beginTime, endTime, userid);
	}


	public List<Map<String, Object>> FinancingUserJunior(String userid, String Junior, Integer start, Integer length,
			String iSortCol_0, String sSortDir_0) {
		// TODO Auto-generated method stub
		return statisticDao.GainFinancingUserJunior(userid, Junior, start, length, iSortCol_0, sSortDir_0);
	}

	public Integer GainFinancingUserJuniorNum(String userid, String Junior) {
		// TODO Auto-generated method stub
		return statisticDao.GainGainFinancingUserJuniorNum(userid, Junior);
	}

	public Integer GainJuniorAgentNum(String UserId) {
		Integer jaNum = new Integer(0);
		List<String> JuniorAgentNumList = statisticDao.GainJuniorAgentNumList(UserId);
		jaNum += JuniorAgentNumList.size();
		if (JuniorAgentNumList.size() == 0) {
			return jaNum;
		} else {
			for (int i = 0; i < JuniorAgentNumList.size(); i++) {
				jaNum += GainJuniorAgentNum(JuniorAgentNumList.get(i));
			}
		}
		return jaNum;
	}

	public Integer GainAgentStep(String UserId) {
		Integer Num = new Integer(0);
		String ParentId = statisticDao.GainParentId(UserId);
		if (ParentId == null) {
			return Num;
		} else {
			Num += GainAgentStep(ParentId);
			Num++;
		}
		return Num;
	}

    public List<FinancingBaseInfo> GainFinancingBaseInfoList() {
        // TODO Auto-generated method stub
        return financingBaseInfoRepository.findByStatusBetween(1,4);
    }

    public List<UserFinancingRecord> GainFinancingTranferTimeManager(String Time) {
        // TODO Auto-generated method stub
        String beginTime = Time + " 00:00:00";
        String endTime = Time + " 23:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<UserFinancingRecord> list = null;
        try{
            list =  userFinancingRecordRepository.findByRecordCreateTimeBetween(sdf.parse(beginTime),sdf.parse(endTime));
        }catch(Exception e){
            log.error("数据解析格式错误：",e);
        }
        return list;
    }

    public Map<String, String> GainFinancingNumByUserId(Integer userId) {
        // TODO Auto-generated method stub
        List<UserFinancingRecord> list = userFinancingRecordRepository.findByUserId(userId);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.isEmpty(map.get(list.get(i).getFinancingUuid()))) {
                map.put(list.get(i).getFinancingUuid(), "1");
            } else {
                map.put(list.get(i).getFinancingUuid(),
                        (Integer.parseInt(map.get(list.get(i).getFinancingUuid())) + 1) + "");
            }
        }
        return map;
    }

	public String dateStr(String str,String formate) throws Exception {
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(formate);
			return formatter.format(formatter.parse(str));
		}catch (Exception e){
			throw new Exception("时间转字符串，转化格式不正确",e);
		}
	}
}
