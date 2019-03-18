package com.bcb.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.dto.statistics.FinancingTranferRecord;
import com.bcb.domain.dao.StatisticsDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.bcb.bean.JSONParam;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.manager.StatisticsManager;
import com.bcb.util.MapUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Slf4j
@Api("数据统计接口")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

	@Autowired
	StatisticsManager statisticsManager;
	
	@Autowired
	StatisticsDao statisticDao;
	

	@ApiOperation("资金概况金额")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flag", value = "选择用户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "beginTime", value = "统计开始时间", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "统计结束时间", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/Overview", method = RequestMethod.POST)
	public ResponseResult Overview(HttpServletRequest request) {
		try{
			String beginTime = request.getParameter("beginTime");
			String endTime = request.getParameter("endTime");
			Integer flag = Integer.parseInt(request.getParameter("flag"));
			if (flag != 0 && flag != 1) {
				return new ResponseResult(WorldValue.ERROR, ResponseStatus.DATA_WRONG.getKey(),
						ResponseStatus.DATA_WRONG.getValue());
			}
			
			if (StringUtils.isEmpty(beginTime)) {
				beginTime = "1970-01-01 00:00:00";
			}
			
			if(StringUtils.isEmpty(endTime)){
				endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			
			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),
					statisticsManager.OverviewManager(flag, beginTime, endTime), ResponseStatus.SUCCESS.getValue());
		}catch(Exception e){
			log.error("资金概况金额失败",e);

		}
		return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
				"资金概况金额失败", ResponseStatus.CODE_WRONG.getValue());
		
	}

	@ApiOperation("资金概况-收益折线图")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flag", value = "选择用户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "length", value = "时长：7天，15天，30天", required = true, dataType = "String", paramType = "query") })
	@RequestMapping(value = "/Diagrams", method = RequestMethod.POST)
	@LoginRequired
	public ResponseResult Diagrams(HttpServletRequest request) {
		try{
			String length = request.getParameter("length");
			Integer flag = Integer.parseInt(request.getParameter("flag"));
		
			if(!length.equals("7") && !length.equals("15") && !length.equals("30")){
				return new ResponseResult(WorldValue.ERROR,"length 参数错误",ResponseStatus.DATA_WRONG.getKey(),
						ResponseStatus.DATA_WRONG.getValue());
			}
			
			if( flag != 0 && flag != 1){
				return new ResponseResult(WorldValue.ERROR, "flag 参数错误",ResponseStatus.DATA_WRONG.getKey(),
						ResponseStatus.DATA_WRONG.getValue());
			}

			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),
					statisticsManager.StatDiagramsDtoManager(length,flag), ResponseStatus.SUCCESS.getValue());
			
		}catch(Exception e){
			log.error("折线图请求失败",e);
			return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
					e.getMessage(), ResponseStatus.CODE_WRONG.getValue());
		}
	}

	@ApiOperation("资金概况-已收流水")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "length", value = "时长：7天，15天，30天", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "flag", value = "value：0,1", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/ReceivedFlows", method = RequestMethod.POST)
	public String ReceivedFlows(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			Map<String, String> paramMap = MapUtil.convertToMap(params);
		
			Integer flag = Integer.parseInt(paramMap.get("flag"));
			String sEcho = paramMap.get("sEcho");
			String length = paramMap.get("length");
		
			if(!length.equals("7") && !length.equals("15") && !length.equals("30")){
				return WorldValue.ERROR + "length参数错误" + ResponseStatus.DATA_WRONG.getKey() + " " + ResponseStatus.DATA_WRONG.getValue();
			}
			
			if( flag != 0 && flag != 1){
				return WorldValue.ERROR + " flag参数错误" + ResponseStatus.DATA_WRONG.getKey() + " " + ResponseStatus.DATA_WRONG.getValue();
			}
			
			return statisticsManager.StatReceivedFlowsDtoManager(length,flag,sEcho);
			
		}catch(Exception e){
			log.error("资金概况-已收流水获取失败",e);
				return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+""+e.getMessage()+""+ResponseStatus.CODE_WRONG.getValue();
		}

	}

	@ApiOperation("资金概况-已支付流水")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flag", value = "选择用户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "length", value = "时长	：7天，15天，30天", required = true, dataType = "String", paramType = "query") })
