package com.bcb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.dto.UserInvestRateDto;
import com.bcb.domain.entity.*;
import com.bcb.manager.FinancingManager;
import com.bcb.service.FinancingService;
import com.bcb.util.BCBAddressCheck;
import com.bcb.util.IBanUtil;
import com.bcb.util.eth.WalletFileParser;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.bcb.annotation.LoginRequired;
import com.bcb.bean.JSONParam;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;

@Slf4j
@RestController
@RequestMapping(value = "/admin/financing")
public class FinancingController {

	@Autowired
	private FinancingService financingService;
	@Autowired
	private FinancingManager financingManager;

	@Autowired
	IBanUtil iBanUtil;

	@Autowired
	BCBAddressCheck bcbAddressCheck;

	/**
	 * @author huxinxing
	 * @description 理财流水_交易管理列表
	 */
	@LoginRequired
	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public Object tradeList(@RequestBody JSONParam[] params, HttpServletRequest request) throws Exception {
		try{
			return financingService.queryFinancingTradeManagerList(params);
		}catch (Exception e){
			log.error("获取理财流水交易管理列表失败",e);
		}
		return new ResponseResult(WorldValue.ERROR,"500","请求失败");
	}

	/**
	 * @author huxinxing
	 * @description 理财流水_交易流水列表
	 */
	@LoginRequired
	@RequestMapping(value = "flow/list", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public Object financingTradeFlowList(@RequestBody JSONParam[] params, HttpServletRequest request) throws Exception {
		try{
			return financingService.queryFinancingTradeFlowList(params);
		}catch (Exception e){
			log.error("获取理财流水交易流水列表失败",e);
		}
		return new ResponseResult(WorldValue.ERROR,"500","请求失败");

	}

	@RequestMapping(value = "/agents/list",method = RequestMethod.GET)
	public ResponseResult agentsList(HttpServletRequest request) {
		try{
			Integer tradeId = Integer.parseInt(request.getParameter("tradeId"));
			List<UserInvestRateDto> list = financingService.queryAgentTradeList(tradeId);
			return new ResponseResult(WorldValue.SUCCESS,"200",list,"请求成功");
		}catch (Exception e){
			log.error("获取代理收益列表失败",e);
		}
		return new ResponseResult(WorldValue.ERROR,"500","请求失败");
	}

	@ApiOperation("提币审核列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type", value = "审核类型0：待审核；1：审核通过；2:审核驳回", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "user", value = "用户id或者用户昵称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数据", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页编号", required = true, dataType = "String", paramType = "query")
	})
	@LoginRequired
	@RequestMapping(value = "withdraw/list", method = RequestMethod.POST)
	public ResponseResult financingWithdrawList(
			HttpServletRequest request,
			@RequestParam(value = "user",required = false) String user,
			@RequestParam(value = "type",required = true) Integer type,
			@RequestParam(value = "pageSize",required = false) Integer pageSize,
			@RequestParam(value = "pageNum",required = true) Integer pageNum
	)throws Exception {
		try{
			return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),financingService.queryFinancingWithDrawList(user,type,pageSize,pageNum),ResponseStatus.SUCCESS.getValue());
		}catch (Exception e){
			log.error("获取订单列表失败",e);
		}
		return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取订单列表失败",ResponseStatus.CODE_WRONG.getValue());
	}

	/**
	 * @author huxinxing
	 * @description 理财流水_提币审核
	 */
	@ApiOperation("提币审核")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type", value = "审核类型0：待审核；1：审核通过；2:审核驳回", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "提币id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "refuseContain", value = "审核拒绝理由项目状态", required = true, dataType = "String", paramType = "query")
	})
	@LoginRequired
	@RequestMapping(value = "approval", method = RequestMethod.POST)
	public ResponseResult approval(HttpServletRequest request,@RequestParam(value = "type",required = false) String type,@RequestParam(value = "id",required = false) Integer id,@RequestParam(value = "refuseContain",required = false) String refuseContain) throws Exception {
		try{
			if(type.equals("2")){
				type = "3";
			}
			SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
			ResponseResult result = financingService.approval(account, id, refuseContain,type);
			return result;
		}catch (Exception e){
			log.error("提币异常",e);
		}
		return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"提币异常",ResponseStatus.CODE_WRONG.getValue());
	}

	/**
	 * @author huxinxing
	 * @description 处理未识别打币记录
	 */
	@LoginRequired
	@RequestMapping(value = "/unknownIncome/deal", method = RequestMethod.POST)
	public ResponseResult unknownIncomeDeal(HttpServletRequest request) {
		try{
			Integer id = Integer.parseInt(request.getParameter("id"));
			SystemLoginAccount account = (SystemLoginAccount) request.getAttribute("account");
			return financingService.FinancingUnknownIncomeDeal(account, id);
		}catch (Exception e){
			log.error("处理未识别打币记录失败",e);
		}
		return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"处理未识别打币记录失败",ResponseStatus.CODE_WRONG.getValue());
	}

	/**
	 * @author huxinxing
	 * @description 查询项目钱包地址
	 */
	@LoginRequired
	@RequestMapping(value = "/wallets", method = RequestMethod.GET)
	public ResponseResult projectWallets(HttpServletRequest request) {
		try{
			String financingUuid = request.getParameter("financingUuid");
			List<FinancingWalletInfo> wallets = financingService.queryFinancingWalletsByFinancingUuid(financingUuid);

			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(), wallets,
					ResponseStatus.SUCCESS.getValue());
		}catch (Exception e){
			log.error("查询项目钱包地址失败",e);
		}
		return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"查询项目钱包地址失败",ResponseStatus.CODE_WRONG.getValue());
	}

	/**
	 * @author huxinxing
	 * @description 项目列表_项目详情展示
	 */
	@ApiOperation("获取项目列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "iDisplayStart", value = "起始页", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "iDisplayLength", value = "项目状态", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "financing", value = "项目Uuid/名称，用于查询", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "status", value = "项目状态：0待开启、1募集中、2募集暂停、3募集结束", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/base/list", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public Object active(@RequestBody JSONParam[] params, HttpServletRequest request) throws Exception {
		try{
			return financingService.queryFinancingBaseInfoList(params);
		}catch (Exception e){
			log.error("获取项目列表获取失败",e);
		}
		return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"获取项目列表失败",ResponseStatus.CODE_WRONG.getValue());
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@ApiOperation("编辑项目")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "financingUuid", value = "项目Uuid", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "status", value = "项目状态", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Title", value = "项目名称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Attentions", value = "项目说明", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "CoinType", value = "理财币种", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Amount", value = "理财金额", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "CoinLimit", value = "单笔限投金额", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Token", value = "钱包地址", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "tag", value = "项目标签", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Freeze", value = "理财期限", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "AnnualRate", value = "年化收益率", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "TotalRate", value = "用户保底收益", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "FoundationRate", value = "超额收益基金公司返点比例", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "SubscriptionFeeRate", value = "认购费", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "DiscountRate", value = "认购费折扣", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@ResponseBody
	@RequestMapping(value = "/status/update", method = RequestMethod.POST)
	public ResponseResult projectStatusUpdate(HttpServletRequest request,@RequestBody MultipartFile ETH,@RequestBody MultipartFile BCB ) throws IOException, Exception {
		try{
			SystemLoginAccount systemLoginAccount = (SystemLoginAccount) request.getAttribute("account");
			
			JSONObject paramsJSON = JSONObject.parseObject(request.getParameter("params"));
			
			Map<String, Object> map = paramsJSON;

			JSONArray list = JSONArray.parseArray(map.get("list").toString());
			
			List<FinancingWalletInfo> financingWalletInfoList = new ArrayList<>();
			FinancingBaseInfo financingBaseInfo = new FinancingBaseInfo();
			Map<String,MultipartFile> Token = new HashMap<>();
			Token.put("BCB",BCB);
			Token.put("ETH",ETH);

			if (!ObjectUtils.isEmpty(Token)) {

				if (!ObjectUtils.isEmpty(Token.get("BCB"))){
					FinancingWalletInfo financingWalletInfo = new WalletFileParser().gainFinancingWalletInfoUSDX(Token.get("BCB").getBytes());
					if (bcbAddressCheck.checkAddress(financingWalletInfo.getToken())){
						financingWalletInfoList.add(financingWalletInfo);
					}else {
						return new ResponseResult(WorldValue.ERROR, ResponseStatus.DATA_PROJECT_NOT_FUND.getKey() ,"加载的BCB地址文件错误","加载的BCB地址文件错误");
					}
					financingWalletInfoList.add(financingWalletInfo);

				}


				if(!ObjectUtils.isEmpty(Token.get("ETH"))){
					FinancingWalletInfo financingWalletInfo = new WalletFileParser().gainFinancingWalletInfoETH(Token.get("ETH").getBytes());
					if(iBanUtil.isAddress(financingWalletInfo.getToken())){
						financingWalletInfoList.add(financingWalletInfo);
					}else {
						return new ResponseResult(WorldValue.ERROR, ResponseStatus.DATA_PROJECT_NOT_FUND.getKey(),"加载的ETH地址文件错误" ,"加载的ETH地址文件错误");
					}

				}

			}

			if (!StringUtils.isEmpty(map.get("financingUuid")))
				financingBaseInfo.setFinancingUuid(map.get("financingUuid").toString());
			if (!StringUtils.isEmpty(map.get("status")))
				financingBaseInfo.setStatus(Integer.parseInt(map.get("status").toString()));
			if (!StringUtils.isEmpty(map.get("Title")))
				financingBaseInfo.setTitle(map.get("Title").toString());
			if (!StringUtils.isEmpty(map.get("Attentions")))
				financingBaseInfo.setAttentions(map.get("Attentions").toString());
			if (!StringUtils.isEmpty(map.get("tag")))
				financingBaseInfo.setTag(map.get("tag").toString());
			if (!StringUtils.isEmpty(map.get("CoinType")))
				financingBaseInfo.setCoinType(map.get("CoinType").toString());
			if (!StringUtils.isEmpty(map.get("Amount")))
				financingBaseInfo.setAmount(new BigDecimal(map.get("Amount").toString()));
			if (!StringUtils.isEmpty(map.get("CoinLimit"))){
				financingBaseInfo.setCoinLimit(new BigDecimal(map.get("CoinLimit").toString()));
			}else {
				financingBaseInfo.setCoinLimit(financingBaseInfo.getAmount());
			}
			if (!StringUtils.isEmpty(map.get("coinMinLimit"))) {
				financingBaseInfo.setCoinMinLimit(new BigDecimal(map.get("coinMinLimit").toString()));
			}else {
				financingBaseInfo.setCoinMinLimit(BigDecimal.valueOf(0));
			}
			if (!StringUtils.isEmpty(map.get("Freeze"))){
				financingBaseInfo.setFreezeUnit(map.get("Freeze").toString().substring(map.get("Freeze").toString().length() - 1, map.get("Freeze").toString().length()));
				financingBaseInfo.setFreezeNumber(Integer.parseInt(map.get("Freeze").toString().substring(0, map.get("Freeze").toString().length() - 1)));
			}
			if (!StringUtils.isEmpty(map.get("AnnualRate")))
				financingBaseInfo.setAnnualRate(new BigDecimal(map.get("AnnualRate").toString()));
			if (!StringUtils.isEmpty(map.get("TotalRate")))
				financingBaseInfo.setTotalRate(new BigDecimal(map.get("TotalRate").toString()));
			if (!StringUtils.isEmpty(map.get("FoundationRate")))
				financingBaseInfo.setFoundationRate(new BigDecimal(map.get("FoundationRate").toString()));
			if (!StringUtils.isEmpty(map.get("SubscriptionFeeRate")))
				financingBaseInfo.setSubscriptionFeeRate(new BigDecimal(map.get("SubscriptionFeeRate").toString()));
			if (!StringUtils.isEmpty(map.get("DiscountRate")))
				financingBaseInfo.setDiscountRate(new BigDecimal(map.get("DiscountRate").toString()));
			if (!StringUtils.isEmpty(map.get("paymentType")))
				financingBaseInfo.setPaymentType(map.get("paymentType").toString());

			return financingManager.updateFinancingProjectManager(financingBaseInfo, financingWalletInfoList,list,systemLoginAccount);

		}catch(Exception e){
			log.error("项目修改失败",e);
		}
		return new ResponseResult(WorldValue.ERROR, "项目修改失败 " ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue()+"请校验参数和钱包文件");
	}

	@LoginRequired
	// @RequiresPermissions(value={"projectDetail"},logical=Logical.OR)
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ResponseResult projectDetail(HttpServletRequest request) throws Exception {
		try{
			String financingUuid = request.getParameter("financingUuid");
			FinancingBaseInfo financingBaseInfoVO = financingService.queryFinancingBaseInfoByUuid(financingUuid);
			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(), financingBaseInfoVO,
					ResponseStatus.SUCCESS.getValue());
		}catch (Exception e){
			log.error("获取项目详情失败",e);
		}
		return new ResponseResult(WorldValue.ERROR, "获取项目详情失败 " ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
	}

	@ApiOperation("添加项目")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Title", value = "项目名称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Attentions", value = "项目说明", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "CoinType", value = "理财币种", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Amount", value = "理财金额", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "CoinLimit", value = "单笔限最大投金额", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "coinMinLimit", value = "单笔最小限投金额", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Token", value = "钱包地址", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "Freeze", value = "理财期限", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "tag", value = "项目标签", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "AnnualRate", value = "年化收益率", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "TotalRate", value = "用户保底收益", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "FoundationRate", value = "超额收益基金公司返点比例", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "SubscriptionFeeRate", value = "认购费", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "PaymentType", value = "支付币种", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "DiscountRate", value = "认购费折扣", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST) 
	public ResponseResult financingProjectAdd(HttpServletRequest request,@RequestBody MultipartFile ETH,@RequestBody MultipartFile BCB) throws Exception {
		
		try {
			SystemLoginAccount systemLoginAccount = (SystemLoginAccount) request.getAttribute("account");
			FinancingBaseInfo financingBaseInfo = new FinancingBaseInfo();
			List<FinancingWalletInfo> financingWalletInfoList = new ArrayList<>();
			Map<String,MultipartFile> map = new HashMap<>();
			map.put("BCB",BCB);
			map.put("ETH",ETH);
			if (!ObjectUtils.isEmpty(map)) {

				if (!ObjectUtils.isEmpty(map.get("BCB"))){
					FinancingWalletInfo financingWalletInfo = new WalletFileParser().gainFinancingWalletInfoUSDX(map.get("BCB").getBytes());
					if (bcbAddressCheck.checkAddress(financingWalletInfo.getToken())){
						financingWalletInfoList.add(financingWalletInfo);
					}else {
						return new ResponseResult(WorldValue.ERROR, ResponseStatus.DATA_PROJECT_NOT_FUND.getKey() ,"加载的BCB地址文件错误","加载的BCB地址文件错误");
					}
					financingWalletInfoList.add(financingWalletInfo);
				}


				if(!ObjectUtils.isEmpty(map.get("ETH"))){
					FinancingWalletInfo financingWalletInfo = new WalletFileParser().gainFinancingWalletInfoETH(map.get("ETH").getBytes());
					if(iBanUtil.isAddress(financingWalletInfo.getToken())){
						financingWalletInfoList.add(financingWalletInfo);
					}else {
						return new ResponseResult(WorldValue.ERROR, ResponseStatus.DATA_PROJECT_NOT_FUND.getKey(),"加载的ETH地址文件错误" ,"加载的ETH地址文件错误" );
					}

				}

			}
			String Title = request.getParameter("Title");
			String Attentions = request.getParameter("Attentions");
			String CoinType = request.getParameter("CoinType");
			String Amount = request.getParameter("Amount");
			String CoinMinLimit = request.getParameter("coinMinLimit");
			String CoinLimit = request.getParameter("CoinLimit");
			String Freeze = request.getParameter("Freeze");
			String AnnualRate = request.getParameter("AnnualRate");
			String TotalRate = request.getParameter("TotalRate");
			String FoundationRate = request.getParameter("FoundationRate");
			String SubscriptionFeeRate = request.getParameter("SubscriptionFeeRate");
			String DiscountRate = request.getParameter("DiscountRate");
			String PaymentType = request.getParameter("PaymentType");
			String tag = request.getParameter("tag");


			if (!StringUtils.isEmpty(Title))
				financingBaseInfo.setTitle(Title);
			if (!StringUtils.isEmpty(Attentions))
				financingBaseInfo.setAttentions(Attentions);
			if (!StringUtils.isEmpty(CoinType))
				financingBaseInfo.setCoinType(CoinType);
			if (!StringUtils.isEmpty(Amount))
				financingBaseInfo.setAmount(new BigDecimal(Amount));
			if (!StringUtils.isEmpty(Amount))
				financingBaseInfo.setAmount(new BigDecimal(Amount));
			if (!StringUtils.isEmpty(CoinLimit)){
				financingBaseInfo.setCoinLimit(new BigDecimal(CoinLimit));
			}else{
				financingBaseInfo.setCoinLimit(financingBaseInfo.getAmount());
			}
			if (!StringUtils.isEmpty(CoinMinLimit)) {
				financingBaseInfo.setCoinMinLimit(new BigDecimal(CoinMinLimit));
			}else{
				financingBaseInfo.setCoinMinLimit(BigDecimal.ZERO);
			}
			if (!StringUtils.isEmpty(Freeze)) {
				financingBaseInfo.setFreezeUnit(Freeze.substring(Freeze.length() - 1, Freeze.length()));
				financingBaseInfo.setFreezeNumber(Integer.parseInt(Freeze.substring(0, Freeze.length() - 1)));
			}
			if (!StringUtils.isEmpty(TotalRate))
				financingBaseInfo.setTotalRate(new BigDecimal(TotalRate));
			if (!StringUtils.isEmpty(AnnualRate))
				financingBaseInfo.setAnnualRate(new BigDecimal(AnnualRate));
			if (!StringUtils.isEmpty(FoundationRate))
				financingBaseInfo.setFoundationRate(new BigDecimal(FoundationRate));
			if (!StringUtils.isEmpty(SubscriptionFeeRate))
				financingBaseInfo.setSubscriptionFeeRate(new BigDecimal(SubscriptionFeeRate));
			if (!StringUtils.isEmpty(DiscountRate))
				financingBaseInfo.setDiscountRate(new BigDecimal(DiscountRate));
			if (!StringUtils.isEmpty(PaymentType))
				financingBaseInfo.setPaymentType(PaymentType);
			if (!StringUtils.isEmpty(tag))
				financingBaseInfo.setTag(tag);
			

			return financingManager.financingProjectAddManager(financingBaseInfo, financingWalletInfoList,systemLoginAccount);
		}catch(Exception e){
			log.error("新增项目失败",e);
		}
		return new ResponseResult(WorldValue.ERROR, "新增项目失败" ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue()+ "请校验参数和钱包文件");
	}

	@ApiOperation("获取项目标签列表")
	@LoginRequired
	@RequestMapping(value = "/queryTagList", method = RequestMethod.POST)
	public ResponseResult queryTagList(HttpServletRequest request){
		try{
			return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),financingManager.queryTagListManager() ,ResponseStatus.SUCCESS.getValue());
		}catch (Exception e){
			log.error("获取项目标签列表失败",e);
		}
		return new ResponseResult(WorldValue.ERROR, "financingUuid 不能为空",ResponseStatus.CODE_WRONG.getKey(),ResponseStatus.CODE_WRONG.getValue());
	}

	/**
	 * @param request
	 * @return
	 */
	@ApiOperation("详情_项目相关")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "financingUuid", value = "项目uuid", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public ResponseResult queryFinancingInfoById(HttpServletRequest request) {
		try{
			String financingUuid = request.getParameter("financingUuid");
			if(financingUuid == null || financingUuid.length() == 0){
				return new ResponseResult(WorldValue.ERROR, "financingUuid 不能为空",ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
			}
			return financingManager.GainQueryFinancingInfoByIdManager(financingUuid);
		}catch(Exception e){
			log.error("获取项目标签列表失败",e);

		}
		return new ResponseResult(WorldValue.ERROR, "详情_项目相关获取失败"  ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());

	}

	@ApiOperation("详情_语言相关")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "financingUuid", value = "项目uuid", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/EditLanager", method = RequestMethod.POST)
	public ResponseResult EditLanager(HttpServletRequest request) {
		try{
			String financingUuid = request.getParameter("financingUuid");
			if(financingUuid == null || financingUuid.length() == 0){
				return new ResponseResult(WorldValue.ERROR, "financingUuid 不能为空",ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
			}
			return financingManager.GainEditLanagerManager(financingUuid);
		}catch(Exception e){
			log.error("详情_语言相关失败",e);
		}
		return new ResponseResult(WorldValue.ERROR, "详情_语言相关获取失败" ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());

	}

	@ApiOperation("语言编辑保存")
	@LoginRequired
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params", value = "语言", required = true, dataType = "String", paramType = "query") })
	@RequestMapping(value = "/SaveFinancingLanager", method = RequestMethod.POST)
	public ResponseResult SaveFinancingLanager(
			HttpServletRequest request) {
		try{
			SystemLoginAccount systemLoginAccount = (SystemLoginAccount) request.getAttribute("account");
			
			JSONObject paramsJSON = JSONObject.parseObject(request.getParameter("params"));
			
			Map<String, Object> map = paramsJSON;

			return financingManager.SaveFinancingLanagerManager(map,systemLoginAccount);
		}catch(Exception e){
			log.error("语言编辑保存失败",e);
		}
		return new ResponseResult(WorldValue.ERROR, "语言编辑失败" ,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
	}
}
