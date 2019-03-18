package com.bcb.service;

import com.bcb.annotation.ActionType;
import com.bcb.bean.dto.User.FinancingUser;
import com.bcb.domain.dao.TradeDao;
import com.bcb.domain.entity.*;
import com.bcb.domain.repository.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserAccountInfoRepository userAccountInfoRepository;

    @Autowired
    UserWalletInfoRepository userWalletInfoRepository;

    @Autowired
    SystemLogOperationRepository systemLogOperationRepository;

    @Autowired
    TradeDao tradeDao;

    @Autowired
    CommonService commonService;

    @Autowired
    SystemLoginMenuRepository systemLoginMenuRepository;

    @Autowired
    SystemRolePermissionRepository systemRolePermissionRepository;

    public PageInfo<FinancingUser> userListService(String condition, Integer pageSize, Integer pageNum,SystemLoginAccount account) throws Exception {
        boolean phonePermession = false;
        SystemLoginMenu systemLoginMenu = systemLoginMenuRepository.findFirstByMenuKey("checkMobileNumber");
        SystemRolePermission systemRolePermission = systemRolePermissionRepository.findFirstByMenuIdAndRoleId(systemLoginMenu.getId(),account.getRole());
        if(!ObjectUtils.isEmpty(systemRolePermission)){
            phonePermession = true;
        }
        PageInfo<FinancingUser> pageInfo = new PageInfo<>();

        Map<String, Object> userMap = tradeDao.queryUserTreeDatas((pageNum - 1) * pageSize, pageSize, condition);

        Integer total = (Integer) userMap.get("num");
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) userMap.get("items");
        List<FinancingUser> financingUserList = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            FinancingUser financingUser = new FinancingUser();
            financingUser.setUserId(ObjectUtils.isEmpty(map.get("userId")) ? "_" : map.get("userId").toString());
            financingUser.setCodeId(ObjectUtils.isEmpty(map.get("codeId")) ? "-" : map.get("codeId").toString());
            financingUser.setDisplayName(ObjectUtils.isEmpty(map.get("displayName")) ? "-" : map.get("displayName").toString());
            financingUser.setStep(ObjectUtils.isEmpty(map.get("step")) ? "-" : map.get("step").toString());
            financingUser.setFinancingScale(ObjectUtils.isEmpty(map.get("financingScale")) ? "-" : map.get("financingScale").toString());
            financingUser.setRegistTime(ObjectUtils.isEmpty(map.get("registTime")) ? "-" : map.get("registTime").toString());
            financingUser.setCheckPhonePermission(phonePermession?"1":"0");
            financingUserList.add(financingUser);
        }

        pageInfo.setTotal(total);
        pageInfo.setList(financingUserList);
        return pageInfo;

    }


    public String checkMobileNumberService(Integer userId,SystemLoginAccount account) throws Exception {
        SystemLoginMenu systemLoginMenu = systemLoginMenuRepository.findFirstByMenuKey("checkMobileNumber");
        SystemRolePermission systemRolePermission = systemRolePermissionRepository.findFirstByMenuIdAndRoleId(systemLoginMenu.getId(),account.getRole());
        if(!ObjectUtils.isEmpty(systemRolePermission)){
            throw new Exception(account.getLoginName() + "没有权限查询用户电话号码");
        }
        UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(userId);
        if (ObjectUtils.isEmpty(userAccountInfo)) {
            return "-";
        }
        return "+" + userAccountInfo.getCountryCode() + " " + userAccountInfo.getMobileNumber();
    }

    public Map<String,String> checkWalletService(Integer userId) {
        Map<String,String> map = new HashMap<>();
        List<UserWalletInfo> userWalletInfoList = userWalletInfoRepository.findByUserIdAndStatusNot(userId,UserWalletInfo.WALLET_STATUS_INVALID);
        if (CollectionUtils.isEmpty(userWalletInfoList)) {
            log.info("用户钱包未绑定");
            return null;
        }
        for (UserWalletInfo userWalletInfo : userWalletInfoList) {
            String strCoin = userWalletInfo.getToken().substring(0, 2);
            if (strCoin.equals("0x")) {
                map.put("ETH",userWalletInfo.getToken());
            } else if (strCoin.equals("bc")) {
                map.put("BCB",userWalletInfo.getToken());
            }
        }
        return map;
    }

    @Transactional
    public List<String> settingUserMessageService(String financingScale, String userDiscountRate, String parentId, Integer userId, SystemLoginAccount account) throws Exception {
        List<String> resultList = new ArrayList<>();
        UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(userId);
        Integer newParentId = null;
        BigDecimal newFinancingScale = null;
        BigDecimal newUserDisCountRate = null;

        if(!ObjectUtils.isEmpty(financingScale)&&financingScale.length()!=0){
            newFinancingScale = new BigDecimal(financingScale);
        }

        if (!ObjectUtils.isEmpty(parentId)&&parentId.length()!=0){
            newParentId = Integer.parseInt(parentId);
        }

        if(!ObjectUtils.isEmpty(userDiscountRate)&&userDiscountRate.length()!=0){
            newUserDisCountRate = new BigDecimal(userDiscountRate);
        }

        if(!ObjectUtils.isEmpty(newParentId)){
            if(newParentId.equals(userId)){
                throw new Exception("代理上级：不允许将自己设置成自己的上级");
            }
            if (!newParentId.equals(0)){
                UserAccountInfo parentUserAccountInfo = userAccountInfoRepository.findByUserId(newParentId);
                if (ObjectUtils.isEmpty(parentUserAccountInfo)){
                    throw new Exception("代理上级:代理上级不存在:parentId=" + newParentId);
                }
                Integer parentIds = parentUserAccountInfo.getParentId();
                while (parentIds != null) {
                    if (parentIds.equals(userId)) {
                        // 出现循环
                        throw new Exception("代理上级:不允许将原来的下级设为自己的上级");
                    }
                    UserAccountInfo                                    info = userAccountInfoRepository.findByUserId(parentIds);
                    if (info == null) {
                        break;
                    }

                    if (info.getParentId() == null) {
                        break;
                    }
                    parentIds = info.getParentId();
                }
                if (!ObjectUtils.isEmpty(newFinancingScale)){
                    if (newFinancingScale.compareTo(parentUserAccountInfo.getFinancingScale()) > 0){
                        throw new Exception("代理上级:本级代理返点大于父级代理返点                                                                                                                                                  ("+parentUserAccountInfo.getFinancingScale().setScale(1,BigDecimal.ROUND_HALF_UP)+")" );
                    }
                }else {
                    if (userAccountInfo.getFinancingScale().compareTo(parentUserAccountInfo.getFinancingScale()) > 0) {
                        throw new Exception("代理上级:本级代理返点大于父级代理返点("+parentUserAccountInfo.getFinancingScale().setScale(1,BigDecimal.ROUND_HALF_UP)+")");
                    }
                }



                if (!ObjectUtils.isEmpty(newFinancingScale)){
                    //判断是否有上级
                    if(newFinancingScale.compareTo(BigDecimal.valueOf(5)) > 0 || newFinancingScale.compareTo(BigDecimal.valueOf(0)) < 0){
                        throw new Exception("返点比列：数值能不能大于5小于0");
                    }
                    Integer parentIdss = userAccountInfo.getParentId();
                    if (!ObjectUtils.isEmpty(parentIdss) && parentIdss.compareTo(0) > 0) {
                        UserAccountInfo parentAccountInfo = userAccountInfoRepository.findByUserId(parentIdss);
                        BigDecimal parentFinancingScale = parentAccountInfo.getFinancingScale();
                        if (newFinancingScale.compareTo(parentFinancingScale) > 0) {
                            throw new Exception("返点比列:设置的返点比例比上级的返点比例("+parentAccountInfo.getFinancingScale().setScale(1,BigDecimal.ROUND_HALF_UP)+")高");
                        }
                    }
                    //判断是否有下级
                    List<UserAccountInfo> ls = userAccountInfoRepository.findByParentId(userId);
                    if (!CollectionUtils.isEmpty(ls)) {
                        for (UserAccountInfo lai : ls) {
                            if (newFinancingScale.compareTo(lai.getFinancingScale()) < 0) {
                                throw new Exception("返点比列:设置的返点比例比下级的返点比例("+lai.getFinancingScale().setScale(1,BigDecimal.ROUND_HALF_UP)+")低");
                            }
                        }
                    }
                }
            }
        }

        if(!ObjectUtils.isEmpty(newUserDisCountRate)){
            if(newUserDisCountRate.compareTo(BigDecimal.valueOf(100)) > 0 || newUserDisCountRate.compareTo(BigDecimal.valueOf(0)) < 0){
                throw new Exception("用户产品折扣率：数值能不能大于100小于0");
            }
        }

        if(!ObjectUtils.isEmpty(newFinancingScale)){
            userAccountInfo.setFinancingScale(newFinancingScale);
            SystemLogOperation tempLog = new SystemLogOperation();
            Date now = new Date();
            tempLog.setCreateTime(now);
            tempLog.setCommentDescribe("用户管理");
            tempLog.setOperatorName(ObjectUtils.isEmpty(account)?"-":account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            tempLog.setParameterContext("返点设置" + ":" + "用户ID" + userAccountInfo.getUserId() + "，返点比例从" + userAccountInfo.getFinancingScale() + "修改为" + newFinancingScale);
            systemLogOperationRepository.save(tempLog);
        }

        if (!ObjectUtils.isEmpty(newParentId)){
            userAccountInfo.setParentId(newParentId.equals(0)?null:newParentId);
            SystemLogOperation tempLog = new SystemLogOperation();
            Date now = new Date();
            tempLog.setCreateTime(now);
            tempLog.setCommentDescribe("用户管理");
            tempLog.setOperatorName(ObjectUtils.isEmpty(account)?"-":account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            if (userAccountInfo.getParentId() != null && !ObjectUtils.isEmpty(userAccountInfo.getParentId().toString()) && !"0".equalsIgnoreCase(userAccountInfo.getParentId().toString())) {
                tempLog.setParameterContext("更换上级代理" + ":" + "用户ID" + userAccountInfo.getUserId() + "，上级代理从" + userAccountInfo.getParentId() + "修改为" + newParentId);
            } else {
                tempLog.setParameterContext("更换上级代理" + ":" + "用户ID" + userAccountInfo.getUserId() + "，上级代理从" + "无" + "修改为" + newParentId);
            }
            systemLogOperationRepository.save(tempLog);
        }

        if(!ObjectUtils.isEmpty(newUserDisCountRate)){
            userAccountInfo.setUserDiscountRate(newUserDisCountRate);
            SystemLogOperation tempLog = new SystemLogOperation();
            Date now = new Date();
            tempLog.setCreateTime(now);
            tempLog.setCommentDescribe("用户管理");
            tempLog.setOperatorName(ObjectUtils.isEmpty(account)?"-":account.getLoginName());
            tempLog.setActionType(ActionType.UPDATE.getText());
            tempLog.setParameterContext("用户折扣率设置" + ":" + "用户ID" + userAccountInfo.getUserDiscountRate() + "，用户折扣率比例从" + userAccountInfo.getUserDiscountRate() + "修改为" + newFinancingScale);
            systemLogOperationRepository.save(tempLog);
        }

        userAccountInfoRepository.save(userAccountInfo);
        resultList.add("设置成功");
        return resultList;
    }

    public Map<String, String> userMessageService(Integer userId) {
        Map<String, String> map = new HashMap<>();
        UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(userId);
        map.put("financingScale", ObjectUtils.isEmpty(userAccountInfo.getFinancingScale()) ? null : userAccountInfo.getFinancingScale().setScale(1,BigDecimal.ROUND_HALF_DOWN).toString());
        map.put("userDiscountRate", ObjectUtils.isEmpty(userAccountInfo.getUserDiscountRate()) ? null : userAccountInfo.getUserDiscountRate().setScale(1,BigDecimal.ROUND_HALF_DOWN).toString());
        map.put("parentId", ObjectUtils.isEmpty(userAccountInfo.getParentId()) ? null : userAccountInfo.getParentId().toString());
        return map;
    }
}

