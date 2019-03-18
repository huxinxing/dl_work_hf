package com.bcb.controller;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.OperationsManager.ConfigCommonQuestionLangDto;
import com.bcb.bean.dto.OperationsManager.GoldExperienceDto;
import com.bcb.bean.dto.OperationsManager.InterstCouponsDto;
import com.bcb.service.OperationManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Api("运营管理相关接口")
@RestController
@RequestMapping("/operarions")
public class OperationsManagerController {

    @Autowired
    OperationManagerService operationManagerService;

    @ApiOperation("体验金_获取体验金列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "geId", value = "体验金id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0:关闭  1：已开启", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
            @RequestMapping(value = "/gelist", method = RequestMethod.POST)
    public ResponseResult gelist(HttpServletRequest request,@RequestParam Integer flag,@RequestParam(value = "geId",required =  false) Integer geId, @RequestParam  Integer pageSize,@RequestParam Integer pageNum) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.gelistService(flag,geId,pageNum,pageSize),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金项目获取失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金项目获取失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("体验金_新增体验金")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goldExperienceDto", value = "体验金对象", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/geGenerator", method = RequestMethod.POST)
    public ResponseResult generatorOrder(HttpServletRequest request, @RequestBody GoldExperienceDto goldExperienceDto) {
        try{
            if(ObjectUtils.isEmpty(goldExperienceDto)){
                log.error("新增对象为空;","goldExperienceDto" + goldExperienceDto.toString());
                return new ResponseResult(WorldValue.ERROR, "参数格式错误：" + goldExperienceDto.toString(),ResponseStatus.DATA_WRONG.getKey(),
                        ResponseStatus.DATA_WRONG.getValue());
            }
            if(StringUtils.isEmpty(goldExperienceDto.getGeAmount()) || StringUtils.isEmpty(goldExperienceDto.getCondition()) || StringUtils.isEmpty(goldExperienceDto.getExperienceLength() ) || StringUtils.isEmpty(goldExperienceDto.getExperienceProject())){
                log.error("参数格式错误","goldExperienceDto" + goldExperienceDto.toString());
                return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + goldExperienceDto.toString(),
                        ResponseStatus.DATA_WRONG.getValue());
            }

            if(StringUtils.isEmpty(goldExperienceDto.getValidityDay())){
                if(StringUtils.isEmpty(goldExperienceDto.getValidityBeginTime()) || StringUtils.isEmpty(goldExperienceDto.getValidityEndTime())){
                    log.error("体验时长设置错误","goldExperienceDto" + goldExperienceDto.toString());
                    return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + goldExperienceDto.toString(), ResponseStatus.DATA_WRONG.getValue());
                }
            }