//	@LoginRequired
	@RequestMapping(value = "/PaidFlows", method = RequestMethod.POST)
	public String PaidFlows(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			Map<String, String> paramMap = MapUtil.convertToMap(params);
			Integer flag = Integer.parseInt(paramMap.get("flag"));
			String sEcho = paramMap.get("sEcho");
			String length = paramMap.get("length");

//			Integer flag = Integer.parseInt(request.getParameter("flag"));
//			String sEcho = request.getParameter("sEcho");
//			String length = request.getParameter("length");


			if(!length.equals("7") && !length.equals("15") && !length.equals("30")){
				return WorldValue.ERROR + "length参数错误" + ResponseStatus.DATA_WRONG.getKey() + " " + ResponseStatus.DATA_WRONG.getValue();
			}
			
			if( flag != 0 && flag != 1){
				return WorldValue.ERROR + " flag参数错误" + ResponseStatus.DATA_WRONG.getKey() + " " + ResponseStatus.DATA_WRONG.getValue();
			}
		
			return statisticsManager.StatPaidFlowsDtoManager(length,flag,sEcho);
			
		}catch(Exception e){
			log.error("资金概况-已支付流水失败",e);
			return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+""+e.getMessage()+""+ResponseStatus.CODE_WRONG.getValue();
		}

	}

	@ApiOperation("理财项目")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flag", value = "选择用户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "iDisplayStart", value = "起始页", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "iDisplayLength", value = "页长度", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "projectState", value = "项目状态", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "financingUuid", value = "项目Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "beginTime", value = "统计开始时间", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "统计结束时间", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/FinancingProject", method = RequestMethod.POST)
	public String FinancingProject(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			Map<String, String> paramMap = MapUtil.convertToMap(params);
		
			String sEcho = paramMap.get("sEcho");
			Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
			Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
			Integer flag = Integer.parseInt(paramMap.get("flag"));
			String beginTime = paramMap.get("beginTime");
			String endTime = paramMap.get("endTime");
			String projectState = paramMap.get("projectState");
			String financingUuid = paramMap.get("financingUuid");
			
			if(beginTime == null || beginTime.length() == 0){
				beginTime = "1970-00-00 00:00:00";
			}
			
			if(endTime == null || endTime.length() == 0){
				endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			
			if(flag != 0 && flag != 1){
				return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+" flag 参数错误 "+ResponseStatus.CODE_WRONG.getValue();
			}
			
			return statisticsManager.FinancingProjectManager(sEcho,beginTime,endTime,projectState,financingUuid,start,length,flag);
		}catch(Exception e){
			log.error("理财项目",e);
			return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+""+e.getMessage()+""+ResponseStatus.CODE_WRONG.getValue();
		}
	}

	@ApiOperation("理财项目项目详情")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flag", value = "选择用户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "serialNum", value = "项目Id", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/FinancingProjectDetail", method = RequestMethod.POST)
	public ResponseResult FinancingProjectDetail(HttpServletRequest request) {
		try{
			Integer flag = Integer.parseInt(request.getParameter("flag"));
			String serialNum = request.getParameter("serialNum");
			if(flag != 0 && flag != 1 && serialNum != null){
				return new ResponseResult(WorldValue.ERROR , ResponseStatus.DATA_WRONG.getKey() , ResponseStatus.DATA_WRONG.getValue());
			}
			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),statisticsManager.FinancingProjectDetailManager(serialNum,flag), ResponseStatus.SUCCESS.getValue());
		}catch(Exception e){
			log.error("获取项目项目详情失败" , e);
			return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),e.getMessage(), ResponseStatus.CODE_WRONG.getValue());
		}

	}

	@ApiOperation("理财项目项目详情-用户列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "flag", value = "选择用户", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayStart", value = "起始页", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayLength", value = "页长度", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "serialNum", value = "项目Id", required = true, dataType = "String", paramType = "query") })
	@LoginRequired
	@RequestMapping(value = "/FinancingProjectDetailUser", method = RequestMethod.POST)
	public String FinancingProjectDetailUser(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			Map<String, String> paramMap = MapUtil.convertToMap(params);

			String sEcho = paramMap.get("sEcho");
			Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
			Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
			Integer flag = Integer.parseInt(paramMap.get("flag"));
			String serialNum = paramMap.get("serialNum");
//
//			String sEcho = request.getParameter("sEcho");
//			Integer start = Integer.parseInt(request.getParameter("iDisplayStart"));
//			Integer length = Integer.parseInt(request.getParameter("iDisplayLength"));
//			String serialNum = request.getParameter("serialNum");
//			Integer flag = Integer.parseInt(request.getParameter("flag"));

			if(flag != 0 && flag != 1 && serialNum != null){
				return WorldValue.ERROR + " flag 参数错误" + ResponseStatus.DATA_WRONG.getKey() + " " + ResponseStatus.DATA_WRONG.getValue();
			}
			return statisticsManager.FinancingProjectDetailUserManager(sEcho,start,length,serialNum,flag);
		}catch(Exception e){
			log.error("项目用户列表获取失败" ,e);
			return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+""+e.getMessage()+""+ResponseStatus.CODE_WRONG.getValue();
		}
	}

	@ApiOperation("理财项目_导出投资该项目的用户信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flag", value = "选择用户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "serialNum", value = "项目serialNum", required = true, dataType = "String", paramType = "query")})
	@RequestMapping(value = "/geExportFinancingUser", method = RequestMethod.GET)
	public void geExportExeclUser(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "serialNum",required = true) Integer serialNum,
			@RequestParam(value = "flag",required = true) Integer flag
	) {
		try{
			HttpSession session = request.getSession();
			boolean strData = (boolean) session.getAttribute("isTrueExportExecl");
			if (!strData){
				return ;
			}
			session.setAttribute("isTrueExportExecl", false);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(flag != 0 && flag != 1 && StringUtils.isEmpty(serialNum)){
				return;
			}
			List<Map<String,Object>> list =  statisticsManager.financingUserExportExecl(serialNum,flag);
			if(CollectionUtils.isEmpty(list)){
				return ;
			}
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(serialNum.toString());

			String fileName = "financing"+serialNum+"_User.xls";
			int rowNum = 1;
			String[] headers = {"编号","UID","昵称","form","to","投资金额","折算法币","BCB理财价格","投资时间","到期时间"};
			HSSFRow row = sheet.createRow(0);
			for(int i=0;i<headers.length;i++){
				HSSFCell cell = row.createCell(i);
				HSSFRichTextString text = new HSSFRichTextString(headers[i]);
				cell.setCellValue(text);
			}

			for(int i = 0; i < list.size(); i++){
				Map<String,Object> obj = list.get(i);
				HSSFRow row1 = sheet.createRow(rowNum);
				row1.createCell(0).setCellValue(i + 1);
				row1.createCell(1).setCellValue(obj.get("userId").toString());
				row1.createCell(2).setCellValue(obj.get("displayName").toString());
				row1.createCell(3).setCellValue(obj.get("fromToken").toString());
				row1.createCell(4).setCellValue(obj.get("toToken").toString());
				row1.createCell(5).setCellValue(obj.get("paymentAmount").toString() + obj.get("paymentType").toString());
				row1.createCell(6).setCellValue(new BigDecimal(obj.get("UsdxAmount").toString())+ "USDX");
				row1.createCell(7).setCellValue(obj.get("bcb2usdx").toString());
				row1.createCell(8).setCellValue(simpleDateFormat.format(simpleDateFormat.parse(obj.get("createTime").toString())));
				row1.createCell(9).setCellValue(simpleDateFormat.format(simpleDateFormat.parse(obj.get("expireTime").toString())));
				rowNum++;
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName);
			response.flushBuffer();
			workbook.write(response.getOutputStream());
		}catch (Exception e){
			log.error("体验金_导出到execl文档异常",e);
			return ;
		}
		return ;
	}
	
	
	@ApiOperation("用户列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "User", value = "支持查询可以是用户id或者用户昵称", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iSortCol_0", value = "排序编号 6 投资收益  7代理收益", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "sSortDir_0", value = "排序方式ASC 升序  DESC降序", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayStart", value = "起始页", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayLength", value = "页长度", required = true, dataType = "String", paramType = "query")})
	@LoginRequired
	@RequestMapping(value = "/FinancingUser", method = RequestMethod.POST)
	public String FinancingUser(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			
			Map<String, String> paramMap = MapUtil.convertToMap(params);
			
			String sEcho = paramMap.get("sEcho");
			Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
			Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
			String User = paramMap.get("User");
			String iSortCol_0 = paramMap.get("iSortCol_0");
			String sSortDir_0 = paramMap.get("sSortDir_0");

			return  statisticsManager.FinancingUserManager(User, sEcho, start, length,iSortCol_0,sSortDir_0);
			
		}catch(Exception e){
			log.error("项目投资用户列表获取失败" ,e);
			return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+e.getMessage()+ResponseStatus.CODE_WRONG.getValue();
		}
		
		
	}
	
	
	@ApiOperation("用户信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "UserId", value = "选择用户", required = true, dataType = "String", paramType = "query")})
	@LoginRequired
	@RequestMapping(value = "/FinancingUserMessage", method = RequestMethod.POST)
	public ResponseResult FinancingUserMessage(HttpServletRequest request) {
		try{
			
			String Userid = request.getParameter("UserId");
			String stepStr = request.getParameter("step");
			if(stepStr == null){
				return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),"请填写用户等级", ResponseStatus.CODE_WRONG.getValue());
			}
			Integer step = Integer.parseInt(stepStr.subSequence(0, 1).toString());
			
			if(Userid == null){
				Userid = "";
			}
			if(Userid.length() == 0){
				return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),"请填写合法的用户id 参数错误", ResponseStatus.CODE_WRONG.getValue());
			}
			
			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),
					statisticsManager.FinancingUserMessageManager(Userid,step), ResponseStatus.SUCCESS.getValue());
		}catch(Exception e){
			log.error("用户信息失败" ,e);
			return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
					e.getMessage(), ResponseStatus.CODE_WRONG.getValue());
		}
		
	}
	
	
	@ApiOperation("用户_投资收益")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayStart", value = "起始页", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayLength", value = "页长度", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "UserId", value = "选择用户", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "beginTime", value = "查询起始时间", required = true, 	dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "endTime", value = "查询结束时间", required = true, dataType = "String", paramType = "query")
		})
	@LoginRequired
	@RequestMapping(value = "/FinancingUserInvestmentIncome", method = RequestMethod.POST)
	public String FinancingUserInvestmentIncome(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			Map<String, String> paramMap = MapUtil.convertToMap(params);
			
			String sEcho = paramMap.get("sEcho");
			Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
			Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
			String Userid = paramMap.get("UserId");
			String beginTime = paramMap.get("beginTime");
			String endTime = paramMap.get("endTime");

			if(beginTime == null || beginTime.length() == 0){
				beginTime = "1970-00-00 00:00:00";
			}
			
			if(endTime == null || endTime.length() == 0){
				endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			
			if(Userid == null){
				Userid = "";
			}
			if(Userid.length() == 0){
				return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+" 请填写合法的用户id 参数错误 "+ResponseStatus.CODE_WRONG.getValue();
			}
			
			return  statisticsManager.FinancingUserInvestmentIncomeManager(Userid, sEcho, start, length,beginTime,endTime);
		}catch(Exception e){
			log.error("用户_投资收益失败" ,e);
			return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+e.getMessage()+ResponseStatus.CODE_WRONG.getValue();
		}
		
	}
	
	
	@ApiOperation("用户_代理收益")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayStart", value = "起始页", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayLength", value = "页长度", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "UserId", value = "选择用户", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "beginTime", value = "查询起始时间", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "endTime", value = "查询结束时间", required = true, dataType = "String", paramType = "query")
		})
	@LoginRequired
	@RequestMapping(value = "/FinancingUserAgent", method = RequestMethod.POST)
	public String FinancingUserAgent(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			Map<String, String> paramMap = MapUtil.convertToMap(params);
			
			String sEcho = paramMap.get("sEcho");
			Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
			Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
			Integer Userid = Integer.parseInt(paramMap.get("UserId"));
			String beginTime = paramMap.get("beginTime");
			String endTime = paramMap.get("endTime");

			if(beginTime == null || beginTime.length() == 0){
				beginTime = "1970-00-00 00:00:00";
			}
			
			if(endTime == null || endTime.length() == 0){
				endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}


			
			return  statisticsManager.FinancingUserAgentManager(Userid, sEcho, start, length,beginTime,endTime);
		}catch(Exception e){
			log.error("用户_代理收益失败" ,e);
			return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+e.getMessage()+ResponseStatus.CODE_WRONG.getValue();
		}
		
	}
	
	@ApiOperation("用户_我的下级")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sEcho", value = "页数", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayStart", value = "起始页", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "iDisplayLength", value = "页长度", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "UserId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "Junior", value = "二级用户id或者用户昵称", required = true, dataType = "String", paramType = "query")
		})
	@LoginRequired
	@RequestMapping(value = "/FinancingUserJunior", method = RequestMethod.POST)
	public String FinancingUserJunior(HttpServletRequest request,@RequestBody JSONParam[] params) {
		try{
			Map<String, String> paramMap = MapUtil.convertToMap(params);
			
			String sEcho = paramMap.get("sEcho");
			Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
			Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
			String Userid = paramMap.get("UserId");
			String Junior = paramMap.get("Junior");
			String iSortCol_0 = paramMap.get("iSortCol_0");
			String sSortDir_0 = paramMap.get("sSortDir_0");
			String stepStr = paramMap.get("step");

			if(stepStr == null){
				return WorldValue.ERROR+ResponseStatus.CODE_WRONG.getKey()+"请填写用户等级"+ResponseStatus.CODE_WRONG.getValue();
			}
			Integer step = Integer.parseInt(stepStr.subSequence(0, 1).toString());
			
			if(Userid == null){
				Userid = "";
			}
			if(Userid.length() == 0){
				return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+" 请填写合法的用户id 参数错误 "+ResponseStatus.CODE_WRONG.getValue();
			}
			
			
			return  statisticsManager.FinancingUserJuniorManager(Userid,Junior, sEcho, start, length,iSortCol_0,sSortDir_0,step);
		}catch(Exception e){
			log.error("用户_我的下级" ,e);
			return WorldValue.ERROR +""+ ResponseStatus.CODE_WRONG.getKey()+e.getMessage()+ResponseStatus.CODE_WRONG.getValue();
		}
		
	}  
	
	
	@ApiOperation("订单管理_到账时间统计")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "Time", value = "", required = true, dataType = "String", paramType = "query")
		})
	@LoginRequired
	@RequestMapping(value = "/FinancingTranferTime", method = RequestMethod.POST)
	public ResponseResult FinancingTranferTime(HttpServletRequest request) {
		try{
			String Time = request.getParameter("Time");

			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(),
					statisticsManager.financingTranferTimeManager(Time), ResponseStatus.SUCCESS.getValue());
		}catch (Exception e){
			log.error("订单管理_到账时间统计异常",e);
			return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
					e.getMessage(), ResponseStatus.CODE_WRONG.getValue());
		}
	}



	@ApiOperation("数据统计_流水记录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tranferId", value = "流水ID",required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "user", value = "用户ID、昵称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "status", value = "流水状态:0、全部 1、提币 2、充币 3、投资  4、代理返点 5、归还本息 6、额外收益本人分拥 7、资金担保机构分拥 8、基金公司分拥 9、赎回本金 10、赎回利息 11、体验金收益 12、加息收益 13、税费", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "String", paramType = "query")
	})
	@LoginRequired
	@RequestMapping(value = "/FinancingTranferRecord", method = RequestMethod.POST)
	public ResponseResult FinancingTranferRecord(
	        HttpServletRequest request,
            @RequestParam(value = "tranferId",required = false) Integer tranferId,
            @RequestParam(value = "user",required = false) String user
            ,@RequestParam(value = "beginTime",required = false) String beginTime,
            @RequestParam(value = "endTime",required = false) String endTime,
            @RequestParam(value = "status",required = false) Integer status,
            @RequestParam(value = "pageSize",required = true) Integer pageSize,
            @RequestParam(value = "pageNum",required =  true) Integer pageNum
    ) throws Exception {
		try{
			if(StringUtils.isEmpty(beginTime)){
				beginTime = "1970-00-00 00:00:00";
			}

			if(StringUtils.isEmpty(endTime)){
				endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(), statisticsManager.FinancingTranferRecord(tranferId,user,beginTime,endTime,status,pageSize,pageNum), ResponseStatus.SUCCESS.getValue());

		}catch (Exception e){
			log.error("数据统计_流水记录异常",e);
		}
		return new ResponseResult(WorldValue.ERROR, ResponseStatus.CODE_WRONG.getKey(),
				"数据统计_流水记录异常", ResponseStatus.CODE_WRONG.getValue());

	}

	@ApiOperation("流水记录_导出到execl文档")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tranferId", value = "流水ID",required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "user", value = "用户ID、昵称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "status", value = "流水状态:0、全部 1、提币 2、充币 3、投资  4、代理返点 5、归还本息 6、额外收益本人分拥 7、资金担保机构分拥 8、基金公司分拥 9、赎回本金 10、赎回利息 11、体验金收益 12、加息收益 13、税费", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", required = true, dataType = "String", paramType = "query")
	})
	@RequestMapping(value = "/FinancingTransferExecl", method = RequestMethod.GET)
	public void FinancingTransferExecl(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "tranferId",required = false) Integer tranferId,
			@RequestParam(value = "user",required = false) String user
			,@RequestParam(value = "beginTime",required = false) String beginTime,
			@RequestParam(value = "endTime",required = false) String endTime,
			@RequestParam(value = "status",required = false) Integer status
	) throws Exception  {
		try{
			HttpSession session = request.getSession();
			boolean strData = (boolean) session.getAttribute("isTrueExportExecl");
			if (!strData){
				return;
			}
			session.setAttribute("isTrueExportExecl", false);
			if(StringUtils.isEmpty(beginTime)){
				beginTime = "1970-01-01 23:59:59";
			}

			if(endTime == null || endTime.length() == 0){
				endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("交易流水");

			//获取需要加载的数据
			List<FinancingTranferRecord> tranferRecordList = statisticsManager.FinancingTransferExeclManager(tranferId,user,status,beginTime,endTime);

			String fileName = "transfer.xls";
			int rowNum = 1;
			String[] headers = {"流水id","类型","用户id","昵称","from","to","币种","金额","时间"};
			HSSFRow row = sheet.createRow(0);
			for(int i=0;i<headers.length;i++){
				HSSFCell cell = row.createCell(i);
				HSSFRichTextString text = new HSSFRichTextString(headers[i]);
				cell.setCellValue(text);
			}
			for (FinancingTranferRecord tranferRecord : tranferRecordList) {
				HSSFRow row1 = sheet.createRow(rowNum);
				row1.createCell(0).setCellValue(tranferRecord.getId());
				row1.createCell(1).setCellValue(tranferRecord.getType());
				row1.createCell(2).setCellValue(tranferRecord.getUserId());
				row1.createCell(3).setCellValue(tranferRecord.getUserName());
				row1.createCell(4).setCellValue(tranferRecord.getFromAddress());
				row1.createCell(5).setCellValue(tranferRecord.getToAddress());
				row1.createCell(6).setCellValue(tranferRecord.getCoinType());
				row1.createCell(7).setCellValue(tranferRecord.getAmount().equals("0E-9")?"0":tranferRecord.getAmount());
				row1.createCell(8).setCellValue(tranferRecord.getTransferTime());
				rowNum++;
			}

			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName);
			response.flushBuffer();
			workbook.write(response.getOutputStream());
		}catch (Exception e){
			log.error("流水记录_导出到execl文档异常",e);
		}
		return ;
	}

}
