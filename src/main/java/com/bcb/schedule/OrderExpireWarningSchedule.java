package com.bcb.schedule;

import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.SystemProperties;
import com.bcb.config.RedisClient;
import com.bcb.domain.entity.FinancingBaseInfo;
import com.bcb.domain.entity.FinancingDailyRate;
import com.bcb.domain.entity.SystemConfiguration;
import com.bcb.domain.entity.UserFinancingRecord;
import com.bcb.domain.repository.FinancingBaseInfoRepository;
import com.bcb.domain.repository.FinancingDailyRateRepository;
import com.bcb.domain.repository.SystemConfigurationRepository;
import com.bcb.domain.repository.UserFinancingRecordRepository;
import com.bcb.util.DateUtil;
import com.bcb.util.HttpPostUtil;
import com.bcb.util.MD5Util;
import com.bcb.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class OrderExpireWarningSchedule {

    @Autowired
    SystemProperties systemProperties;

    @Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    FinancingDailyRateRepository financingDailyRateRepository;

    @Autowired
    FinancingBaseInfoRepository financingBaseInfoRepository;

    @Autowired
    SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    RedisClient redisClient;


 //   @Scheduled(fixedDelay = 5 * 60 * 1000)   // 5 分钟执行一次消息预警
    @Scheduled(cron = "00 00 01 * * ?")   //凌晨 1 点进行当日需结算订单消息推送
    public void orderExpireWaringSendMsgToTelegram() throws Exception {
        log.debug("启动定时任务：对当日需结算订单发送预警消息到电报群");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(telegramMesageToday(DateUtil.DateZero(new Date()), DateUtil.DateTweentyFour(new Date())));
        stringBuilder.append(telegramMesageAfter(DateUtil.TimeAfterPath(1), DateUtil.TimeAfterPath(2)));     //明日到期订单预警信息
        stringBuilder.append(telegramMesageAfter(DateUtil.TimeAfterPath(0), DateUtil.TimeAfterPath(10)));    //近10日订单预警信息
        stringBuilder.append(telegramMesageAfter(DateUtil.TimeAfterPath(0), DateUtil.TimeAfterPath(20)));    //近20日订单预警信息
        stringBuilder.append(telegramMesageAfter(DateUtil.TimeAfterPath(0), DateUtil.TimeAfterPath(30)));    //近30日订单预警信息
        log.info(stringBuilder.toString());
        if (ObjectUtils.isEmpty(stringBuilder)) {
            log.debug("今日没有理财到期产品");
            return;
        }
         TelegramUtil.sendAlertMsg(systemProperties.getTelegramChatId(), stringBuilder.toString());
    }

    public String telegramMesageToday(Date beiginTime, Date endTime) throws Exception {

        Map<String, String> msgMap = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String day = simpleDateFormat.format(new Date());
        msgMap.put("todayTime", day);
        Map<String, Map<String, String>> financingMap = new HashMap<>();

        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByName(SystemConfiguration.TAX_RATE);

        List<UserFinancingRecord> list = userFinancingRecordRepository.findByRecordStatusAndExpireTimeBetween(UserFinancingRecord.RECORD_STATUS_ONGOING, Timestamp.valueOf(DateUtil.DateToStr(beiginTime, "yyyy-MM-dd HH:mm:ss")), Timestamp.valueOf(DateUtil.DateToStr(endTime, "yyyy-MM-dd HH:mm:ss")));

        if (CollectionUtils.isEmpty(list)) {
            log.debug("今日没有理财到期的订单");
            return "今日没有理财到期的订单\n";
        }
        msgMap.put("recordNum", list.size() + "");

        FinancingDailyRate financingDailyRate = financingDailyRateRepository.findFirstByOrderByCreateTimeDesc();
        msgMap.put("bcb2usdx", financingDailyRate.getBcb2Usdx().setScale(3, BigDecimal.ROUND_HALF_UP).toString());

        BigDecimal returnBcbTotal = BigDecimal.ZERO;
        msgMap.put("minRate", "0");
        msgMap.put("maxRate", "0");

        for (UserFinancingRecord userFinancingRecord : list) {

            BigDecimal totalRate = financingDailyRate.getBcb2Usdx().divide(userFinancingRecord.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal fixRate = (userFinancingRecord.getAnnualRate().subtract(userFinancingRecord.getServiceRate())).multiply(getActualRatio(userFinancingRecord.getFinancingUuid()));

            BigDecimal returnBcb = (fixRate.add(BigDecimal.valueOf(1))).multiply(userFinancingRecord.getUsdxAmount()).divide(financingDailyRate.getBcb2Usdx(), 9, BigDecimal.ROUND_HALF_DOWN);

            if (totalRate.compareTo(fixRate.add(BigDecimal.valueOf(1)).add(userFinancingRecord.getServiceRate())) > 0) {  //如果总收益率大于保底收益率，存在额外收益率
                BigDecimal extraUSDX = totalRate.subtract(fixRate.add(BigDecimal.valueOf(1)).add(userFinancingRecord.getServiceRate())).multiply(userFinancingRecord.getUsdxAmount());
                BigDecimal scUsdx = extraUSDX.subtract(extraUSDX.multiply(userFinancingRecord.getFoundationCommissionRate()));
                returnBcb = returnBcb.add(scUsdx.subtract(scUsdx.multiply(userFinancingRecord.getSuperiorCommissionRate()).multiply(new BigDecimal(systemConfiguration.getValue()))));
            }

            if (msgMap.get("minRate").equals("0") || new BigDecimal(msgMap.get("minRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) > 0) {
                msgMap.put("minRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
            }

            if (msgMap.get("maxRate").equals("0") || new BigDecimal(msgMap.get("maxRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) < 0) {
                msgMap.put("maxRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
            }

            returnBcbTotal = returnBcbTotal.add(returnBcb);

            ////////////////////////////////////////按照项目统计返回消息

            if (StringUtils.isEmpty(financingMap.get(userFinancingRecord.getFinancingUuid()))) {
                Map<String, String> map = new HashMap<>();
                map.put("financingReturnBCB", returnBcb.toString());
                map.put("financingMinRate", "0");
                map.put("financingMaxRate", "0");
                if (map.get("financingMinRate").equals("0") || new BigDecimal(map.get("financingMinRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) > 0) {
                    map.put("financingMinRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
                }

                if (map.get("financingMaxRate").equals("0") || new BigDecimal(map.get("financingMaxRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) < 0) {
                    map.put("financingMaxRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
                }
                financingMap.put(userFinancingRecord.getFinancingUuid(), map);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("financingReturnBCB", new BigDecimal(map.get("financingReturnBCB")).add(returnBcb).toString());
                if (map.get("financingMinRate").equals("0") || new BigDecimal(map.get("financingMinRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) > 0) {
                    map.put("financingMinRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
                }

                if (map.get("financingMaxRate").equals("0") || new BigDecimal(map.get("financingMaxRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) < 0) {
                    map.put("financingMaxRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
                }
                financingMap.put(userFinancingRecord.getFinancingUuid(), map);
            }


        }
        msgMap.put("returnBcbTotal", returnBcbTotal.toString());
        StringBuilder returnStr = new StringBuilder();
        returnStr.append("数字谷理财到期提醒：" + msgMap.get("todayTime") + "日共有" + msgMap.get("recordNum") + "笔理财到期，理财到期结算BCB价格" + msgMap.get("bcb2usdx") + "USDX，理财返币总额" + msgMap.get("returnBcbTotal") + "BCB，理财结算收益率：" + msgMap.get("minRate") + "%~" + msgMap.get("maxRate") + "%\n");
        for (Map.Entry<String, Map<String, String>> entry : financingMap.entrySet()) {
            FinancingBaseInfo financingBaseInfo = financingBaseInfoRepository.findOneByFinancingUuid(entry.getKey());
            if (ObjectUtils.isEmpty(financingBaseInfo)) {
                continue;
            }
            returnStr.append(financingBaseInfo.getTitle() + "理财项目：理财返币总额" + entry.getValue().get("financingReturnBCB") + " BCB，理财实际结算收益率：" + entry.getValue().get("financingMinRate") + "%~" + entry.getValue().get("financingMaxRate") + "%\n");
        }
        return returnStr.toString();
    }

    public String telegramMesageAfter(Date beiginTime, Date endTime) throws Exception {
        Map<String, String> msgMap = new HashMap<>();
        if (DateUtil.surplusDay(beiginTime, endTime) == 1) {
            msgMap.put("todayTime", DateUtil.DateToStr(beiginTime, "yyyy-MM-dd") + "日");
        } else {
            msgMap.put("todayTime", "近" + DateUtil.surplusDay(beiginTime, endTime).toString() + "天内");
        }

        List<UserFinancingRecord> list = userFinancingRecordRepository.findByRecordStatusAndExpireTimeBetween(UserFinancingRecord.RECORD_STATUS_ONGOING, Timestamp.valueOf(DateUtil.DateToStr(beiginTime, "yyyy-MM-dd HH:mm:ss")), Timestamp.valueOf(DateUtil.DateToStr(endTime, "yyyy-MM-dd HH:mm:ss")));
        if (CollectionUtils.isEmpty(list)) {
            return msgMap.get("todayTime")+"没有到期订单\n";
        }
        msgMap.put("recordNum", list.size() + "");
        String BCB2USDX = redisClient.get("BCB_USDX");
        BigDecimal totalBCB = BigDecimal.ZERO;
        BigDecimal totalBcb2Usdx = BigDecimal.ZERO;
        BigDecimal totalFixed = BigDecimal.ZERO;
        msgMap.put("minRate", "0");
        msgMap.put("maxRate", "0");
        for (UserFinancingRecord userFinancingRecord : list) {
            totalBCB = totalBCB.add(userFinancingRecord.getBcbAmount());
            totalBcb2Usdx = totalBcb2Usdx.add(userFinancingRecord.getBcb2usdx());
            totalFixed = totalFixed.add((userFinancingRecord.getAnnualRate().subtract(userFinancingRecord.getServiceRate())).multiply(getActualRatio(userFinancingRecord.getFinancingUuid())).multiply(userFinancingRecord.getUsdxAmount()).divide(new BigDecimal(BCB2USDX), 9, BigDecimal.ROUND_HALF_DOWN));
            BigDecimal totalRate = new BigDecimal(BCB2USDX).divide(userFinancingRecord.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_DOWN);
            if (msgMap.get("minRate").equals("0") || new BigDecimal(msgMap.get("minRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) > 0) {
                msgMap.put("minRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
            }

            if (msgMap.get("maxRate").equals("0") || new BigDecimal(msgMap.get("maxRate")).compareTo(totalRate.subtract(BigDecimal.valueOf(1))) < 0) {
                msgMap.put("maxRate", totalRate.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        msgMap.put("totalBCB", totalBCB.toString());
        msgMap.put("totalFixed", totalFixed.toString());
        if (list.size() == 0) {
            msgMap.put("avgBcb2Usdx", "0");
        } else {
            msgMap.put("avgBcb2Usdx", totalBcb2Usdx.divide(BigDecimal.valueOf(list.size()), 4, BigDecimal.ROUND_HALF_DOWN).toString());
        }
        return msgMap.get("todayTime") + "共有" + msgMap.get("recordNum") + "笔理财到期，理财投资总额" + msgMap.get("totalBCB") + "BCB，固定收益" + msgMap.get("totalFixed") + " BCB,理财平均价格" + msgMap.get("avgBcb2Usdx") + "USDX，当前理财实际收益率:" + msgMap.get("minRate") + "~%" + msgMap.get("maxRate") + "%\n";
    }

    /**
     * 获取年化率和实际比例的比例：理财期限6个月，实际比例是0.5
     *
     * @return
     */
    private BigDecimal getActualRatio(String financingUuid) throws Exception {
        FinancingBaseInfo info = financingBaseInfoRepository.findOneByFinancingUuid(financingUuid);
        if (info.getFreezeUnit().equals("年")) {
            return BigDecimal.valueOf(info.getFreezeNumber());
        } else if (info.getFreezeUnit().equals("月")) {
            return BigDecimal.valueOf(info.getFreezeNumber()).divide(new BigDecimal("12"));
        } else if (info.getFreezeUnit().equals("日")) {
            return BigDecimal.valueOf(info.getFreezeNumber()).divide(new BigDecimal("365"));
        } else if (info.getFreezeUnit().equals("周")) {
            return BigDecimal.valueOf(info.getFreezeNumber()).divide(new BigDecimal("52"));
        } else {
            throw new Exception("不支持的时间周期：" + info.getFreezeUnit());
        }
    }

}
