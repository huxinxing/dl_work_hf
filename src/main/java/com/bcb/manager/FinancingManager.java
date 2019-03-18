package com.bcb.manager;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.bcb.config.RedisClient;
import com.bcb.domain.entity.*;
import com.bcb.domain.repository.FinancingCnInfoRepository;
import com.bcb.domain.repository.FinancingEnInfoRepository;
import com.bcb.domain.repository.SystemConfigurationRepository;
import com.bcb.domain.repository.SzgDictInfoRepository;
import com.bcb.service.CommonService;
import com.bcb.service.FinancingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.FinancingBaseInfoVO;
import com.bcb.bean.dto.statistics.FinancingLanagerMessageDto;
import com.bcb.bean.dto.statistics.LanguageDto;
import com.bcb.util.CommonUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class FinancingManager {

	@Autowired
	private CommonService commonService;
	@Autowired
	private FinancingService financingService;

	@Autowired
	RedisClient redisClient;

	@Autowired
	SzgDictInfoRepository szgDictInfoRepository;

	@Autowired
	FinancingCnInfoRepository financingCnInfoRepository;

	@Autowired
	FinancingEnInfoRepository financingEnInfoRepository;

	@Autowired
	SystemConfigurationRepository systemConfigurationRepository;

	public ResponseResult financingProjectAddManager(FinancingBaseInfo financingBaseInfo, List<FinancingWalletInfo> financingWalletInfoList, SystemLoginAccount systemLoginAccount) {
		try{
			if(CollectionUtils.isEmpty(financingWalletInfoList)){
				return new ResponseResult(WorldValue.ERROR, "新增项目失败: 钱包地址未加载",ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
			}
			String BCB_USDX = redisClient.get("BCB_USDX");
			int financingNum = commonService.findFinancingBaseInfoList().size();
			SimpleDateFormat formatter = new SimpleDateFormat("YYMMHHmm");
			String financingUuid = UUID.randomUUID().toString().replaceAll("-", "");
			BigDecimal currentBcb2UsdxPrice = new BigDecimal(BCB_USDX);
			if(StringUtils.isEmpty(financingBaseInfo.getTag()))
				financingBaseInfo.setTag("short_term");
			financingBaseInfo.setTag(szgDictInfoRepository.findOneByDicTypeIdAndDicLangFlagAndDicKey(4,"CN",financingBaseInfo.getTag()).getDicValue());
			for (int i = 0; i < financingWalletInfoList.size(); i++){
				financingWalletInfoList.get(i).setFinancingUuid(financingUuid);
			}
			financingBaseInfo.setFinancingUuid(financingUuid);
			financingBaseInfo.setCreateBcb2Usdx(currentBcb2UsdxPrice);
			financingBaseInfo.setSerialNum(formatter.format(new Date()) + (financingNum + 1));
			financingBaseInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
			financingBaseInfo.setLang("cn");;

			financingBaseInfo.setStatus(0);
			int timeLong = 0;
			if(financingBaseInfo.getFreezeUnit().equals("月"))
				timeLong = 12/financingBaseInfo.getFreezeNumber();
			if(financingBaseInfo.getFreezeUnit().equals("天"))
				timeLong = 365/financingBaseInfo.getFreezeNumber();
			if(financingBaseInfo.getFreezeUnit().equals("年"))
				timeLong = financingBaseInfo.getFreezeNumber();
			BigDecimal ServiceRate = new BigDecimal(systemConfigurationRepository.findByName("ServiceRate").getValue());
			financingBaseInfo.setTotalRate((financingBaseInfo.getAnnualRate().subtract(ServiceRate)).multiply(new BigDecimal(timeLong)));
			commonService.saveOrUpdateFinancingProject(financingBaseInfo);
			for (int i = 0; i < financingWalletInfoList.size(); i++){
				commonService.saveOrUpdateWalletMessage(financingWalletInfoList.get(i));
			}
			FinancingCnInfo financingCnInfo = new FinancingCnInfo();
			financingCnInfo.setFinancingUuid(financingUuid);
			financingCnInfo.setTitle(financingBaseInfo.getTitle());
			financingCnInfo.setAttentions(financingBaseInfo.getAttentions());
			financingCnInfo.setTag(financingBaseInfo.getTag());
			commonService.saveOrUpdatecFinancingCnInfo(financingCnInfo);
			SystemLogOperation systemLogOperation = new SystemLogOperation();
			systemLogOperation.setCreateTime(new Date());
			systemLogOperation.setParameterContext("项目管理:新增项目");
			if(systemLoginAccount != null && systemLoginAccount.getLoginName() != null){
				systemLogOperation.setOperatorName(systemLoginAccount.getLoginName());
			}else{
				systemLogOperation.setOperatorName("");
			}
			systemLogOperation.setActionType("更新");
			systemLogOperation.setCommentDescribe("项目管理");
			commonService.saveOrUpdateSystemLogOperation(systemLogOperation);
			return new ResponseResult("ok", "200", "新增项目成功");
			
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseResult(WorldValue.ERROR, "新增项目失败: " + e.getMessage() ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
		}
		

	}

	public  List<Map<String,String>> queryTagListManager() throws Exception {
		List<Map<String,String>> resultList = new ArrayList<>();
		try{
			List<SzgDictInfo> szgDictInfoList = szgDictInfoRepository.findByDicTypeIdAndDicLangFlag(4,"CN");
			for(int i = 0; i < szgDictInfoList.size(); i++){
				Map<String,String> map = new HashMap<>();
				map.put("key",szgDictInfoList.get(i).getDicKey());
				map.put("value",szgDictInfoList.get(i).getDicValue());
				resultList.add(map);
			}
			return resultList;
		}catch (Exception e){
			throw new Exception("体验金保存失败",e);
		}
	}

	@Transactional
	public ResponseResult updateFinancingProjectManager(FinancingBaseInfo financingBaseInfo, List<FinancingWalletInfo> financingWalletInfoList, List<Object> list, SystemLoginAccount systemLoginAccount) {

			FinancingBaseInfo financingBaseInfoUpdate = commonService
					.queryFinancingBaseInfoByUuid(financingBaseInfo.getFinancingUuid());
			List<FinancingWalletInfo> financingWalletInfoUpdatelist = commonService
					.queryFinancingWalletInfo(financingBaseInfo.getFinancingUuid());
			if(financingBaseInfoUpdate == null || financingWalletInfoUpdatelist == null){
				return new ResponseResult(WorldValue.ERROR, "项目不存在或钱包地址不存在,请确认项目Uuid",ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
			}

			if (financingBaseInfo.getTitle() != null)
				financingBaseInfoUpdate.setTitle(financingBaseInfo.getTitle());
			if (financingBaseInfo.getAttentions() != null)
				financingBaseInfoUpdate.setAttentions(financingBaseInfo.getAttentions());
			if (financingBaseInfo.getCoinType() != null)
				financingBaseInfoUpdate.setCoinType(financingBaseInfo.getCoinType());
			if (financingBaseInfo.getAmount() != null)
				financingBaseInfoUpdate.setAmount(financingBaseInfo.getAmount());
			if (!StringUtils.isEmpty(financingBaseInfo.getCoinLimit())){
				financingBaseInfoUpdate.setCoinLimit(financingBaseInfo.getCoinLimit());
			}else {
				financingBaseInfoUpdate.setCoinLimit(financingBaseInfoUpdate.getAmount());
			}
			if (!StringUtils.isEmpty(financingBaseInfo.getCoinMinLimit())) {
				financingBaseInfoUpdate.setCoinMinLimit(financingBaseInfo.getCoinMinLimit());
			}else {
				financingBaseInfoUpdate.setCoinMinLimit(BigDecimal.valueOf(0));
			}
			if (financingBaseInfo.getFreezeUnit() != null)
				financingBaseInfoUpdate.setFreezeUnit(financingBaseInfo.getFreezeUnit());
			if (financingBaseInfo.getFreezeNumber() != null)
				financingBaseInfoUpdate.setFreezeNumber(financingBaseInfo.getFreezeNumber());
			if (financingBaseInfo.getTotalRate() != null)
				financingBaseInfoUpdate.setTotalRate(financingBaseInfo.getTotalRate());
			if (financingBaseInfo.getAnnualRate() != null){
				financingBaseInfoUpdate.setAnnualRate(financingBaseInfo.getAnnualRate());
				int timeLong = 0;
				if(financingBaseInfo.getFreezeUnit().equals("月"))
					timeLong = 12/financingBaseInfo.getFreezeNumber();
				if(financingBaseInfo.getFreezeUnit().equals("天"))
					timeLong = 365/financingBaseInfo.getFreezeNumber();
				if(financingBaseInfo.getFreezeUnit().equals("年"))
					timeLong = financingBaseInfo.getFreezeNumber();
				BigDecimal ServiceRate = new BigDecimal(systemConfigurationRepository.findByName("ServiceRate").getValue());
				financingBaseInfoUpdate.setTotalRate((financingBaseInfo.getAnnualRate().subtract(ServiceRate)).multiply(new BigDecimal(timeLong)));
			}
			if (financingBaseInfo.getFoundationRate() != null)
				financingBaseInfoUpdate.setFoundationRate(financingBaseInfo.getFoundationRate());
			if (financingBaseInfo.getSubscriptionFeeRate() != null)
				financingBaseInfoUpdate.setSubscriptionFeeRate(financingBaseInfo.getSubscriptionFeeRate());
			if (financingBaseInfo.getDiscountRate() != null)
				financingBaseInfoUpdate.setDiscountRate(financingBaseInfo.getDiscountRate());
			if (financingBaseInfo.getPaymentType() != null)
				financingBaseInfoUpdate.setPaymentType(financingBaseInfo.getPaymentType());
			if (financingBaseInfo.getStatus() != null)
				financingBaseInfoUpdate.setStatus(financingBaseInfo.getStatus());
			if (financingBaseInfo.getTag() != null){
				financingBaseInfoUpdate.setTag(szgDictInfoRepository.findOneByDicTypeIdAndDicLangFlagAndDicKey(4,"CN",financingBaseInfo.getTag()).getDicValue());
			}


			if (!CollectionUtils.isEmpty(financingWalletInfoList)) {
				for (FinancingWalletInfo financingWalletInfo : financingWalletInfoList){
					for (FinancingWalletInfo financingWalletInfoUpdate : financingWalletInfoUpdatelist){
						if(financingWalletInfo.getWalletType() == financingWalletInfoUpdate.getWalletType()){
							financingWalletInfoUpdate.setCreateTime(financingWalletInfo.getCreateTime());
							financingWalletInfoUpdate.setWalletType(financingWalletInfo.getWalletType());
							financingWalletInfoUpdate.setWalletLevel(financingWalletInfo.getWalletLevel());
							financingWalletInfoUpdate.setStatus(financingWalletInfo.getStatus());
							financingWalletInfoUpdate.setFileByte(financingWalletInfo.getFileByte());
							commonService.saveOrUpdateWalletMessage(financingWalletInfoUpdate);
						}else {
							financingWalletInfo.setFinancingUuid(financingBaseInfo.getFinancingUuid());
							commonService.saveOrUpdateWalletMessage(financingWalletInfo);
						}
					}
				}
			}

			String langStr = "[";
			
			for(int i = 0; i < list.size(); i++){
				JSONObject jsonObjects = new JSONObject().parseObject(list.get(i).toString());
				Map<String, Object> innerMap = jsonObjects;
				String lan = innerMap.get("lan").toString();
				String title = innerMap.get("title").toString();
				String attention = innerMap.get("attention").toString();
				langStr += lan + "],[";
				if(lan.equals("中文")){
					FinancingCnInfo financingCnInfo = new FinancingCnInfo();
					financingCnInfo.setFinancingUuid(financingBaseInfo.getFinancingUuid());
					financingCnInfo.setTitle(title);
					financingCnInfo.setAttentions(attention);
					financingCnInfo.setTag(szgDictInfoRepository.findOneByDicTypeIdAndDicLangFlagAndDicKey(4,"CN",financingBaseInfo.getTag()).getDicValue());
					financingBaseInfoUpdate.setTitle(title);
					financingBaseInfoUpdate.setAttentions(attention);
					commonService.saveOrUpdatecFinancingCnInfo(financingCnInfo);
				}else if(lan.equals("英文")){
					FinancingEnInfo financingEnInfo = new FinancingEnInfo();
					financingEnInfo.setFinancingUuid(financingBaseInfo.getFinancingUuid());
					financingEnInfo.setTitle(title);
					financingEnInfo.setTag(szgDictInfoRepository.findOneByDicTypeIdAndDicLangFlagAndDicKey(4,"EN",financingBaseInfo.getTag()).getDicValue());
					financingEnInfo.setAttentions(attention);
					commonService.saveOrUpdatecFinancingEnInfo(financingEnInfo);
				}
			}

			if(!langStr.contains("中文")){
				FinancingCnInfo financingCnInfo = new FinancingCnInfo();
				financingCnInfo.setFinancingUuid(financingBaseInfo.getFinancingUuid());
				financingCnInfoRepository.delete(financingCnInfo);
			}

			if(!langStr.contains("英文")){
				FinancingEnInfo financingEnInfo = new FinancingEnInfo();
				financingEnInfo.setFinancingUuid(financingBaseInfo.getFinancingUuid());
				financingEnInfoRepository.delete(financingEnInfo);
			}

			commonService.saveOrUpdateFinancingProject(financingBaseInfoUpdate);
			SystemLogOperation systemLogOperation = new SystemLogOperation();
			systemLogOperation.setCreateTime(new Date());
			if(financingBaseInfo.getStatus() != null){
				if(financingBaseInfo.getStatus().equals("-1")){
					systemLogOperation.setParameterContext("项目管理:项目暂停");
				}else if(financingBaseInfo.getStatus().equals("1")){
					systemLogOperation.setParameterContext("项目管理:项目募集中");
				}else if(financingBaseInfo.getStatus().equals("2")){
					systemLogOperation.setParameterContext("项目管理:项目结束");
				}else if(financingBaseInfo.getStatus().equals("3")){
					systemLogOperation.setParameterContext("项目管理:返币结束");
				}
			}else{
				systemLogOperation.setParameterContext("项目管理:项目修改");
			}
			if(systemLoginAccount != null && systemLoginAccount.getLoginName() != null){
				systemLogOperation.setOperatorName(systemLoginAccount.getLoginName());
			}else{
				systemLogOperation.setOperatorName("");
			}
			systemLogOperation.setActionType("更新");
			systemLogOperation.setCommentDescribe("项目管理");
			commonService.saveOrUpdateSystemLogOperation(systemLogOperation);
			return new ResponseResult("ok", "200", "项目更新成功");

		
	}

	public ResponseResult GainQueryFinancingInfoByIdManager(String financingUuid) {

		try{
			FinancingBaseInfo financingBaseInfo = commonService.queryFinancingBaseInfoByUuid(financingUuid);
			List<FinancingWalletInfo> financingWalletInfolist = commonService.queryFinancingWalletInfo(financingUuid);
			FinancingCnInfo financingCnInfo = commonService.queryFinancingCnInfoByUuid(financingUuid);
			FinancingEnInfo financingEnInfo = commonService.queryFinancingEnInfoByUuid(financingUuid);

			
			FinancingBaseInfoVO financingBaseInfoVO = new FinancingBaseInfoVO();

			List<FinancingLanagerMessageDto> list = new ArrayList<>();
			if(financingCnInfo != null){
				FinancingLanagerMessageDto lanagerMessageDtoCN = new FinancingLanagerMessageDto("中文",
						financingCnInfo.getTitle(), financingCnInfo.getAttentions());
				list.add(lanagerMessageDtoCN);
			}
			
			if(financingEnInfo != null){
				FinancingLanagerMessageDto lanagerMessageDtoEN = new FinancingLanagerMessageDto("英文",
						financingEnInfo.getTitle(), financingEnInfo.getAttentions());
				list.add(lanagerMessageDtoEN);
			}
			
			financingBaseInfoVO.setFinancingUuid(financingUuid);
			financingBaseInfoVO.setCoinType(financingBaseInfo.getCoinType());
			financingBaseInfoVO.setAmount(financingBaseInfo.getAmount());
			financingBaseInfoVO.setCoinMinLimit(financingBaseInfo.getCoinMinLimit());
			financingBaseInfoVO.setCoinLimit(financingBaseInfo.getCoinLimit());
			Map<String,String> map = new HashMap<>();
			for(int i = 0; i < financingWalletInfolist.size(); i++){
				if (financingWalletInfolist.get(i).getWalletType() == 1){
					map.put("BCB",financingWalletInfolist.get(i).getToken());
				}else if(financingWalletInfolist.get(i).getWalletType() == 2){
					map.put("USDX",financingWalletInfolist.get(i).getToken());
				}else if(financingWalletInfolist.get(i).getWalletType() == 3){
					map.put("ETH",financingWalletInfolist.get(i).getToken());
				}else if(financingWalletInfolist.get(i).getWalletType() == 4){
					map.put("USDX_BCB",financingWalletInfolist.get(i).getToken());
				}
			}
			financingBaseInfoVO.setToken(map);
			financingBaseInfoVO.setFreezeNumber(financingBaseInfo.getFreezeNumber());
			financingBaseInfoVO.setFreezeUnit(financingBaseInfo.getFreezeUnit());
			financingBaseInfoVO.setAnnualRate(financingBaseInfo.getAnnualRate());
			financingBaseInfoVO.setTotalRate(financingBaseInfo.getTotalRate());
			financingBaseInfoVO.setFoundationRate(financingBaseInfo.getFoundationRate());
			financingBaseInfoVO.setSubscriptionFeeRate(financingBaseInfo.getSubscriptionFeeRate());
			financingBaseInfoVO.setDiscountRate(financingBaseInfo.getDiscountRate());
			financingBaseInfoVO.setPaymentType(financingBaseInfo.getPaymentType());
			financingBaseInfoVO.setTag(szgDictInfoRepository.findOneByDicTypeIdAndDicValue(4,financingBaseInfo.getTag()).getDicKey());
			financingBaseInfoVO.setCreateTime(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(financingBaseInfo.getCreateTime()));
			financingBaseInfoVO.setStatus(financingBaseInfo.getStatus());
			financingBaseInfoVO.setFinancingLanagerMessageList(list);

			return new ResponseResult("ok", "200", financingBaseInfoVO, "请求成功");
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseResult(WorldValue.ERROR, "查询详情_项目失败: " + e.getMessage() ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
		}
		

	}

	public ResponseResult GainEditLanagerManager(String financingUuid) {
		try{
			LanguageDto languageDto = new LanguageDto();
			List<SzgDictInfo> szgDicInfolist = commonService.queryLanguageEnumByDicTypeId(3);
			List<Map<String, String>> list = new ArrayList<>();
			for (int i = 0; i < szgDicInfolist.size(); i++) {
				Map<String, String> map = new HashMap<>();
				map.put("key", szgDicInfolist.get(i).getDicKey());
				map.put("value", szgDicInfolist.get(i).getDicValue());
				list.add(map);
			}
			
			List<FinancingLanagerMessageDto> lists = new ArrayList<>();
			FinancingCnInfo financingCnInfo = commonService.queryFinancingCnInfoByUuid(financingUuid);
			FinancingEnInfo financingEnInfo = commonService.queryFinancingEnInfoByUuid(financingUuid);
			
			if(financingCnInfo != null){
				FinancingLanagerMessageDto lanagerMessageDtoCN = new FinancingLanagerMessageDto("中文",
						financingCnInfo.getTitle(), financingCnInfo.getAttentions());
				lists.add(lanagerMessageDtoCN);
			}
			
			if(financingEnInfo != null){
				FinancingLanagerMessageDto lanagerMessageDtoEN = new FinancingLanagerMessageDto("英文",
						financingEnInfo.getTitle(), financingEnInfo.getAttentions());
				lists.add(lanagerMessageDtoEN);
			}
			
			languageDto.setLanguageEnum(list);
			languageDto.setFinancingLanagerMessageDto(lists);

			return new ResponseResult("ok", "200", languageDto, "请求成功");
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseResult(WorldValue.ERROR, "查询详情_语言失败: " + e.getMessage() ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
		}
	}

	@SuppressWarnings({ "unchecked", "static-access", "unused" })
	@Transactional
	public ResponseResult SaveFinancingLanagerManager(Map<String, Object> paramMap, SystemLoginAccount systemLoginAccount) {

			String financingUuid = paramMap.get("financingUuid").toString();

            JSONArray list = JSONArray.parseArray(paramMap.get("list").toString());


            String langStr = "[";
			for(int i = 0; i < list.size(); i++){

				JSONObject jsonObjects = list.getJSONObject(i);
				Map<String, Object> innerMap = jsonObjects;
				String lan = innerMap.get("lan").toString();
				String title = innerMap.get("title").toString();
				String attention = innerMap.get("attention").toString();
				langStr += lan + "],[";
				FinancingBaseInfo financingBaseInfo = commonService.queryFinancingBaseInfoByUuid(financingUuid);
				String tagKey = szgDictInfoRepository.findOneByDicTypeIdAndDicValue(4,financingBaseInfo.getTag()).getDicKey();
				if(lan.equals("中文")){
					FinancingCnInfo financingCnInfo = new FinancingCnInfo();
					financingCnInfo.setFinancingUuid(financingUuid);
					financingCnInfo.setTitle(title);
					financingCnInfo.setAttentions(attention);
					financingCnInfo.setTag(szgDictInfoRepository.findOneByDicTypeIdAndDicLangFlagAndDicKey(4,"CN",tagKey).getDicValue());
					if(financingBaseInfo == null){
						return new ResponseResult(WorldValue.ERROR, "项目不存在",ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
					}
					if(financingBaseInfo.getLang() == null){
						financingBaseInfo.setLang(financingBaseInfo.getLang() + "cn");
					}else if(!financingBaseInfo.getLang().contains("cn")){
						financingBaseInfo.setLang(financingBaseInfo.getLang() + ",cn");
					}
					

					commonService.saveOrUpdatecFinancingCnInfo(financingCnInfo);
					commonService.saveOrUpdateFinancingProject(financingBaseInfo);
				}else if(lan.equals("英文")){
					FinancingEnInfo financingEnInfo = new FinancingEnInfo();
					financingEnInfo.setFinancingUuid(financingUuid);
					financingEnInfo.setTitle(title);
					financingEnInfo.setAttentions(attention);
					financingEnInfo.setTag(szgDictInfoRepository.findOneByDicTypeIdAndDicLangFlagAndDicKey(4,"EN",tagKey).getDicValue());
					if(financingBaseInfo == null){
						return new ResponseResult(WorldValue.ERROR, "项目不存在",ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
					}
					if(financingBaseInfo.getLang() == null){
						financingBaseInfo.setLang(financingBaseInfo.getLang() + "en");
					}else if(!financingBaseInfo.getLang().contains("en")){
						financingBaseInfo.setLang(financingBaseInfo.getLang() + ",en");
					}
					

					commonService.saveOrUpdatecFinancingEnInfo(financingEnInfo);
					commonService.saveOrUpdateFinancingProject(financingBaseInfo);
				}
			}

			if(!langStr.contains("中文")){
				FinancingCnInfo financingCnInfo = new FinancingCnInfo();
				financingCnInfo.setFinancingUuid(financingUuid);
				financingCnInfoRepository.delete(financingCnInfo);
			}

			if(!langStr.contains("英文")){
				FinancingEnInfo financingEnInfo = new FinancingEnInfo();
				financingEnInfo.setFinancingUuid(financingUuid);
				financingEnInfoRepository.delete(financingEnInfo);
			}

			SystemLogOperation systemLogOperation = new SystemLogOperation();
			systemLogOperation.setCreateTime(new Date());
			systemLogOperation.setParameterContext("项目管理:语言编辑");
			if(systemLoginAccount != null && systemLoginAccount.getLoginName() != null){
				systemLogOperation.setOperatorName(systemLoginAccount.getLoginName());
			}else{
				systemLogOperation.setOperatorName("");
			}
			systemLogOperation.setActionType("更新");
			systemLogOperation.setCommentDescribe("项目管理");
			commonService.saveOrUpdateSystemLogOperation(systemLogOperation);
			return new ResponseResult("ok", "200", "多语言保存成功");
		
	}

}