            if(!StringUtils.isEmpty(goldExperienceDto.getValidityDay())){
                if(!StringUtils.isEmpty(goldExperienceDto.getValidityBeginTime()) || !StringUtils.isEmpty(goldExperienceDto.getValidityEndTime())){
                    log.error("体验时长设置错误","goldExperienceDto" + goldExperienceDto.toString());
                    return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + goldExperienceDto.toString(), ResponseStatus.DATA_WRONG.getValue());
                }
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.generatorOrderService(goldExperienceDto),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金创建失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金创建失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("体验金_发送体验金")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "geId", value = "体验金id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "geUserExecl", value = "指定用户execl文件", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "发送体验金用户类型 0 ： 新用户 1：表示全部用户  指定用户：2", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/geSend", method = RequestMethod.POST)
    public ResponseResult geSend(
            HttpServletRequest request,
            @RequestParam Integer flag,
            @RequestParam Integer geId,
            @RequestParam(value = "geUserExecl",required =  false) MultipartFile geUserExecl
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.geSendService(flag,geId,geUserExecl),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金发送失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金发送失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("体验金_获取体验项目")
    @LoginRequired
    @RequestMapping(value = "/gefinancing", method = RequestMethod.POST)
    public ResponseResult gefinancing(HttpServletRequest request) {
        try{
           return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.gefinancingService(),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金项目获取失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金项目获取失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("体验金_详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "geId", value = "体验金id", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/gedetail", method = RequestMethod.POST)
    public ResponseResult gedetail(HttpServletRequest request, @RequestParam int geId) {
        try{
           if(ObjectUtils.isEmpty(geId)){
               log.error("体验金id不能为空","geId" + geId);
               return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + "geId:" + geId,ResponseStatus.DATA_WRONG.getValue());
           }
           return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.gedetailService(geId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金修改失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金修改失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("体验金_修改体验金")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goldExperienceDto", value = "体验金对象", required = true, dataType = "String", paramType = "query") })
   @LoginRequired
    @RequestMapping(value = "/geUpdate", method = RequestMethod.POST)
    public ResponseResult geUpdate(HttpServletRequest request, @RequestBody GoldExperienceDto goldExperienceDto) {
        try{
            if(StringUtils.isEmpty(goldExperienceDto.getGeId())){
                log.error("体验金id不能为空","goldExperienceDto" + goldExperienceDto.getGeId());
                return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + "geId:" + goldExperienceDto.getGeId(),ResponseStatus.DATA_WRONG.getValue());
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.geUpdateService(goldExperienceDto),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金修改失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金修改失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("体验金_获取体验金用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "geId", value = "体验金id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "geStatus", value = "体验金状态", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/geUserList", method = RequestMethod.POST)
    public ResponseResult geUserList(HttpServletRequest request, @RequestParam(value = "geStatus",required = false) Integer geStatus, @RequestParam(value = "userId",required = false) Integer userId,@RequestParam  Integer pageSize,@RequestParam Integer pageNum,@RequestParam Integer geId) {
        try{
            if(StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageNum)){
                log.error("体验金id或用户id不能为空","geStatus:" + geStatus + " " + "userId" + userId );
                return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + "geStatus:" + geStatus + " " + "userId" + userId+ "pageSize:" + pageSize + " " + "pageNum" + pageNum ,ResponseStatus.DATA_WRONG.getValue());
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.geUserListService(userId,geStatus,pageSize,pageNum,geId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("体验金用户列表获取成功",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"体验金用户列表获取成功",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("体验金_导出指定用户模板")
    @RequestMapping(value = "/geExportExeclUser", method = RequestMethod.GET)
    public void geExportExeclUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try{
            HttpSession session = request.getSession();
            boolean strData = (boolean) session.getAttribute("isTrueExportExecl");
            if (!strData){
                return;
            }
            session.setAttribute("isTrueExportExecl", false);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("指定用户列表");

            String fileName = "geUser.xls";
            int rowNum = 1;
            String[] headers = {"用户ID"};
            HSSFRow row = sheet.createRow(0);
            for(int i=0;i<headers.length;i++){
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }

            for(int i = 0; i < 5; i++){
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.createCell(0).setCellValue("xxxx");
                rowNum++;
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.flushBuffer();
            workbook.write(response.getOutputStream());
            return;
        }catch (Exception e){
            log.error("体验金_导出到execl文档异常",e);
        }
        return ;
    }




    /////////////////////////////////////////////////////////////////////加息券


    @ApiOperation("加息券_获取加息券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "icId", value = "加息券id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0:关闭  1：已开启", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/iclist", method = RequestMethod.POST)
    public ResponseResult iclist(HttpServletRequest request,@RequestParam Integer flag,@RequestParam(value = "icId",required =  false) Integer icId, @RequestParam  Integer pageSize,@RequestParam Integer pageNum) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.iclistService(flag,icId,pageNum,pageSize),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("加息券项目获取失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"加息券项目获取失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("加息券_新增加息券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "InterstCouponsDto", value = "加息券对象", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/icGenerator", method = RequestMethod.POST)
    public ResponseResult icGenerator(HttpServletRequest request, @RequestBody InterstCouponsDto interstCouponsDto) {
        try{
            if(ObjectUtils.isEmpty(interstCouponsDto)){
                log.error("新增对象为空;","InterstCouponsDto" + interstCouponsDto.toString());
                return new ResponseResult(WorldValue.ERROR, "参数格式错误：" + interstCouponsDto.toString(),ResponseStatus.DATA_WRONG.getKey(),
                        ResponseStatus.DATA_WRONG.getValue());
            }
            if(StringUtils.isEmpty(interstCouponsDto.getIcRate()) || StringUtils.isEmpty(interstCouponsDto.getIcLength() ) || StringUtils.isEmpty(interstCouponsDto.getIcProject())){
                log.error("参数格式错误","InterstCouponsDto" + interstCouponsDto.toString());
                return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + interstCouponsDto.toString(),
                        ResponseStatus.DATA_WRONG.getValue());
            }

            if(StringUtils.isEmpty(interstCouponsDto.getIcValidityDay())){
                if(StringUtils.isEmpty(interstCouponsDto.getIcValidityBeginTime()) || StringUtils.isEmpty(interstCouponsDto.getIcValidityEndTime())){
                    log.error("体验时长设置错误","InterstCouponsDto" + interstCouponsDto.toString());
                    return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + interstCouponsDto.toString(), ResponseStatus.DATA_WRONG.getValue());
                }
            }

            if(!StringUtils.isEmpty(interstCouponsDto.getIcValidityDay())){
                if(!StringUtils.isEmpty(interstCouponsDto.getIcValidityBeginTime()) || !StringUtils.isEmpty(interstCouponsDto.getIcValidityEndTime())){
                    log.error("体验时长设置错误","InterstCouponsDto" + interstCouponsDto.toString());
                    return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + interstCouponsDto.toString(), ResponseStatus.DATA_WRONG.getValue());
                }
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.generatorIcOrderService(interstCouponsDto),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("加息券创建失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"加息券创建失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("加息券_发送加息券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "icId", value = "加息券id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "icUserExecl", value = "指定用户execl文件", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "发送加息券用户类型 0 ： 新用户 1：表示全部用户", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/icSend", method = RequestMethod.POST)
    public ResponseResult icSend(
            HttpServletRequest request,
            @RequestParam Integer flag,
            @RequestParam Integer icId,
            @RequestParam(value = "icUserExecl",required =  false) MultipartFile icUserExecl
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.icSendService(flag,icId,icUserExecl),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("加息券发送失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"加息券发送失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("加息券_获取加息券项目")
    @LoginRequired
    @RequestMapping(value = "/icfinancing", method = RequestMethod.POST)
    public ResponseResult icfinancing(HttpServletRequest request) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.gefinancingService(),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("加息券项目获取失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"加息券项目获取失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("加息券_详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "icId", value = "加息券id", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/icdetail", method = RequestMethod.POST)
    public ResponseResult icdetail(HttpServletRequest request, @RequestParam int icId) {
        try{
            if(ObjectUtils.isEmpty(icId)){
                log.error("加息券id不能为空","icId" + icId);
                return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + "icId:" + icId,ResponseStatus.DATA_WRONG.getValue());
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.icdetailService(icId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("加息券修改失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"加息券修改失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("加息券_修改加息券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goldExperienceDto", value = "加息券对象", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/icUpdate", method = RequestMethod.POST)
    public ResponseResult icUpdate(HttpServletRequest request, @RequestBody InterstCouponsDto interstCouponsDto) {
        try{
            if(StringUtils.isEmpty(interstCouponsDto.getIcId())){
                log.error("加息券id不能为空","InterstCouponsDto" + interstCouponsDto.getIcId());
                return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + "icId:" + interstCouponsDto.getIcId(),ResponseStatus.DATA_WRONG.getValue());
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.icUpdateService(interstCouponsDto),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("加息券修改失败",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"加息券修改失败",ResponseStatus.CODE_WRONG.getValue());
    }

    @ApiOperation("加息券_获取加息券用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "icId", value = "加息券id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "icStatus", value = "加息券状态", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query") })

    @LoginRequired
    @RequestMapping(value = "/icUserList", method = RequestMethod.POST)
    public ResponseResult icUserList(HttpServletRequest request, @RequestParam(value = "icStatus",required = false) Integer icStatus, @RequestParam(value = "userId",required = false) Integer userId,@RequestParam  Integer pageSize,@RequestParam Integer pageNum,@RequestParam Integer icId) {
        try{
            if(StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageNum)){
                log.error("加息券id或用户id不能为空","icStatus:" + icStatus + " " + "userId" + userId );
                return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(), "参数格式错误：" + "icStatus:" + icStatus + " " + "userId" + userId+ "pageSize:" + pageSize + " " + "pageNum" + pageNum ,ResponseStatus.DATA_WRONG.getValue());
            }
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.icUserListService(userId,icStatus,pageSize,pageNum,icId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("加息券用户列表获取成功",e);
        }
        return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),"加息券用户列表获取成功",ResponseStatus.CODE_WRONG.getValue());
    }


    @ApiOperation("加息券_导出指定用户模板")
    @RequestMapping(value = "/icExportExeclUser", method = RequestMethod.GET)
    public void icExportExeclUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try{
            HttpSession session = request.getSession();
            boolean strData = (boolean) session.getAttribute("isTrueExportExecl");
            if (!strData){
                return;
            }
            session.setAttribute("isTrueExportExecl", false);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("指定用户列表");

            String fileName = "icUser.xls";
            int rowNum = 1;
            String[] headers = {"用户ID"};
            HSSFRow row = sheet.createRow(0);
            for(int i=0;i<headers.length;i++){
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }

            for(int i = 0; i < 5; i++){
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.createCell(0).setCellValue("xxxx");
                rowNum++;
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.flushBuffer();
            workbook.write(response.getOutputStream());
            return ;
        }catch (Exception e){
            log.error("加息券_导出到execl文档异常",e);
        }
        return;
    }


    @ApiOperation("导出execl文件验证")
    @LoginRequired
    @RequestMapping(value = "/isTrueExportExecl", method = RequestMethod.GET)
    public ResponseResult isTrueExportExecl(
            HttpServletRequest request,
            HttpServletResponse response

    ) {
        HttpSession session = request.getSession();
        session.setAttribute("isTrueExportExecl", true);
        return new ResponseResult(WorldValue.SUCCESS, ResponseStatus.SUCCESS.getKey(), "用户验证成功", ResponseStatus.SUCCESS.getValue());
    }

    @ApiOperation("首页配置_轮播图列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "轮播图状态:1、已上架 2、已下架", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deviceType", value = "设备类型：分别是 '-'、'android'、'ios'  注：不限端口时不传输这设备类型参数", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/hcBannerList", method = RequestMethod.POST)
    public ResponseResult hcBannerList(
            HttpServletRequest request,
            @RequestParam(value = "status",required = false) Integer status,
            @RequestParam(value = "deviceType",required = false) String deviceType,
            @RequestParam(value = "beginTime",required = false) String beginTime,
            @RequestParam(value = "endTime",required = false) String endTime,
            @RequestParam  Integer pageSize,
            @RequestParam Integer pageNum) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcBannerListService(status,deviceType,beginTime,endTime,pageSize,pageNum),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_轮播图列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("首页配置_轮播图新增")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bannerPic", value = "轮播图图片", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "有效开始时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "有效结束时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contentUrl", value = "跳转url", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "轮播图标题", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "weight", value = "权重", required = true, dataType = "String", paramType = "query")
            })
    @LoginRequired
    @RequestMapping(value = "/hcBannerGenerate", method = RequestMethod.POST)
    public ResponseResult hcBannerGenerate(
            HttpServletRequest request,
            @RequestParam(value = "beginTime") String beginTime,
            @RequestParam(value = "endTime") String endTime,
            @RequestParam(value = "contentUrl",required = false) String contentUrl,
            @RequestParam(value = "weight",required = false) Integer weight,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "bannerPic",required = false) MultipartFile bannerPic) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcBannerGenerateService(contentUrl,weight,beginTime,endTime,title,bannerPic),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_轮播图生成成功",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }


    @ApiOperation("首页配置_轮播图详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "类型id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/hcBannerDetail", method = RequestMethod.POST)
    public ResponseResult hcBannerDetail(
            HttpServletRequest request,
            @RequestParam(value = "typeId") Integer typeId) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcBannerDetailService(typeId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_轮播图生成成功",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("首页配置_轮播图上架/下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "是否已上架：1、已上架  2、已下架", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "typeId", value = "轮播图id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/hcBannerUD", method = RequestMethod.POST)
    public ResponseResult hcBannerUD(
            HttpServletRequest request,
            @RequestParam(value = "status",required = true) Integer status,
            @RequestParam(value = "typeId",required = true) Integer typeId
            ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcBannerUDService(typeId,status),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_轮播图列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("首页配置_轮播图修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "轮播图id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "bannerPic", value = "轮播图图片", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "有效开始时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "有效结束时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contentUrl", value = "跳转url", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "weight", value = "权重", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/hcBannerupdate", method = RequestMethod.POST)
    public ResponseResult hcBannerupdate(
            HttpServletRequest request,
            @RequestParam(value = "typeId") Integer typeId,
            @RequestParam(value = "beginTime",required = false) String beginTime,
            @RequestParam(value = "endTime",required = false) String endTime,
            @RequestParam(value = "contentUrl",required = false) String contentUrl,
            @RequestParam(value = "weight",required =  false) Integer weight,
            @RequestParam(value = "title",required =  false) String title,
            @RequestParam(value = "bannerPic",required =  false) MultipartFile bannerPic
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcBannerupdateService(typeId,beginTime,endTime,contentUrl,weight,title,bannerPic),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_轮播图列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }



    @ApiOperation("首页配置_体验金列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "是否已上架:1、已上架 2、未上架", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/hcGeList", method = RequestMethod.POST)
    public ResponseResult hcGeList(
            HttpServletRequest request,
            @RequestParam(value = "status",required = false) String status,
            @RequestParam  Integer pageSize,
            @RequestParam Integer pageNum) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcGeListService(status,pageSize,pageNum),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_体验金列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }


    @ApiOperation("首页配置_新增体验金")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "geId", value = "体验金id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/hcGeGenerate", method = RequestMethod.POST)
    public ResponseResult hcGeGenerate(
            HttpServletRequest request,
            @RequestParam(value = "geId",required = false) Integer geId
            ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcGeGenerateService(geId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_新增体验金失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }


    @ApiOperation("首页配置_获取体验金项目")
    @LoginRequired
    @RequestMapping(value = "/hcGe", method = RequestMethod.POST)
    public ResponseResult hcGe(
            HttpServletRequest request
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcGeService(),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取体验金项目失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }


    @ApiOperation("首页配置_体验金上架下架状态修改")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(name = "geId", value = "体验金id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "是否已上架:1、已上架 2、未上架", required = true, dataType = "String", paramType = "query") })
    @RequestMapping(value = "/hcGeUD", method = RequestMethod.POST)
    public ResponseResult hcGeUD(
            HttpServletRequest request,
            @RequestParam(value = "geId",required = false) Integer geId,
            @RequestParam(value = "status",required = false) Integer status
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcGeUDService(geId,status),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取体验金项目失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }








    @ApiOperation("首页配置_新人见面礼图片列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数据量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "是否已上架:1、已上架 2、未上架", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/hcNewManPicList", method = RequestMethod.POST)
    public ResponseResult hcNewManPicList(
            HttpServletRequest request,
            @RequestParam(value = "status",required = false) Integer status,
            @RequestParam  Integer pageSize,
            @RequestParam Integer pageNum) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcNewManPicListService(status,pageSize,pageNum),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_新人见面礼图片列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("首页配置_新人见面礼图片新增")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newManPic", value = "新人见面礼图片", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "新人见面礼土图片名称", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/hcNewManPicGenerator", method = RequestMethod.POST)
    public ResponseResult hcNewManPicGenerator(
            HttpServletRequest request,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "newManPic") MultipartFile newManPic) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcNewManPicGeneratorService(title,newManPic),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_新人见面礼图片列表获取失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("首页配置_新人见面礼图片上架/下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hcNewManId", value = "新人见面礼图图片id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "是否已上架:1、已上架 2、未上架", required = true, dataType = "String", paramType = "query") })
    @LoginRequired
    @RequestMapping(value = "/hcNewManPicUD", method = RequestMethod.POST)
    public ResponseResult hcNewManPicUD(
            HttpServletRequest request,
            @RequestParam(value = "hcNewManId",required = false) Integer hcNewManId,
            @RequestParam(value = "status",required = false)  Integer status
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.hcNewManPicUDService(hcNewManId,status),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_新人见面礼图片上架或者下架失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }


    //////////////////////////////////////////////////////////////////运营管理_常见问题配置///////////////////////////////////////////////////////////////

    @ApiOperation("常见问题配置_常见问题配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ccqType", value = "常见问题配置类型类型0、全部;1、登录注册；2、账户安全 3、充值投资 4、提现 5、费用 6、资产", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页长度", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/ccqlist", method = RequestMethod.POST)
    public ResponseResult ccqlist(
            HttpServletRequest request,
            @RequestParam(value = "ccqType") Integer ccqType,
            @RequestParam(value = "pageNum") Integer pageNum,
            @RequestParam(value = "pageSize") Integer pageSize
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqlistService(ccqType,pageNum,pageSize),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取常见配置列表失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }


    @ApiOperation("常见问题配置_新增常见问题配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ccqType", value = "常见问题配置类型类型1、登录注册；2、账户安全 3、充值投资 4、提现 5、费用 6、资产", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ccqTitle", value = "常见问题标题", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ccqQuesttion", value = "常见问题内容", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ccqWeight", value = "权重", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/ccqGenerator", method = RequestMethod.POST)
    public ResponseResult ccqGenerator(
            HttpServletRequest request,
            @RequestParam(value = "ccqType") Integer ccqType,
            @RequestParam(value = "ccqTitle") String ccqTitle,
            @RequestParam(value = "ccqQuesttion") String ccqQuesttion,
            @RequestParam(value = "ccqWeight") Integer ccqWeight
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqGeneratorService(ccqType,ccqTitle,ccqQuesttion,ccqWeight),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_新增常见配置列表失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("常见问题配置_常见问题类型")
    @LoginRequired
    @RequestMapping(value = "/ccqType", method = RequestMethod.POST)
    public ResponseResult ccqGenerator(
            HttpServletRequest request
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqTypeService(),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取常见问题类型失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("常见问题配置_常见问题详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ccqId", value = "常见问题id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/ccqDetail", method = RequestMethod.POST)
    public ResponseResult ccqDetail(
            HttpServletRequest request,
            @RequestParam(value = "ccqId") Integer ccqId
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqDetailService(ccqId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取常见问题类型失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("常见问题配置_更新问题详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ccqId", value = "常见问题id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ccqType", value = "常见问题配置类型类型1、登录注册；2、账户安全 3、充值投资 4、提现 5、费用 6、资产", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ccqTitle", value = "常见问题标题", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ccqQuesttion", value = "常见问题内容", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ccqWeight", value = "权重", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/ccqUpdate", method = RequestMethod.POST)
    public ResponseResult ccqUpdate(
            HttpServletRequest request,
            @RequestParam(value = "ccqId") Integer ccqId,
            @RequestParam(value = "ccqType",required = false) Integer ccqType,
            @RequestParam(value = "ccqTitle",required = false) String ccqTitle,
            @RequestParam(value = "ccqQuesttion" ,required = false) String ccqQuesttion,
            @RequestParam(value = "ccqWeight",required = false) Integer ccqWeight
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqUpdateService(ccqId,ccqType,ccqTitle,ccqQuesttion,ccqWeight),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_更新常见问题类型失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("常见问题配置_删除常见问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ccqId", value = "常见问题id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/ccqDelete", method = RequestMethod.POST)
    public ResponseResult ccqDelete(
            HttpServletRequest request,
            @RequestParam(value = "ccqId") Integer ccqId
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqDeleteService(ccqId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取常见问题类型失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("常见问题配置_支持的语言列表")
    @LoginRequired
    @RequestMapping(value = "/ccqLangClassification", method = RequestMethod.POST)
    public ResponseResult ccqLangClassification(
            HttpServletRequest request
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqLangClassificationService(),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取获取支持语言列表失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

    @ApiOperation("常见问题配置_已经翻译过的语言列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ccqId", value = "常见问题id", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/ccqLangList", method = RequestMethod.POST)
    public ResponseResult ccqLangList(
            HttpServletRequest request,
            @RequestParam(value = "ccqId") Integer ccqId
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqLangListService(ccqId),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取常见问题类型失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }


    @ApiOperation("常见问题配置_修改翻译内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "configCommonQuestionLangDtoList", value = "常见问题返参对象", required = true, dataType = "String", paramType = "query")
    })
    @LoginRequired
    @RequestMapping(value = "/ccqUpateLang", method = RequestMethod.POST)
    public ResponseResult ccqUpateLang(
            HttpServletRequest request,
            @RequestBody List<ConfigCommonQuestionLangDto> configCommonQuestionLangDtoList
    ) {
        try{
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),operationManagerService.ccqUpateLangService(configCommonQuestionLangDtoList),ResponseStatus.SUCCESS.getValue());
        }catch (Exception e){
            log.error("首页配置_获取常见问题类型失败",e);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),e,ResponseStatus.CODE_WRONG.getValue());
        }
    }

}
