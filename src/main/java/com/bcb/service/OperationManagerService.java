package com.bcb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.SystemProperties;
import com.bcb.bean.dto.OperationsManager.*;
import com.bcb.domain.entity.*;
import com.bcb.domain.repository.*;
import com.bcb.util.FastDFSUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OperationManagerService {

    @Autowired
    FinancingActivityGoldexperienceRepository financingActivityGoldexperienceRepository;

    @Autowired
    UserGoldexperienceRelationRepository userGoldexperienceRelationRepository;

    @Autowired
    FinancingBaseInfoRepository financingBaseInfoRepository;

    @Autowired
    UserAccountInfoRepository userAccountInfoRepository;

    @Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    FinancingActivityInterstCouponsRepository financingActivityInterstCouponsRepository;

    @Autowired
    UserInterstCouponsRelationRepository userInterstCouponsRelationRepository;

    @Autowired
    FinancingHomeConfigRepository financingHomeConfigRepository;

    @Autowired
    FinancingConfigCommonQuestionRepository financingConfigCommonQuestionRepository;

    @Autowired
    SzgDictInfoRepository szgDictInfoRepository;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    SystemProperties systemProperties;


    //获取体验金列表，按照体验金状态查询，flag为0表示关闭   1表示开启
    public PageInfo<GoldExperienceListDto> ；gelistService(Integer flag, Integer geId, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<GoldExperienceListDto> pageInfo = new PageInfo<>();
        try {
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, new Sort(Sort.Direction.DESC, "geId"));

            Specification<FinancingActivityGoldexperience> specification = new Specification<FinancingActivityGoldexperience>() {
                @Override
                public Predicate toPredicate(Root<FinancingActivityGoldexperience> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (!StringUtils.isEmpty(flag)) {
                        Predicate predicate = criteriaBuilder.equal(root.get("geStatus").as(Integer.class), flag);
                        predicates.add(predicate);
                    }
                    if (!StringUtils.isEmpty(geId)) {
                        Predicate predicateUser = criteriaBuilder.like(root.get("geId").as(String.class), "%" + geId + "%");
                        predicates.add(predicateUser);
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            Page<FinancingActivityGoldexperience> page = financingActivityGoldexperienceRepository.findAll(specification, pageable);
            List<GoldExperienceListDto> listGoldExperience = new ArrayList<>();
            for (FinancingActivityGoldexperience pr : page.getContent()) {
                FinancingActivityGoldexperience dto = new FinancingActivityGoldexperience();
                BeanUtils.copyProperties(pr, dto);
                GoldExperienceListDto goldExperienceList = new GoldExperienceListDto();
                goldExperienceList.setGeId(dto.getGeId());
                goldExperienceList.setGeAmount(dto.getGeAmount());
                goldExperienceList.setExperienceLength(dto.getExperienceLength());
                List<Object> listTemp = JSONArray.parseArray(dto.getGeCondition());
                Map<String, Object> map = JSONObject.parseObject(listTemp.get(0).toString());
                goldExperienceList.setCondition("满" + new BigDecimal(map.get("condition").toString().substring(1, map.get("condition").toString().length())) + "usdx可用");

                Map<String, Object> mapProject = JSONObject.parseObject(dto.getExperienceProject());
                List<String> projectlist = new ArrayList<>();
                for (Map.Entry<String, Object> maps : mapProject.entrySet()) {
                    projectlist.add(maps.getValue().toString());
                }
                goldExperienceList.setExperienceProject(projectlist);
                if (StringUtils.isEmpty(dto.getValidityDay())) {
                    goldExperienceList.setValidityTime(dateStr(dto.getValidityBeginTime(), "yyyy-MM-dd") + " ~ " + dateStr(dto.getValidityEndTime(), "yyyy-MM-dd"));
                } else {
                    goldExperienceList.setValidityTime(dto.getValidityDay() + "天");
                }
                listGoldExperience.add(goldExperienceList);
            }
            pageInfo.setTotal(page.getTotalElements());
            pageInfo.setList(listGoldExperience);
        } catch (Exception e) {
            throw new Exception("构建体验金用户列表失败", e);
        }
        return pageInfo;
    }

    //生成体验金项目，包括体验金额度、体验金时长、体验金有效时间或时长等信息
    @Transactional
    public String generatorOrderService(GoldExperienceDto goldExperienceDto) throws Exception {

        FinancingActivityGoldexperience financingActivityGoldexperience = new FinancingActivityGoldexperience();

        financingActivityGoldexperience.setGeAmount(goldExperienceDto.getGeAmount());
        financingActivityGoldexperience.setExperienceLength(goldExperienceDto.getExperienceLength());
        financingActivityGoldexperience.setGeStatus(1);
        if (StringUtils.isEmpty(goldExperienceDto.getValidityDay())) {
            financingActivityGoldexperience.setValidityBeginTime(dateFormate(goldExperienceDto.getValidityBeginTime(), "yyyy-MM-dd HH:mm:ss"));
            financingActivityGoldexperience.setValidityEndTime(dateFormate(goldExperienceDto.getValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
        } else {
            financingActivityGoldexperience.setValidityDay(Integer.parseInt(goldExperienceDto.getValidityDay()));
        }
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("describe", "满" + goldExperienceDto.getCondition() + "usdx可用");
        map.put("condition", ">" + goldExperienceDto.getCondition());
        list.add(map);
        financingActivityGoldexperience.setGeCondition(JSONObject.toJSONString(list));
        financingActivityGoldexperience.setExperienceProject(JSONArray.toJSONString(goldExperienceDto.getExperienceProject()));
        System.out.println(financingActivityGoldexperience.toString());
        financingActivityGoldexperience.setGeCreateTime(new Date());
        financingActivityGoldexperienceRepository.save(financingActivityGoldexperience);
        return "体验金保存成功";


    }

    //获取体验金可以投资项目，目前设置的时全部项目，后续如果针对新手项目再做调试
    public Map<String, String> gefinancingService() throws Exception {
        Map<String, String> map = new HashMap<>();
        try {

            List<FinancingBaseInfo> list = financingBaseInfoRepository.findAll();
            if (CollectionUtils.isEmpty(list) || list.size() == 0) {
                map.put("error", "目前没有可选的项目");
                return map;
            }
            for (int i = 0; i < list.size(); i++) {
                map.put(list.get(i).getFinancingUuid(), list.get(i).getTitle());
            }
        } catch (Exception e) {
            throw new Exception("项目获取错误", e);
        }
        return map;
    }

    //发放体验金，目前发放体验金用户，包含：新手用户（注册未投资），全部用户
    @Transactional
    public String geSendService(Integer flag, Integer geId, MultipartFile geUserExecl) throws Exception {

        List<String> list = new ArrayList<>();

        if (!ObjectUtils.isEmpty(geUserExecl) && flag == 2) {
            list = geAndIcUserSend(geUserExecl);
        }


        if (flag == 1) {
            //获取全部用户
            List<UserAccountInfo> listUserAccount = userAccountInfoRepository.findAll();
            for (int i = 0; i < listUserAccount.size(); i++) {
                list.add(listUserAccount.get(i).getUserId().toString());
            }
        } else if (flag == 0) {
            //获取新用户（新用户，注册未投资）
            List<UserFinancingRecord> listRecord = userFinancingRecordRepository.findAll();
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 0; i < listRecord.size(); i++) {
                map.put(listRecord.get(i).getUserId(), listRecord.get(i).getUserId());
            }
            List<UserAccountInfo> listUserAccount = userAccountInfoRepository.findAll();
            for (int i = 0; i < listUserAccount.size(); i++) {
                if (ObjectUtils.isEmpty(map.get(listUserAccount.get(i).getUserId()))) {
                    continue;
                } else {
                    listUserAccount.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < listUserAccount.size(); i++) {
                list.add(listUserAccount.get(i).getUserId().toString());
            }
        }

        //过滤掉已经领取过该体验金的用户
        List<UserGoldexperienceRelation> userGoldexperienceRelationList = userGoldexperienceRelationRepository.findByGeId(geId);
        String userIdStr = "[";
        for (int i = 0; i < userGoldexperienceRelationList.size(); i++) {
            if (userGoldexperienceRelationList.get(i).getGeId() == geId) {
                userIdStr = userIdStr + userGoldexperienceRelationList.get(i).getUserId() + "],[";
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (userIdStr.contains(list.get(i))) {
                list.remove(i--);
            }
        }

        FinancingActivityGoldexperience financingActivityGoldexperience = financingActivityGoldexperienceRepository.findByGeId(geId);

        for (int i = 0; i < list.size(); i++) {
            if(isInt(list.get(i))){
                UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(Integer.parseInt(list.get(i)));
                if(ObjectUtils.isEmpty(userAccountInfo)){
                    continue;
                }
            }else {
                continue;
            }
            UserGoldexperienceRelation userGoldexperienceRelation = new UserGoldexperienceRelation();
            userGoldexperienceRelation.setGeId(financingActivityGoldexperience.getGeId());
            userGoldexperienceRelation.setUserId(Integer.parseInt(list.get(i)));
            userGoldexperienceRelation.setReceiveDate(new Date());
            userGoldexperienceRelation.setExperienceStatus(0);
            userGoldexperienceRelationRepository.save(userGoldexperienceRelation);
        }

        return "发送体验金成功";
    }


    //指定用户发放体验金
    public List<String> geAndIcUserSend(MultipartFile geUserExecl) throws Exception {
        List<String> stringList = new ArrayList<>();
        HSSFWorkbook workbook = null;
        String fileName = geUserExecl.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            throw new Exception("文件传输失败!");
        }

        if (fileName.endsWith("xls")) {
            try {
                workbook = new HSSFWorkbook(geUserExecl.getInputStream());
                HSSFSheet hssfSheet = workbook.getSheetAt(0);
                if (ObjectUtils.isEmpty(hssfSheet)) {
                    throw new Exception("sheet页不存在");
                }
                for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow == null) {
                        continue;
                    }
                    // 循环列Cell
                    for (int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++) {
                        HSSFCell hssfCell = hssfRow.getCell(cellNum);
                        if (hssfCell == null) {
                            continue;
                        }
                        stringList.add(getValue(hssfCell));

                    }

                }
            } catch (Exception e) {
                log.error("解析xls文件失败");
                throw new Exception("解析xls文件失败", e);
            }

        }

        return stringList;
    }

    private String getValue(HSSFCell hssfCell){
        if(hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN){
            return String.valueOf( hssfCell.getBooleanCellValue());
        }else if(hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC){
            return String.valueOf((int) hssfCell.getNumericCellValue());
        }else{
            return String.valueOf( hssfCell.getStringCellValue());
        }
    }


    //获取体验金项目详情
    public GoldExperienceDto gedetailService(Integer geId) throws Exception {
        GoldExperienceDto goldExperienceDto = new GoldExperienceDto();
        try {
            FinancingActivityGoldexperience financingActivityGoldexperience = financingActivityGoldexperienceRepository.findByGeId(geId);
            goldExperienceDto.setGeId(geId);
            goldExperienceDto.setExperienceLength(financingActivityGoldexperience.getExperienceLength());
            goldExperienceDto.setGeAmount(financingActivityGoldexperience.getGeAmount());
            goldExperienceDto.setGeStatus(financingActivityGoldexperience.getGeStatus());
            if (StringUtils.isEmpty(financingActivityGoldexperience.getValidityDay()) || financingActivityGoldexperience.getValidityDay() == 0) {
                goldExperienceDto.setValidityDay(null);
                goldExperienceDto.setValidityBeginTime(dateStr(financingActivityGoldexperience.getValidityBeginTime(), "yyyy-MM-dd HH:mm:ss"));
                goldExperienceDto.setValidityEndTime(dateStr(financingActivityGoldexperience.getValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
            } else {
                goldExperienceDto.setValidityDay(financingActivityGoldexperience.getValidityDay() + "");
            }
            Map<String, Object> mapProject = JSONObject.parseObject(financingActivityGoldexperience.getExperienceProject());
            goldExperienceDto.setExperienceProject(mapProject);
            List<Object> list = JSONArray.parseArray(financingActivityGoldexperience.getGeCondition());
            Map<String, Object> mapCondition = JSONObject.parseObject(list.get(0).toString());
            goldExperienceDto.setCondition(new BigDecimal(mapCondition.get("condition").toString().substring(1, mapCondition.get("condition").toString().length())));
            goldExperienceDto.setGeCreateTime(dateStr(financingActivityGoldexperience.getGeCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new Exception("项目详情获取失败", e);
        }
        return goldExperienceDto;
    }

    //更新体验金项目
    @Transactional
    public String geUpdateService(GoldExperienceDto goldExperienceDto) throws Exception {


        FinancingActivityGoldexperience financingActivityGoldexperience = financingActivityGoldexperienceRepository.findByGeId(goldExperienceDto.getGeId());
        if (!StringUtils.isEmpty(goldExperienceDto.getGeAmount()))
            financingActivityGoldexperience.setGeAmount(goldExperienceDto.getGeAmount());
        if (!StringUtils.isEmpty(goldExperienceDto.getExperienceLength()))
            financingActivityGoldexperience.setExperienceLength(goldExperienceDto.getExperienceLength());
        if (!StringUtils.isEmpty(goldExperienceDto.getExperienceProject())) {
            financingActivityGoldexperience.setExperienceProject(JSONArray.toJSONString(goldExperienceDto.getExperienceProject()));
        }
        if (!StringUtils.isEmpty(goldExperienceDto.getGeStatus()))
            financingActivityGoldexperience.setGeStatus(goldExperienceDto.getGeStatus());
        if (StringUtils.isEmpty(goldExperienceDto.getValidityDay())) {
            if (!StringUtils.isEmpty(goldExperienceDto.getValidityBeginTime()))
                financingActivityGoldexperience.setValidityBeginTime(dateFormate(goldExperienceDto.getValidityBeginTime(), "yyyy-MM-dd HH:mm:ss"));
            if (!StringUtils.isEmpty(goldExperienceDto.getValidityEndTime()))
                financingActivityGoldexperience.setValidityEndTime(dateFormate(goldExperienceDto.getValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
            financingActivityGoldexperience.setValidityDay(null);
        } else {
            if (!StringUtils.isEmpty(goldExperienceDto.getValidityDay()))
                financingActivityGoldexperience.setValidityDay(Integer.parseInt(goldExperienceDto.getValidityDay()));
            financingActivityGoldexperience.setValidityBeginTime(null);
            financingActivityGoldexperience.setValidityEndTime(null);
        }
        if (!StringUtils.isEmpty(goldExperienceDto.getCondition())) {
            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("describe", "满" + goldExperienceDto.getCondition() + "usdx可用");
            map.put("condition", ">" + goldExperienceDto.getCondition());
            list.add(map);
            financingActivityGoldexperience.setGeCondition(JSONObject.toJSONString(list));
        }
        financingActivityGoldexperienceRepository.save(financingActivityGoldexperience);

        return "修改成功";
    }

    //获取拥有体验金用户，并展示用户使用体验金状态，用户使用体验金状态包含（0、未使用 1、计息中 2、已使用（计息结算）3、已过期）
    public PageInfo<GoldExperienceUserDto> geUserListService(Integer userId, Integer geStatus, Integer pageSize, Integer pageNum, Integer geId) throws Exception {
        PageInfo<GoldExperienceUserDto> pageinfo = new PageInfo();
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "receiveDate");
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
            Specification<UserGoldexperienceRelation> specification = new Specification() {
                @Override
                public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (!StringUtils.isEmpty(userId)) {
                        Predicate predicateUser = criteriaBuilder.like(root.get("userId").as(String.class), "%" + userId + "%");
                        predicates.add(predicateUser);
                    }

                    if (!StringUtils.isEmpty(geStatus)) {
                        Predicate predicateStatus = criteriaBuilder.like(root.get("experienceStatus").as(String.class), "%" + geStatus + "%");
                        predicates.add(predicateStatus);
                    }

                    if (!StringUtils.isEmpty(geId)) {
                        Predicate predicateStatus = criteriaBuilder.equal(root.get("geId").as(Integer.class), geId + "");
                        predicates.add(predicateStatus);
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

                }
            };
            Page<UserGoldexperienceRelation> page = userGoldexperienceRelationRepository.findAll(specification, pageable);
            List<GoldExperienceUserDto> list = new ArrayList<>();
            for (UserGoldexperienceRelation pr : page.getContent()) {
                UserGoldexperienceRelation dto = new UserGoldexperienceRelation();
                BeanUtils.copyProperties(pr, dto);
                GoldExperienceUserDto goldExperienceUserDto = new GoldExperienceUserDto();
                goldExperienceUserDto.setUserid(dto.getUserId());
                goldExperienceUserDto.setDisplayName(userAccountInfoRepository.findByUserId(goldExperienceUserDto.getUserid()).getDisplayName());
                goldExperienceUserDto.setReviceTime(dateStr(dto.getReceiveDate(), "yyyy-MM-dd HH:mm:ss"));
                if (dto.getExperienceStatus() == 0) {
                    goldExperienceUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_0));
                } else if (dto.getExperienceStatus() == 1) {
                    goldExperienceUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_1));
                } else if (dto.getExperienceStatus() == 2) {
                    goldExperienceUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_2));
                } else if (dto.getExperienceStatus() == 3) {
                    goldExperienceUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_3));
                }
                list.add(goldExperienceUserDto);
            }
            pageinfo.setList(list);
            pageinfo.setTotal(page.getTotalElements());
            return pageinfo;
        } catch (Exception e) {
            throw new Exception("获取用户列表信息错误", e);
        }
    }


    ////////////////////////////////////////////////////////////////加息券/////////////////////////////////////////////////////////////////////////////////////


    //获取加息券列表
    public PageInfo<InterstCouponsListDto> iclistService(Integer flag, Integer icId, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<InterstCouponsListDto> pageInfo = new PageInfo<>();
        try {
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, new Sort(Sort.Direction.DESC, "icId"));

            Specification<FinancingActivityInterstCoupons> specification = new Specification<FinancingActivityInterstCoupons>() {
                @Override
                public Predicate toPredicate(Root<FinancingActivityInterstCoupons> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (!StringUtils.isEmpty(flag)) {
                        Predicate predicate = criteriaBuilder.equal(root.get("icStatus").as(Integer.class), flag);
                        predicates.add(predicate);
                    }
                    if (!StringUtils.isEmpty(icId)) {
                        Predicate predicateUser = criteriaBuilder.like(root.get("icId").as(String.class), "%" + icId + "%");
                        predicates.add(predicateUser);
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            Page<FinancingActivityInterstCoupons> page = financingActivityInterstCouponsRepository.findAll(specification, pageable);
            List<InterstCouponsListDto> listInterstCouponsListDto = new ArrayList<>();
            for (FinancingActivityInterstCoupons pr : page.getContent()) {
                FinancingActivityInterstCoupons dto = new FinancingActivityInterstCoupons();
                BeanUtils.copyProperties(pr, dto);
                InterstCouponsListDto interstCouponsListDto = new InterstCouponsListDto();
                interstCouponsListDto.setIcId(dto.getIcId());
                interstCouponsListDto.setIcRate(dto.getIcRate().multiply(BigDecimal.valueOf(100)));
                interstCouponsListDto.setIcLength(dto.getIcLength());
                Map<String, Object> mapProject = JSONObject.parseObject(dto.getIcProject());
                List<String> projectlist = new ArrayList<>();
                for (Map.Entry<String, Object> maps : mapProject.entrySet()) {
                    projectlist.add(maps.getValue().toString());
                }
                interstCouponsListDto.setIcProject(projectlist);
                if (StringUtils.isEmpty(dto.getIcValidityDay())) {

                    interstCouponsListDto.setValidityTime(dateStr(dto.getIcValidityBeginTime(), "yyyy-MM-dd") + " ~ " + dateStr(dto.getIcValidityEndTime(), "yyyy-MM-dd"));
                } else {
                    interstCouponsListDto.setValidityTime(dto.getIcValidityDay() + "天");
                }
                listInterstCouponsListDto.add(interstCouponsListDto);
            }
            pageInfo.setTotal(page.getTotalElements());
            pageInfo.setList(listInterstCouponsListDto);
        } catch (Exception e) {
            throw new Exception("获取加息券用户列表失败", e);
        }
        return pageInfo;
    }

    //生成体验金项目，包括体验金额度、体验金时长、体验金有效时间或时长等信息
    @Transactional
    public String generatorIcOrderService(InterstCouponsDto interstCouponsDto) throws Exception {

        FinancingActivityInterstCoupons financingActivityInterstCoupons = new FinancingActivityInterstCoupons();

        financingActivityInterstCoupons.setIcRate(interstCouponsDto.getIcRate().divide(BigDecimal.valueOf(100), 3, BigDecimal.ROUND_HALF_UP));
        financingActivityInterstCoupons.setIcLength(interstCouponsDto.getIcLength());
        financingActivityInterstCoupons.setIcStatus(1);
        if (StringUtils.isEmpty(interstCouponsDto.getIcValidityDay())) {
            financingActivityInterstCoupons.setIcValidityBeginTime(dateFormate(interstCouponsDto.getIcValidityBeginTime(), "yyyy-MM-dd HH:mm:ss"));
            financingActivityInterstCoupons.setIcValidityEndTime(dateFormate(interstCouponsDto.getIcValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
        } else {
            financingActivityInterstCoupons.setIcValidityDay(Integer.parseInt(interstCouponsDto.getIcValidityDay()));
        }

        financingActivityInterstCoupons.setIcProject(JSONArray.toJSONString(interstCouponsDto.getIcProject()));
        System.out.println(financingActivityInterstCoupons.toString());
        financingActivityInterstCoupons.setIcCreateTime(new Date());
        financingActivityInterstCouponsRepository.save(financingActivityInterstCoupons);
        return "加息券保存成功";


    }


    //发放体验金，目前发放体验金用户，包含：新手用户（注册未投资），全部用户
    @Transactional
    public String icSendService(Integer flag, Integer icId, MultipartFile icUserExecl) throws Exception {

        List<String> list = new ArrayList<>();

        if (!ObjectUtils.isEmpty(icUserExecl) && flag == 2) {
            list = geAndIcUserSend(icUserExecl);
        }

        if (flag == 1) {
            //获取全部用户
            List<UserAccountInfo> listUserAccount = userAccountInfoRepository.findAll();
            for (int i = 0; i < listUserAccount.size(); i++) {
                list.add(listUserAccount.get(i).getUserId().toString());
            }
        } else if (flag == 0) {
            //获取新用户（新用户，注册未投资）
            List<UserFinancingRecord> listRecord = userFinancingRecordRepository.findAll();
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 0; i < listRecord.size(); i++) {
                map.put(listRecord.get(i).getUserId(), listRecord.get(i).getUserId());
            }
            List<UserAccountInfo> listUserAccount = userAccountInfoRepository.findAll();
            for (int i = 0; i < listUserAccount.size(); i++) {
                if (ObjectUtils.isEmpty(map.get(listUserAccount.get(i).getUserId()))) {
                    continue;
                } else {
                    listUserAccount.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < listUserAccount.size(); i++) {
                list.add(listUserAccount.get(i).getUserId().toString());
            }
        }

        //过滤掉已经领取过该体验金的用户
        List<UserInterstCouponsRelation> userInterstCouponsRelationList = userInterstCouponsRelationRepository.findByIcId(icId);
        String userIdStr = "[";
        for (int i = 0; i < userInterstCouponsRelationList.size(); i++) {
            if (userInterstCouponsRelationList.get(i).getIcId() == icId) {
                userIdStr = userIdStr + userInterstCouponsRelationList.get(i).getUserId() + "],[";
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (userIdStr.contains(list.get(i))) {
                list.remove(i--);
            }
        }

        FinancingActivityInterstCoupons financingActivityInterstCoupons = financingActivityInterstCouponsRepository.findByIcId(icId);
        Map<String, Object> map = JSONObject.parseObject(financingActivityInterstCoupons.getIcProject());
        for (int i = 0; i < list.size(); i++) {
            if(isInt(list.get(i))){
                UserAccountInfo userAccountInfo = userAccountInfoRepository.findByUserId(Integer.parseInt(list.get(i)));
                if(ObjectUtils.isEmpty(userAccountInfo)){
                    continue;
                }
            }else {
                continue;
            }
            UserInterstCouponsRelation userInterstCouponsRelation = new UserInterstCouponsRelation();
            userInterstCouponsRelation.setIcId(icId);
            userInterstCouponsRelation.setUserId(Integer.parseInt(list.get(i)));
            userInterstCouponsRelation.setReceiveDate(new Date());
            userInterstCouponsRelation.setIcStatus(0);

            userInterstCouponsRelationRepository.save(userInterstCouponsRelation);
        }


        return "发送加息券成功";
    }

    //获取加息券项目详情
    public InterstCouponsDto icdetailService(Integer icId) throws Exception {
        InterstCouponsDto interstCouponsDto = new InterstCouponsDto();
        try {
            FinancingActivityInterstCoupons financingActivityInterstCoupons = financingActivityInterstCouponsRepository.findByIcId(icId);
            interstCouponsDto.setIcId(icId);
            interstCouponsDto.setIcLength(financingActivityInterstCoupons.getIcLength());
            interstCouponsDto.setIcRate(financingActivityInterstCoupons.getIcRate().multiply(BigDecimal.valueOf(100)));
            interstCouponsDto.setIcStatus(financingActivityInterstCoupons.getIcStatus());
            if (StringUtils.isEmpty(financingActivityInterstCoupons.getIcValidityDay()) || financingActivityInterstCoupons.getIcValidityDay() == 0) {
                interstCouponsDto.setIcValidityDay(null);
                interstCouponsDto.setIcValidityBeginTime(dateStr(financingActivityInterstCoupons.getIcValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
                interstCouponsDto.setIcValidityEndTime(dateStr(financingActivityInterstCoupons.getIcValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
            } else {
                interstCouponsDto.setIcValidityDay(financingActivityInterstCoupons.getIcValidityDay() + "");
            }
            Map<String, Object> mapProject = JSONObject.parseObject(financingActivityInterstCoupons.getIcProject());
            interstCouponsDto.setIcProject(mapProject);
            interstCouponsDto.setIcCreateTime(dateStr(financingActivityInterstCoupons.getIcCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new Exception("项目详情获取失败", e);
        }
        return interstCouponsDto;
    }


    //更新体验金项目
    @Transactional
    public String icUpdateService(InterstCouponsDto interstCouponsDto) throws Exception {

        FinancingActivityInterstCoupons financingActivityInterstCoupons = financingActivityInterstCouponsRepository.findByIcId(interstCouponsDto.getIcId());
        if (!StringUtils.isEmpty(interstCouponsDto.getIcRate()))
            financingActivityInterstCoupons.setIcRate(interstCouponsDto.getIcRate().divide(BigDecimal.valueOf(100), 3, BigDecimal.ROUND_HALF_UP));
        if (!StringUtils.isEmpty(interstCouponsDto.getIcLength()))
            financingActivityInterstCoupons.setIcLength(interstCouponsDto.getIcLength());
        if (!StringUtils.isEmpty(interstCouponsDto.getIcProject())) {
            financingActivityInterstCoupons.setIcProject(JSONArray.toJSONString(interstCouponsDto.getIcProject()));
        }
        if (!StringUtils.isEmpty(interstCouponsDto.getIcStatus()))
            financingActivityInterstCoupons.setIcStatus(interstCouponsDto.getIcStatus());
        if (StringUtils.isEmpty(interstCouponsDto.getIcValidityDay())) {
            if (!StringUtils.isEmpty(interstCouponsDto.getIcValidityBeginTime()))
                financingActivityInterstCoupons.setIcValidityBeginTime(dateFormate(interstCouponsDto.getIcValidityBeginTime(), "yyyy-MM-dd HH:mm:ss"));
            if (!StringUtils.isEmpty(interstCouponsDto.getIcValidityEndTime()))
                financingActivityInterstCoupons.setIcValidityEndTime(dateFormate(interstCouponsDto.getIcValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
            financingActivityInterstCoupons.setIcValidityDay(null);
        } else {
            if (!StringUtils.isEmpty(interstCouponsDto.getIcValidityDay()))
                financingActivityInterstCoupons.setIcValidityDay(Integer.parseInt(interstCouponsDto.getIcValidityDay()));
            financingActivityInterstCoupons.setIcValidityBeginTime(null);
            financingActivityInterstCoupons.setIcValidityEndTime(null);
        }
        financingActivityInterstCouponsRepository.save(financingActivityInterstCoupons);

        return "修改成功";
    }

    //获取拥有体验金用户，并展示用户使用体验金状态，用户使用体验金状态包含（0、未使用 1、计息中 2、已使用（计息结算）3、已过期）
    public PageInfo<InterstCouponsUserDto> icUserListService(Integer userId, Integer geStatus, Integer pageSize, Integer pageNum, Integer geId) throws Exception {
        PageInfo<InterstCouponsUserDto> pageinfo = new PageInfo();
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "receiveDate");
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
            Specification<UserInterstCouponsRelation> specification = new Specification() {
                @Override
                public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (!StringUtils.isEmpty(userId)) {
                        Predicate predicateUser = criteriaBuilder.like(root.get("userId").as(String.class), "%" + userId + "%");
                        predicates.add(predicateUser);
                    }

                    if (!StringUtils.isEmpty(geStatus)) {
                        Predicate predicateStatus = criteriaBuilder.like(root.get("icStatus").as(String.class), "%" + geStatus + "%");
                        predicates.add(predicateStatus);
                    }

                    if (!StringUtils.isEmpty(geId)) {
                        Predicate predicateStatus = criteriaBuilder.equal(root.get("icId").as(Integer.class), geId + "");
                        predicates.add(predicateStatus);
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

                }
            };
            Page<UserInterstCouponsRelation> page = userInterstCouponsRelationRepository.findAll(specification, pageable);
            List<InterstCouponsUserDto> list = new ArrayList<>();
            for (UserInterstCouponsRelation pr : page.getContent()) {
                UserInterstCouponsRelation dto = new UserInterstCouponsRelation();
                BeanUtils.copyProperties(pr, dto);
                InterstCouponsUserDto interstCouponsUserDto = new InterstCouponsUserDto();
                interstCouponsUserDto.setUserid(dto.getUserId());
                interstCouponsUserDto.setDisplayName(userAccountInfoRepository.findByUserId(interstCouponsUserDto.getUserid()).getDisplayName());
                interstCouponsUserDto.setReviceTime(dateStr(dto.getReceiveDate(), "yyyy-MM-dd HH:mm:ss"));
                if (dto.getIcStatus() == 0) {
                    interstCouponsUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_0));
                } else if (dto.getIcStatus() == 1) {
                    interstCouponsUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_1));
                } else if (dto.getIcStatus() == 2) {
                    interstCouponsUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_2));
                } else if (dto.getIcStatus() == 3) {
                    interstCouponsUserDto.setGeState(changeGeState(UserGoldexperienceRelation.EXPERIENCE_STATUS_3));
                }
                list.add(interstCouponsUserDto);
            }
            pageinfo.setList(list);
            pageinfo.setTotal(page.getTotalElements());
            return pageinfo;
        } catch (Exception e) {
            throw new Exception("获取用户列表信息错误", e);
        }
    }


    ///////////////////////////////////////////////////////////////首页配置////////////////////////////////////////////////////////////////////////////////////
    public PageInfo<HomeConfigBannerDto> hcBannerListService(Integer status, String deviceType, String beginTime, String endTime, Integer pageSize, Integer pageNum) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PageInfo<HomeConfigBannerDto> pageInfo = new PageInfo<>();
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "hcModifyTime","hcId");
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
            Specification<FinancingHomeConfig> specification = new Specification<FinancingHomeConfig>() {
                @Override
                public Predicate toPredicate(Root<FinancingHomeConfig> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (!StringUtils.isEmpty(status)) {
                        Predicate predicateUser = criteriaBuilder.equal(root.get("status").as(String.class), status);
                        predicates.add(predicateUser);
                    }

                    if (!StringUtils.isEmpty(deviceType)) {
                        Predicate predicateStatus = criteriaBuilder.equal(root.get("deviceType").as(String.class), deviceType);
                        predicates.add(predicateStatus);
                    }

                    if (!StringUtils.isEmpty(beginTime)) {
                        Predicate predicateStatus = criteriaBuilder.greaterThanOrEqualTo(root.get("validityBeginTime").as(String.class), beginTime);
                        predicates.add(predicateStatus);
                    }

                    if (!StringUtils.isEmpty(endTime)) {
                        Predicate predicateStatus = criteriaBuilder.lessThanOrEqualTo(root.get("validityEndTime").as(String.class), endTime);
                        predicates.add(predicateStatus);
                    }

                    Predicate predicateHcType = criteriaBuilder.equal(root.get("type").as(Integer.class), 1);
                    predicates.add(predicateHcType);

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            Page<FinancingHomeConfig> page = financingHomeConfigRepository.findAll(specification, pageable);
            List<HomeConfigBannerDto> bannerDtoList = new ArrayList<>();
            for (FinancingHomeConfig pr : page.getContent()) {
                if (pr.getValidityEndTime().getTime() - new Date().getTime() < 0) {
                    pr.setStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_TWO);
                    financingHomeConfigRepository.save(pr);
                }
                HomeConfigBannerDto bannerDto = new HomeConfigBannerDto();
                bannerDto.setTypeId(pr.getTypeId().toString());
                Map<String, Object> textMap = JSONObject.parseObject(pr.getTextDescribe());
                bannerDto.setPicUrl(ObjectUtils.isEmpty(textMap.get("pictureUrl")) ? "-" : textMap.get("pictureUrl").toString());
                bannerDto.setValidityBeginTime(ObjectUtils.isEmpty(pr.getValidityBeginTime()) ? "-" : dateStr(pr.getValidityBeginTime(), "yyyy-MM-dd HH:mm:ss"));
                bannerDto.setValidityEndTime(ObjectUtils.isEmpty(pr.getValidityEndTime()) ? "-" : dateStr(pr.getValidityEndTime(), "yyyy-MM-dd HH:mm:ss"));
                bannerDto.setDeviceType(ObjectUtils.isEmpty(pr.getDeviceType()) ? "-" : pr.getDeviceType());
                bannerDto.setWeight(ObjectUtils.isEmpty(pr.getWeight()) ? "-" : pr.getWeight().toString());
                bannerDto.setStatus(ObjectUtils.isEmpty(pr.getStatus()) ? "-" : changeBannerStatus(pr.getStatus()));
                bannerDto.setTitle(ObjectUtils.isEmpty(textMap.get("title")) ? "-" : textMap.get("title").toString());
                bannerDtoList.add(bannerDto);
            }
            pageInfo.setList(bannerDtoList);
            pageInfo.setTotal(page.getTotalElements());

        } catch (Exception e) {
            throw new Exception("获取轮播图列表获取失败", e);
        }
        return pageInfo;
    }

    @Transactional
    public String hcBannerGenerateService(String contentUrl, Integer weight, String beginTime, String endTime, String title, MultipartFile bannerPic) throws Exception {
        Map<String,String> metaList = new HashMap<String, String>();
        String fileName = fastDFSUtil.uploadFile(bannerPic,bannerPic.getOriginalFilename(),metaList);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer rowNum = financingHomeConfigRepository.countByType(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_ONE);
        FinancingHomeConfig financingHomeConfig = new FinancingHomeConfig();
        financingHomeConfig.setTypeId(rowNum + 1);
        financingHomeConfig.setType(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_ONE);
        financingHomeConfig.setStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_ONE);
        financingHomeConfig.setValidityBeginTime(Timestamp.valueOf(beginTime));
        financingHomeConfig.setValidityEndTime(Timestamp.valueOf(endTime));
        financingHomeConfig.setWeight(ObjectUtils.isEmpty(weight)?0:weight);
        Map<String, String> map = new HashMap<>();
        map.put("pictureUrl",systemProperties.getFastdfsNginx() + fileName);
        map.put("contentUrl", contentUrl);
        map.put("title", title);
        financingHomeConfig.setTextDescribe(JSONObject.toJSONString(map));
        financingHomeConfig.setHcModifyTime(Timestamp.valueOf(simpleDateFormat.format(new Date())));
        financingHomeConfig.setDeviceType("android");
        financingHomeConfigRepository.save(financingHomeConfig);

        FinancingHomeConfig config = new FinancingHomeConfig();
        BeanUtils.copyProperties(financingHomeConfig, config);
        config.setHcId(null);
        config.setDeviceType("ios");
        config.setTypeId(rowNum + 2);
        financingHomeConfigRepository.save(config);

        return "轮播图生成成功";

    }

    public HomeConfigBannerDto hcBannerDetailService(Integer typeId) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FinancingHomeConfig financingHomeConfig = financingHomeConfigRepository.findByTypeAndTypeId(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_ONE, typeId);
        if (ObjectUtils.isEmpty(financingHomeConfig)) {
            throw new Exception("轮播图不存在");
        }
        HomeConfigBannerDto homeConfigBannerDto = new HomeConfigBannerDto();
        homeConfigBannerDto.setWeight(financingHomeConfig.getWeight().toString());
        homeConfigBannerDto.setValidityBeginTime(simpleDateFormat.format(financingHomeConfig.getValidityBeginTime()));
        homeConfigBannerDto.setValidityEndTime(simpleDateFormat.format(financingHomeConfig.getValidityEndTime()));
        Map<String, Object> map = JSONObject.parseObject(financingHomeConfig.getTextDescribe());
        homeConfigBannerDto.setTitle(ObjectUtils.isEmpty(map.get("title")) ? "-" : map.get("title").toString());
        homeConfigBannerDto.setPicUrl(ObjectUtils.isEmpty(map.get("pictureUrl")) ? "-" : map.get("pictureUrl").toString());
        homeConfigBannerDto.setContentUrl(ObjectUtils.isEmpty(map.get("contentUrl")) ? "-" : map.get("contentUrl").toString());
        return homeConfigBannerDto;
    }

    public String hcBannerUDService(Integer typeId, Integer status) {
        FinancingHomeConfig financingHomeConfig = financingHomeConfigRepository.findByTypeAndTypeId(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_ONE, typeId);
        if (status == financingHomeConfig.getStatus()) {
            return "轮播图状态与修改状态一致";
        }
        financingHomeConfig.setStatus(status);
        financingHomeConfigRepository.save(financingHomeConfig);
        return "轮播图修改成功！";
    }

    public String hcBannerupdateService(Integer typeId, String beginTime, String endTime, String contentUrl, Integer weight, String title, MultipartFile bannerPic) {

        FinancingHomeConfig financingHomeConfig = financingHomeConfigRepository.findByTypeAndTypeId(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_ONE, typeId);
        if (!ObjectUtils.isEmpty(beginTime)) {
            financingHomeConfig.setValidityBeginTime(Timestamp.valueOf(beginTime));
        }
        if (!ObjectUtils.isEmpty(endTime)) {
            financingHomeConfig.setValidityEndTime(Timestamp.valueOf(endTime));
        }
        if (!ObjectUtils.isEmpty(contentUrl)) {
            Map<String, Object> map = JSONObject.parseObject(financingHomeConfig.getTextDescribe());
            map.put("contentUrl", contentUrl);
            map.put("title", title);
            financingHomeConfig.setTextDescribe(JSONObject.toJSONString(map));
        }
        if (!ObjectUtils.isEmpty(weight)) {
            financingHomeConfig.setWeight(weight);
        }
        financingHomeConfigRepository.save(financingHomeConfig);
        return "轮播图更新成功";
    }

    ;


    public PageInfo<HomeConfigGeDto> hcGeListService(String status, Integer pageSize, Integer pageNum) throws Exception {

        PageInfo<HomeConfigGeDto> pageInfo = new PageInfo<>();

        try {
            Sort sort = new Sort(Sort.Direction.DESC, "hcModifyTime");
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
            Specification<FinancingHomeConfig> specification = new Specification<FinancingHomeConfig>() {
                @Override
                public Predicate toPredicate(Root<FinancingHomeConfig> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (!ObjectUtils.isEmpty(status)) {
                        Predicate predicateHcType = criteriaBuilder.equal(root.get("status").as(Integer.class), status);
                        predicates.add(predicateHcType);
                    }

                    Predicate predicateHcType = criteriaBuilder.equal(root.get("type").as(Integer.class), FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_TWO);
                    predicates.add(predicateHcType);


                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<FinancingHomeConfig> page = financingHomeConfigRepository.findAll(specification, pageable);
            List<HomeConfigGeDto> geDtoList = new ArrayList<>();
            for (FinancingHomeConfig pr : page.getContent()) {
                HomeConfigGeDto geDto = new HomeConfigGeDto();
                geDto.setGeId(ObjectUtils.isEmpty(pr.getTypeId()) ? "-" : pr.getTypeId().toString());
                geDto.setStatus(ObjectUtils.isEmpty(pr.getStatus()) ? "-" : changeBannerStatus(pr.getStatus()));
                Map<String, Object> map = JSONObject.parseObject(pr.getTextDescribe());
                geDto.setGeAmount(map.get("geAmount").toString());
                geDto.setGeStatus(map.get("status").toString());
                geDtoList.add(geDto);
            }
            pageInfo.setList(geDtoList);
            pageInfo.setTotal(page.getTotalElements());

        } catch (Exception e) {
            throw new Exception("获取首页配置体验金成功!", e);
        }
        return pageInfo;

    }

    @Transactional
    public String hcGeGenerateService(Integer geId) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



        FinancingActivityGoldexperience financingActivityGoldexperience = financingActivityGoldexperienceRepository.findByGeId(geId);
        if (ObjectUtils.isEmpty(financingActivityGoldexperience)) {
            log.error("体验金项目不存在");
            throw new Exception("体验金项目不存在");
        }

        List<FinancingHomeConfig> financingHomeConfigList = financingHomeConfigRepository.findByTypeAndStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_TWO,FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_ONE);

        if(!CollectionUtils.isEmpty(financingHomeConfigList)){
            for (FinancingHomeConfig financingHomeConfig : financingHomeConfigList){
                financingHomeConfig.setStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_TWO);
                financingHomeConfigRepository.save(financingHomeConfig);
            }
        }

        FinancingHomeConfig financingHomeConfig = new FinancingHomeConfig();
        financingHomeConfig.setType(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_TWO);
        financingHomeConfig.setTypeId(geId);
        financingHomeConfig.setStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_ONE);
        financingHomeConfig.setDeviceType("-");
        financingHomeConfig.setHcModifyTime(Timestamp.valueOf(simpleDateFormat.format(new Date())));
        Map<String, Object> map = new HashMap<>();
        map.put("geAmount", financingActivityGoldexperience.getGeAmount());
        map.put("status", changeGeStatus(financingActivityGoldexperience.getGeStatus()));
        financingHomeConfig.setTextDescribe(JSONObject.toJSONString(map));
        financingHomeConfigRepository.save(financingHomeConfig);

        return "首页配置体验金配置成功！";
    }

    public Map<String, String> hcGeService() throws Exception {

        Map<String, String> map = new HashMap<>();
        List<FinancingActivityGoldexperience> financingActivityGoldexperienceList = financingActivityGoldexperienceRepository.findAll();
        if (CollectionUtils.isEmpty(financingActivityGoldexperienceList)) {
            throw new Exception("没有可使用的体验金项目");
        }
        for (FinancingActivityGoldexperience financingActivityGoldexperience : financingActivityGoldexperienceList) {
            map.put(financingActivityGoldexperience.getGeId().toString(), "ID" + financingActivityGoldexperience.getGeId() + "  " + financingActivityGoldexperience.getGeAmount() + "USDX");
        }
        return map;
    }


    public Object hcGeUDService(Integer geId, Integer status) {

        FinancingHomeConfig financingHomeConfig = financingHomeConfigRepository.findByTypeAndTypeIdAndStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_TWO, geId,FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_ONE);
        if (status == financingHomeConfig.getStatus()) {
            return "体验金状态与修改状态一致";
        }
        financingHomeConfig.setStatus(status);
        financingHomeConfigRepository.save(financingHomeConfig);
        return "体验金修改成功！";

    }




    public PageInfo<HomeConfigNewManDto> hcNewManPicListService(Integer status, Integer pageSize, Integer pageNum) throws Exception {
        PageInfo<HomeConfigNewManDto> pageInfo = new PageInfo<>();

        try {
            Sort sort = new Sort(Sort.Direction.DESC, "hcId");
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
            Specification<FinancingHomeConfig> specification = new Specification<FinancingHomeConfig>() {
                @Override
                public Predicate toPredicate(Root<FinancingHomeConfig> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (!ObjectUtils.isEmpty(status)) {
                        Predicate predicateHcType = criteriaBuilder.equal(root.get("status").as(Integer.class), status);
                        predicates.add(predicateHcType);
                    }
                    Predicate predicateHcType = criteriaBuilder.equal(root.get("type").as(Integer.class), FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_THREE);
                    predicates.add(predicateHcType);
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<FinancingHomeConfig> page = financingHomeConfigRepository.findAll(specification, pageable);
            List<HomeConfigNewManDto> homeConfigNewManDtoList = new ArrayList<>();
            for (FinancingHomeConfig pr : page.getContent()) {
                HomeConfigNewManDto homeConfigNewManDto = new HomeConfigNewManDto();
                homeConfigNewManDto.setHcNewManId(ObjectUtils.isEmpty(pr.getTypeId()) ? "-" : pr.getTypeId().toString());
                homeConfigNewManDto.setHcNewManStatus(ObjectUtils.isEmpty(pr.getStatus()) ? "-" : changeBannerStatus(pr.getStatus()));
                Map<String,Object> map = JSONObject.parseObject(pr.getTextDescribe());
                homeConfigNewManDto.setHcNewManTitle(ObjectUtils.isEmpty(map.get("title"))?"-":map.get("title").toString());
                homeConfigNewManDto.setHcNewManPic(ObjectUtils.isEmpty(map.get("pictureUrl"))?"-":map.get("pictureUrl").toString());
                homeConfigNewManDtoList.add(homeConfigNewManDto);
            }
            pageInfo.setList(homeConfigNewManDtoList);
            pageInfo.setTotal(page.getTotalElements());


        } catch (Exception e) {
            throw new Exception("获取新人见面礼图片失败", e);
        }
        return pageInfo;

    }

    public String hcNewManPicGeneratorService(String title, MultipartFile newManPic) throws Exception {
        Map<String,String> metaList = new HashMap<String, String>();
        String fileName = fastDFSUtil.uploadFile(newManPic,newManPic.getOriginalFilename(),metaList);
        if(StringUtils.isEmpty(fileName)){
            throw  new Exception("文件上传失败");
        }
        List<FinancingHomeConfig> financingHomeConfigList = financingHomeConfigRepository.findByTypeAndStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_THREE,FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_ONE);
        if(!CollectionUtils.isEmpty(financingHomeConfigList)){
            for (FinancingHomeConfig financingHomeConfig : financingHomeConfigList){
                financingHomeConfig.setStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_TWO);
                financingHomeConfigRepository.save(financingHomeConfig);
            }
        }
        FinancingHomeConfig financingHomeConfig = new FinancingHomeConfig();
        financingHomeConfig.setType(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_THREE);
        financingHomeConfig.setTypeId(financingHomeConfigRepository.countByType(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_THREE ) + 1);
        financingHomeConfig.setStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_ONE);
        financingHomeConfig.setDeviceType("-");
        financingHomeConfig.setHcModifyTime(Timestamp.valueOf(LocalDateTime.now()));
        Map<String,String> map = new HashMap<>();
        map.put("title",title);
        map.put("pictureUrl",systemProperties.getFastdfsNginx() + fileName);
        financingHomeConfig.setTextDescribe(JSONObject.toJSONString(map));
        financingHomeConfigRepository.save(financingHomeConfig);
        return "新人见面礼图片新增成功!";

    }

    public String hcNewManPicUDService(Integer typeId, Integer status) throws Exception {

        FinancingHomeConfig financingHomeConfig = financingHomeConfigRepository.findByTypeAndTypeId(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_THREE,typeId);
        if(ObjectUtils.isEmpty(financingHomeConfig)){
            throw new Exception("配置不存在");
        }

        if(financingHomeConfig.getStatus() == status){
            throw new Exception("修改状态与存在状态一直");
        }

        if(status == 1){  //上架
            List<FinancingHomeConfig> financingHomeConfigList = financingHomeConfigRepository.findByTypeAndStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_THREE,FinancingHomeConfig.FINANCING_HOME_CONFIG_STATUS_ONE);
            if(!CollectionUtils.isEmpty(financingHomeConfigList)){
                for (FinancingHomeConfig financingHomeConfigs : financingHomeConfigList){
                    financingHomeConfigs.setStatus(FinancingHomeConfig.FINANCING_HOME_CONFIG_TYPE_TWO);
                    financingHomeConfigRepository.save(financingHomeConfigs);
                }
            }
        }

        financingHomeConfig.setStatus(status);
        financingHomeConfigRepository.save(financingHomeConfig);

        return "上架下架状态修改成功";

    }


    /////////////////////////////////////////////////////////////////////运营管理_常见问题配置////////////////////////////////////////////////
    public PageInfo<ConfigCommonQuestionDto> ccqlistService(Integer ccqType, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<ConfigCommonQuestionDto> pageInfo = new PageInfo<>();

        try {
            Sort sort = new Sort(Sort.Direction.DESC, "ccqCreateTime");
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
            Specification<FinancingConfigCommonQuestion> specification = new Specification<FinancingConfigCommonQuestion>() {
                @Override
                public Predicate toPredicate(Root<FinancingConfigCommonQuestion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (ccqType != 0) {
                        Predicate predicateHcType = criteriaBuilder.equal(root.get("ccqType").as(Integer.class), ccqType);
                        predicates.add(predicateHcType);
                    }

                    Predicate predicateHcType = criteriaBuilder.equal(root.get("ccqLang").as(String.class), "cn");
                    predicates.add(predicateHcType);

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<FinancingConfigCommonQuestion> page = financingConfigCommonQuestionRepository.findAll(specification, pageable);
            List<ConfigCommonQuestionDto> ccqDtoList = new ArrayList<>();
            for (FinancingConfigCommonQuestion pr : page.getContent()) {
                ConfigCommonQuestionDto ccqDto = new ConfigCommonQuestionDto();
                ccqDto.setCcqId(ObjectUtils.isEmpty(pr.getCcqId()) ? "-" : pr.getCcqId().toString());
                ccqDto.setCcqType(ObjectUtils.isEmpty(pr.getCcqType()) ? "-" : changeCcqStatus(pr.getCcqType()));
                ccqDto.setCcqTitle(ObjectUtils.isEmpty(pr.getCcqTitle()) ? "-" : pr.getCcqTitle());
                ccqDto.setCcqQuestion(ObjectUtils.isEmpty(pr.getCcqQuestion()) ? "-" : pr.getCcqQuestion());
                ccqDto.setCcqWeight(ObjectUtils.isEmpty(pr.getCcqWeight()) ? "-" : pr.getCcqWeight().toString());
                ccqDto.setCcqCreateTime(ObjectUtils.isEmpty(pr.getCcqCreateTime()) ? "-" : dateStr(pr.getCcqCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                ccqDtoList.add(ccqDto);
            }
            pageInfo.setList(ccqDtoList);
            pageInfo.setTotal(page.getTotalElements());


        } catch (Exception e) {
            throw new Exception("获取常见问题失败", e);
        }
        return pageInfo;
    }

    public String ccqGeneratorService(Integer ccqType, String ccqTitle, String ccqQuesttion, Integer ccqWeight) throws Exception {

        FinancingConfigCommonQuestion financingConfigCommonQuestions = financingConfigCommonQuestionRepository.findFirstByCcqLangOrderByCcqLangAscriptionDesc("cn");
        try {
            FinancingConfigCommonQuestion financingConfigCommonQuestion = new FinancingConfigCommonQuestion();
            financingConfigCommonQuestion.setCcqType(ccqType);
            financingConfigCommonQuestion.setCcqTitle(ccqTitle);
            financingConfigCommonQuestion.setCcqQuestion(ccqQuesttion);
            financingConfigCommonQuestion.setCcqWeight(ccqWeight);
            financingConfigCommonQuestion.setCcqCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            financingConfigCommonQuestion.setCcqLang("cn");
            if (ObjectUtils.isEmpty(financingConfigCommonQuestions)) {
                financingConfigCommonQuestion.setCcqLangAscription(1);
            } else {
                financingConfigCommonQuestion.setCcqLangAscription(financingConfigCommonQuestions.getCcqLangAscription() + 1);
            }
            financingConfigCommonQuestionRepository.save(financingConfigCommonQuestion);
        } catch (Exception e) {
            log.error("常见问题新增失败");
            throw new Exception("常见问题新增失败", e);
        }
        return "常见问题配置新增成功";

    }

    public Map<Integer, String> ccqTypeService() throws Exception {
        Map<Integer, String> map = new HashMap<>();
        try {
            map.put(FinancingConfigCommonQuestion.CONFIG_COMMON_QUESTION_ONE, "登录注册");
            map.put(FinancingConfigCommonQuestion.CONFIG_COMMON_QUESTION_TWO, "账户安全");
            map.put(FinancingConfigCommonQuestion.CONFIG_COMMON_QUESTION_THREE, "充值投资");
            map.put(FinancingConfigCommonQuestion.CONFIG_COMMON_QUESTION_FOUR, "提现");
            map.put(FinancingConfigCommonQuestion.CONFIG_COMMON_QUESTION_FIVE, "费用");
            map.put(FinancingConfigCommonQuestion.CONFIG_COMMON_QUESTION_SIX, "资产");
        } catch (Exception e) {
            throw new Exception("常见问题状态获取失败", e);
        }
        return map;
    }

    public ConfigCommonQuestionDto ccqDetailService(Integer ccqId) throws Exception {
        ConfigCommonQuestionDto configCommonQuestionDto = new ConfigCommonQuestionDto();
        FinancingConfigCommonQuestion financingConfigCommonQuestion = financingConfigCommonQuestionRepository.findByCcqId(ccqId);
        if (ObjectUtils.isEmpty(financingConfigCommonQuestion)) {
            throw new Exception("常见问题不存在");
        }
        configCommonQuestionDto.setCcqId(financingConfigCommonQuestion.getCcqId().toString());
        configCommonQuestionDto.setCcqType(financingConfigCommonQuestion.getCcqType().toString());
        configCommonQuestionDto.setCcqTitle(financingConfigCommonQuestion.getCcqTitle());
        configCommonQuestionDto.setCcqQuestion(financingConfigCommonQuestion.getCcqQuestion());
        configCommonQuestionDto.setCcqWeight(financingConfigCommonQuestion.getCcqWeight().toString());

        return configCommonQuestionDto;
    }

    public String ccqUpdateService(Integer ccqId, Integer ccqType, String ccqTitle, String ccqQuesttion,Integer ccqWeight) throws Exception {

        FinancingConfigCommonQuestion financingConfigCommonQuestion = financingConfigCommonQuestionRepository.findByCcqId(ccqId);
        if(ObjectUtils.isEmpty(financingConfigCommonQuestion)){
            throw new Exception("常见问题不存在!");
        }
        if (!ObjectUtils.isEmpty(ccqType)){
            financingConfigCommonQuestion.setCcqType(ccqType);
        }
        if (!ObjectUtils.isEmpty(ccqTitle)){
            financingConfigCommonQuestion.setCcqTitle(ccqTitle);
        }
        if (!ObjectUtils.isEmpty(ccqQuesttion)){
            financingConfigCommonQuestion.setCcqQuestion(ccqQuesttion);
        }
        if (!ObjectUtils.isEmpty(ccqWeight)){
            financingConfigCommonQuestion.setCcqWeight(ccqWeight);
        }
        financingConfigCommonQuestionRepository.save(financingConfigCommonQuestion);
        return "更新成功!";

    }

    @Transactional
    public String ccqDeleteService(Integer ccqId) {
        FinancingConfigCommonQuestion financingConfigCommonQuestion = financingConfigCommonQuestionRepository.findByCcqId(ccqId);
        List<FinancingConfigCommonQuestion> financingConfigCommonQuestionList = financingConfigCommonQuestionRepository.findByCcqLangAscription(financingConfigCommonQuestion.getCcqLangAscription());
        if(ObjectUtils.isEmpty(financingConfigCommonQuestion) || CollectionUtils.isEmpty(financingConfigCommonQuestionList)){
            return  "项目不存在";
        }
        for (FinancingConfigCommonQuestion financingConfigCommonQuestion1 : financingConfigCommonQuestionList){
            financingConfigCommonQuestionRepository.delete(financingConfigCommonQuestion1);
        }
        return  "项目删除成功";

    }


    public Map<String,String> ccqLangClassificationService() throws Exception {

        List<SzgDictInfo> szgDictInfoList = szgDictInfoRepository.findByDicTypeId(3);
        if(CollectionUtils.isEmpty(szgDictInfoList)){
            throw new Exception("没有可以支持的语种");
        }
        Map<String,String> map = new HashMap<>();
        for (SzgDictInfo szgDictInfo : szgDictInfoList){
            map.put(szgDictInfo.getDicKey(),szgDictInfo.getDicValue());
        }
        return  map;
    }

    public List<ConfigCommonQuestionLangDto> ccqLangListService(Integer ccqId) throws Exception {

        List<SzgDictInfo> szgDictInfoList = szgDictInfoRepository.findByDicTypeId(3);

        FinancingConfigCommonQuestion financingConfigCommonQuestion = financingConfigCommonQuestionRepository.findByCcqId(ccqId);
        List<FinancingConfigCommonQuestion> financingConfigCommonQuestionList = financingConfigCommonQuestionRepository.findByCcqLangAscription(financingConfigCommonQuestion.getCcqLangAscription());
        if(ObjectUtils.isEmpty(financingConfigCommonQuestion) || CollectionUtils.isEmpty(financingConfigCommonQuestionList)){
            throw  new Exception("项目不存在");
        }
        List<ConfigCommonQuestionLangDto> configCommonQuestionLangDtoList = new ArrayList<>();
        for (FinancingConfigCommonQuestion financingConfigCommonQuestion1 : financingConfigCommonQuestionList){
            ConfigCommonQuestionLangDto configCommonQuestionLangDto = new ConfigCommonQuestionLangDto();
            configCommonQuestionLangDto.setCcqLang(financingConfigCommonQuestion1.getCcqLang());
            configCommonQuestionLangDto.setCcqLangAscription(financingConfigCommonQuestion1.getCcqLangAscription().toString());
            configCommonQuestionLangDto.setCcqTitle(financingConfigCommonQuestion1.getCcqTitle());
            configCommonQuestionLangDto.setCcqQuestion(financingConfigCommonQuestion1.getCcqQuestion());
            for (SzgDictInfo szgDictInfo : szgDictInfoList){
                if(financingConfigCommonQuestion1.getCcqLang().equals(szgDictInfo.getDicKey().toLowerCase())){
                    configCommonQuestionLangDto.setCcqLangCN(szgDictInfo.getDicValue());
                }
            }
            configCommonQuestionLangDtoList.add(configCommonQuestionLangDto);
        }
        return configCommonQuestionLangDtoList;

    }

    @Transactional
    public String ccqUpateLangService(List<ConfigCommonQuestionLangDto> configCommonQuestionLangDtoList) throws Exception {

        if(CollectionUtils.isEmpty(configCommonQuestionLangDtoList)){
            throw new Exception("保存语言不能为空");
        }

        List<FinancingConfigCommonQuestion> financingConfigCommonQuestionList = financingConfigCommonQuestionRepository.findByCcqLangAscription(Integer.parseInt(configCommonQuestionLangDtoList.get(0).getCcqLangAscription()));

        List<String> langStr = new ArrayList<>();
        for (ConfigCommonQuestionLangDto configCommonQuestionLangDto : configCommonQuestionLangDtoList){

            FinancingConfigCommonQuestion financingConfigCommonQuestion = financingConfigCommonQuestionRepository.findByCcqLangAndCcqLangAscription(configCommonQuestionLangDto.getCcqLang().toLowerCase(),Integer.parseInt(configCommonQuestionLangDto.getCcqLangAscription()));
            if(ObjectUtils.isEmpty(financingConfigCommonQuestion)){
                FinancingConfigCommonQuestion financingConfigCommonQuestionCN = financingConfigCommonQuestionRepository.findByCcqLangAndCcqLangAscription("cn",Integer.parseInt(configCommonQuestionLangDto.getCcqLangAscription()));
                if(ObjectUtils.isEmpty(financingConfigCommonQuestionCN)){
                    throw new Exception("不存在中文翻译，请先设置中文翻译");
                }
                FinancingConfigCommonQuestion financingConfigCommonQuestionLang = new FinancingConfigCommonQuestion();
                financingConfigCommonQuestionLang.setCcqTitle(StringUtils.isEmpty(configCommonQuestionLangDto.getCcqTitle())?"-":configCommonQuestionLangDto.getCcqTitle());
                financingConfigCommonQuestionLang.setCcqQuestion(StringUtils.isEmpty(configCommonQuestionLangDto.getCcqQuestion())?"-":configCommonQuestionLangDto.getCcqQuestion());
                financingConfigCommonQuestionLang.setCcqLang(StringUtils.isEmpty(configCommonQuestionLangDto.getCcqLang())?"-":configCommonQuestionLangDto.getCcqLang());
                financingConfigCommonQuestionLang.setCcqType(financingConfigCommonQuestionCN.getCcqType());
                financingConfigCommonQuestionLang.setCcqWeight(financingConfigCommonQuestionCN.getCcqWeight());
                financingConfigCommonQuestionLang.setCcqLangAscription(financingConfigCommonQuestionCN.getCcqLangAscription());
                financingConfigCommonQuestionLang.setCcqCreateTime(Timestamp.valueOf(LocalDateTime.now()));
                financingConfigCommonQuestionRepository.save(financingConfigCommonQuestionLang);
            }else {
                financingConfigCommonQuestion.setCcqTitle(ObjectUtils.isEmpty(configCommonQuestionLangDto.getCcqTitle())?"-":configCommonQuestionLangDto.getCcqTitle());
                financingConfigCommonQuestion.setCcqQuestion(ObjectUtils.isEmpty(configCommonQuestionLangDto.getCcqQuestion())?"-":configCommonQuestionLangDto.getCcqQuestion());
                financingConfigCommonQuestionRepository.save(financingConfigCommonQuestion);
            }
            langStr.add(configCommonQuestionLangDto.getCcqLang());
        }

        String str = langStr.toString();
        for (int i = 0; i < financingConfigCommonQuestionList.size(); i++){
            if(!str.contains(financingConfigCommonQuestionList.get(i).getCcqLang())){
                financingConfigCommonQuestionRepository.delete(financingConfigCommonQuestionList.get(i));
            }
        }

        return "多语言保存成功";

    }
    /////////////////////////////////////////////////////////////////公用函数//////////////////////////////////////////////////////////////////////////////////


    //字符串转时间格式，DateStr为时间字符串，formate为转化的时间格式  "yyyy-MM-dd HH:mm:ss"
    public Date dateFormate(String DateStr, String formate) throws Exception {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formate);
            Date date = formatter.parse(DateStr);
            return date;
        } catch (ParseException e) {
            throw new Exception("时间格式解析不正确");
        }
    }

    public String dateStr(Date date, String formate) throws Exception {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formate);
            return formatter.format(date);
        } catch (Exception e) {
            throw new Exception("时间转字符串，转化格式不正确", e);
        }
    }

    //判断字符串是否为整形
    public  boolean isInt(String string) {
        if (string == null)
            return false;

        String regEx1 = "[\\-|\\+]?\\d+";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }



    ///////////////////////修改前端显示状态值//////////////////////////////////////////
    public String changeBannerStatus(Integer status) {
        if (ObjectUtils.isEmpty(status)) {
            return "-";
        } else if (status == 1) {
            return "已上架";
        } else if (status == 2) {
            return "已下架";
        } else {
            return "-";
        }
    }


    public String changeGeStatus(Integer status) {
        if (ObjectUtils.isEmpty(status)) {
            return "-";
        } else if (status == 0) {
            return "已关闭";
        } else if (status == 1) {
            return "已开启";
        } else {
            return "-";
        }
    }

    public String changeCcqStatus(Integer status) {
        if (ObjectUtils.isEmpty(status)) {
            return "-";
        } else if (status == 1) {
            return "登录注册";
        } else if (status == 2) {
            return "账户安全";
        } else if (status == 3) {
            return "充值投资";
        } else if (status == 4) {
            return "提现";
        } else if (status == 5) {
            return "费用";
        } else if (status == 6) {
            return "资产";
        } else {
            return "-";
        }
    }


    public String changeGeState(Integer status) {
        if (ObjectUtils.isEmpty(status)) {
            return "-";
        } else if (status == 0) {
            return "未激活";
        } else if (status == 1) {
            return "计息中";
        } else if (status == 2) {
            return "已使用";
        } else if (status == 3) {
            return "已过期";
        } else {
            return "-";
        }
    }


}
