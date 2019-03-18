package com.bcb.service;


import com.bcb.bean.dto.*;
import com.bcb.bean.req.OperationLogRequestDto;
import com.bcb.bean.req.SmsLogRequestDto;
import com.bcb.domain.dao.*;
import com.bcb.domain.repository.*;
import com.bcb.domain.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class CommonService {

    @Autowired
    private UserAccountInfoDao userAccountInfoDao;

    @Autowired
    private FinancingIncomeUnknownDao iFinancingIncomeUnknownDao;

    @Autowired
    private SystemLoginAccountRepository systemLoginAccountRepository;

    @Autowired
    private SystemConfigInfoRepository systemConfigInfoRepository;
    @Autowired
    private SystemBusinessDictionaryRepository systemBusinessDictionaryRepository;

    @Autowired
    private AppConfigInfoRepository appConfigInfoRepository;

    @Autowired
    private SystemLoginMenuRepository systemLoginMenuRepository;

    @Autowired
    private TradeDao tradeDao;

    @Autowired
    private  SystemLoginRoleRepository systemLoginRoleRepository;

    @Autowired
    private SystemLogOperationRepository systemLogOperationRepository;

    @Autowired
    SystemLogSmsRepository systemLogSmsRepository;

    @Autowired
    private FinancingBaseInfoRepository financingBaseInfoRepository;
    
    @Autowired
    private FinancingWalletInfoRepository financingWalletInfoRepository;
    
    @Autowired
    private FinancingCnInfoRepository financingCnInfoRepository;
    
    @Autowired
    private FinancingEnInfoRepository financingEnInfoRepository;
    
    @Autowired
    private SzgDictInfoRepository szgDictInfoRepository;

    @Autowired
    UserAccountInfoRepository userAccountInfoRepository;


    public SystemLoginAccount querySystemLoginAccount(Integer accountId) {
        return systemLoginAccountRepository.findById(accountId).get();
    }

    public void deleteAccountsById(Integer id){
        systemLoginAccountRepository.deleteById(id);
    }

    public Integer saveOrUpdateSystemLoginAccount(SystemLoginAccount systemLoginAccount) {
        return systemLoginAccountRepository.save(systemLoginAccount).getId();
    }


    public List<SystemLoginAccount> querySystemLoginAccounts() {
        return systemLoginAccountRepository.findAll();
    }


    public List<SystemBusinessDictionary> querySystemBusinessDictionarys(Integer groupId) {
        return systemBusinessDictionaryRepository.findByGroupId(groupId);
    }

    public List<SystemLoginMenu> queryMenuList(Integer accountId) {

        return userAccountInfoDao.queryMenuList(accountId);
    }


    public UserAccountInfo getUserAccountInfo(Integer userId) {
        return userAccountInfoRepository.findByUserId(userId);
    }



    public void saveOrUpdateUserAccountInfo(UserAccountInfo userAccountInfo) {
        userAccountInfoRepository.save(userAccountInfo);
    }


    public AppConfigInfo queryAppConfigInfo(Integer id) {
        return appConfigInfoRepository.findById(id).get();
    }


    public void saveOrUpdateAppConfigInfo(AppConfigInfo appConfigInfo) {
        appConfigInfoRepository.save(appConfigInfo);
    }


    public List<UserInvestRateDto> queryAgentTradeList(Integer tradeId) {
        return tradeDao.queryAgentTradeList(tradeId);
    }

    public Map<String, Object> queryFinancingIncomeUnknowns(Integer start, Integer length, Integer status, String search) {
        return iFinancingIncomeUnknownDao.queryFinancingIncomeUnknowns(start, length, status, search);
    }

    //Role

    public void createNewRole(SystemLoginRole systemLoginRole) {
        systemLoginRoleRepository.save(systemLoginRole);
    }


    public void deleteRoleByIds(String ids) {
        //最底层的直接删除，不删除它下面的子节点
        String [] st = ids.split(",");
        int s = st.length;
        for (int i = 0; i < s; i++) {
            systemLoginRoleRepository.deleteById(Integer.parseInt(st[i]));
        }
    }


    public Integer queryAccountExit(String ids) {
        String [] st = ids.split(",");
        int s = st.length;
        List<SystemLoginAccount> ls = new ArrayList<>();
        for (int i = 0; i < s; i++) {
            if(systemLoginAccountRepository.findByRole(Integer.parseInt(st[i])).size()>0){
                List<SystemLoginAccount> lst =  systemLoginAccountRepository.findByRole(Integer.parseInt(st[i]));
                ls.addAll(lst);
            }
        }
        return ls.size();
    }

    public void updateRole(SystemLoginRole systemLoginRole) {
        systemLoginRoleRepository.save(systemLoginRole);
    }

    public List<SystemLoginAccount> queryRoleUser(Integer id) {
        List<SystemLoginAccount> ls = systemLoginAccountRepository.findByRole(id);
        return ls;
    }

    public List<SystemLoginRole> queryRoleUser(String name) {
        return systemLoginRoleRepository.findSystemLoginRoleByRoleName(name);
    }

    public List<SystemLoginMenu> getAllTableList() {
        return  userAccountInfoDao.queryMenuListOrder();
    }


    public List<SystemButtonMenu> queryButtonMenuList() {
        return userAccountInfoDao.queryButtonMenuList();
    }


    public SystemConfigInfo querySystemConfigInfo(Integer projectId) {
        return systemConfigInfoRepository.findByProjectId(projectId);
    }


    public void updateSystemConfigInfo(SystemConfigInfo systemConfigInfo) {
        systemConfigInfoRepository.saveAndFlush(systemConfigInfo);
    }


    public void addOrEditTable(SystemLoginMenu systemLoginMenu) {
        systemLoginMenuRepository.save(systemLoginMenu);
    }


    public void deleteTable(Integer id) {
        systemLoginMenuRepository.deleteById(id);
    }


    public Integer saveOperationLog(SystemLogOperation operationLog) {
        return systemLogOperationRepository.save(operationLog).getId();
    }


    public Page<SystemLogOperation> operationLoglist(OperationLogRequestDto dto,Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<SystemLogOperation> listLog = systemLogOperationRepository.findAll(new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate pred = null;
                if(!StringUtils.isEmpty(dto.getActionType())){
                    pred = criteriaBuilder.like(root.get("actionType"), dto.getActionType());
                }
                if(!StringUtils.isEmpty(dto.getCommentDescribe())){
                    Predicate tmp = criteriaBuilder.like(root.get("commentDescribe"), dto.getCommentDescribe());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getParameterContext())){
                    Predicate tmp = criteriaBuilder.like(root.get("parameterContext"), "%"+dto.getParameterContext()+"%");
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getOperatorName())){
                    Predicate tmp = criteriaBuilder.like(root.get("operatorName"), "%"+dto.getOperatorName()+"%");
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getCreateTimeStart())){
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date start = sdf.parse(dto.getCreateTimeStart());
                        Predicate tmp = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), start);
                        if(pred == null){
                            pred = tmp;
                        }else{
                            pred = criteriaBuilder.and(pred, tmp);
                        }
                    }catch (Exception e){

                    }
                }
                if(!StringUtils.isEmpty(dto.getCreateTimeEnd())){
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date end = sdf.parse(dto.getCreateTimeEnd());
                        Predicate tmp = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), end);
                        if(pred == null){
                            pred = tmp;
                        }else{
                            pred = criteriaBuilder.and(pred, tmp);
                        }
                    }catch (Exception e){

                    }
                }
                return pred;
            }
        }, pageable);
        return listLog;
    }

    public List<SystemLoginRole> queryAllRoles() {
        return systemLoginRoleRepository.findAll();
    }


    public UserInfoResultDto queryUserInfoDetail(Integer userId) {
        List<Integer> listSubStaffId = new ArrayList<Integer>();
        UserInfoResultDto userInfoResultDto = new UserInfoResultDto();
        UserInfoperson userInfoperson = new UserInfoperson();
        //查询所有的用户
        List<UserAccountInfo> listAllUserAccountInfo = tradeDao.queryUserTreeDatasAll();
        for (UserAccountInfo temp1 : listAllUserAccountInfo) {
            if (userId.equals(temp1.getUserId())) {
                userInfoperson.setUserId(temp1.getUserId());
                userInfoperson.setScale(temp1.getFinancingScale());
                userInfoperson.setDisplayName(temp1.getDisplayName());
                userInfoperson.setCreateTime(temp1.getcTime());
                userInfoperson.setCode(temp1.getCode());
                userInfoperson.setAgentLevel(temp1.getAgentLevel());
                userInfoperson.setCountryCode(temp1.getCountryCode());
                userInfoperson.setListWallet(tradeDao.queryUserListWallet(userId));
            }
        }
        userInfoResultDto.setUserInfoperson(userInfoperson);
        //查询父结点
        List<UserInfoparent> userInfoparentList = new ArrayList<>();
        UserInfoparent UserInfoparenttemp = new UserInfoparent();
        UserInfoparenttemp.setUserId(userId);
        for (int i = userInfoperson.getAgentLevel() - 1; i > 0; i--) {
            UserInfoparent forUserInfoparent = tradeDao.queryforUserInfoparent(UserInfoparenttemp.getUserId());
            UserInfoparenttemp.setUserId(forUserInfoparent.getUserId());
            userInfoparentList.add(forUserInfoparent);
        }
        // if(userInfoparentList!=null&&userInfoparentList.size()>0) {
        userInfoResultDto.setUserInfoparentList(userInfoparentList);
        // }
        //查询子结点
        List<UserInfochild> userInfochildList = new ArrayList<>();
        List<Integer> listUserId = getSubStaffTree(listSubStaffId, listAllUserAccountInfo, userId);

        if (listUserId != null && listUserId.size() > 0) {
            userInfochildList = tradeDao.queryAlluserInfochildList(listUserId);

            for (UserInfochild tempUserInfochild : userInfochildList) {
                tempUserInfochild.setInvestTatolSelf(BigDecimal.valueOf(0));
                tempUserInfochild.setInvestTatolLevel(BigDecimal.valueOf(0));
                tempUserInfochild.setInvestTatolLevel(BigDecimal.valueOf(0));
                tempUserInfochild.setInvestUsdxTatolLevel(BigDecimal.valueOf(0));
                //设置投资代理收益
                List<Integer> childlistSubStaffId = new ArrayList<Integer>();
                List<Integer> chiilListUserId = getSubStaffTree(childlistSubStaffId, listAllUserAccountInfo, tempUserInfochild.getUserId());
                //投资额本人
                
                //投资额所有的下级节点
                
                //代理收益本人
                
                //代理收益下级节点
                
            }
            userInfoResultDto.setUserInfochildList(userInfochildList);
        }
        return userInfoResultDto;

    }

    public List<Integer> getSubStaffTree(List<Integer> listSubStaffId, List<UserAccountInfo> listAllUserAccountInfotemp, Integer parentUserId) {
        for (UserAccountInfo teamProfile : listAllUserAccountInfotemp) {
            if (parentUserId.equals(teamProfile.getParentId())) {
                getSubStaffTree(listSubStaffId, listAllUserAccountInfotemp, teamProfile.getUserId());
                listSubStaffId.add(teamProfile.getUserId());
            }
        }
        return listSubStaffId;
    }


    public SystemLoginRole queryRoleById(Integer id) {
        return systemLoginRoleRepository.findById(id).get();
    }

    public Page<SystemLogSms> smsLoglist(SmsLogRequestDto dto, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, new Sort(Sort.Direction.DESC, "updateTime"));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate pred = null;
                if(!StringUtils.isEmpty(dto.getMobilePhone())){
                    pred = criteriaBuilder.like(root.get("mobilePhone"), "%"+dto.getMobilePhone()+"%");
                }
                if(!StringUtils.isEmpty(dto.getIsdealWith())){
                    Predicate tmp = criteriaBuilder.equal(root.get("isdealWith"), dto.getIsdealWith());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getIsSend())){
                    Predicate tmp = criteriaBuilder.equal(root.get("isSend"), dto.getIsSend());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getSmsChannel())){
                    Predicate tmp = criteriaBuilder.equal(root.get("smsChannel"), dto.getSmsChannel());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getCreateTimeStart())){
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date start = sdf.parse(dto.getCreateTimeStart());
                        Predicate tmp = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), start);
                        if(pred == null){
                            pred = tmp;
                        }else{
                            pred = criteriaBuilder.and(pred, tmp);
                        }
                    }catch (Exception e){

                    }
                }
                if(!StringUtils.isEmpty(dto.getCreateTimeEnd())){
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date end = sdf.parse(dto.getCreateTimeEnd());
                        Predicate tmp = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), end);
                        if(pred == null){
                            pred = tmp;
                        }else{
                            pred = criteriaBuilder.and(pred, tmp);
                        }
                    }catch (Exception e){

                    }
                }
                return pred;
            }
        };
        Page<SystemLogSms> listLog = systemLogSmsRepository.findAll(spec, pageable);
        return listLog;
    }

    /**
     * 当前登录人按钮权限列表
     * @return
     */
    public List<SystemLoginMenu> queryPermissionButtonMenuList(Integer accountId){
        return userAccountInfoDao.queryPermissionButtonMenuList(accountId);
    }


	public void saveOrUpdateFinancingProject(FinancingBaseInfo financingBaseInfo) {
		// TODO Auto-generated method stub
		financingBaseInfoRepository.saveAndFlush(financingBaseInfo);
		
	}


	public void saveOrUpdateWalletMessage(FinancingWalletInfo financingWalletInfo) {
		// TODO Auto-generated method stub
		financingWalletInfoRepository.saveAndFlush(financingWalletInfo);
		
	}


	public FinancingBaseInfo queryFinancingBaseInfoByUuid(String financingUuid) {
		// TODO Auto-generated method stub
		return financingBaseInfoRepository.findOneByFinancingUuid(financingUuid);
	}


	public List<FinancingWalletInfo> queryFinancingWalletInfo(String financingUuid) {
		// TODO Auto-generated method stub
		return financingWalletInfoRepository.findByFinancingUuid(financingUuid);
	}


	public FinancingCnInfo queryFinancingCnInfoByUuid(String financingUuid) {
		// TODO Auto-generated method stub
		return financingCnInfoRepository.findOneByFinancingUuid(financingUuid);
	}


	public FinancingEnInfo queryFinancingEnInfoByUuid(String financingUuid) {
		// TODO Auto-generated method stub
		return financingEnInfoRepository.findOneByFinancingUuid(financingUuid);
	}


	public List<SzgDictInfo> queryLanguageEnumByDicTypeId(Integer dicTypeid) {
		// TODO Auto-generated method stub
		return szgDictInfoRepository.findByDicTypeId(dicTypeid);
	}


	public void saveOrUpdatecFinancingCnInfo(FinancingCnInfo financingCnInfo) {
		// TODO Auto-generated method stub
		financingCnInfoRepository.saveAndFlush(financingCnInfo);
	}


	public void saveOrUpdatecFinancingEnInfo(FinancingEnInfo financingEnInfo) {
        // TODO Auto-generated method stub
        financingEnInfoRepository.saveAndFlush(financingEnInfo);
    }

    public void saveOrUpdateSystemLogOperation(SystemLogOperation systemLogOperation) {
        // TODO Auto-generated method stub
        systemLogOperationRepository.saveAndFlush(systemLogOperation);
    }

    public List<FinancingBaseInfo> findFinancingBaseInfoList() {
        // TODO Auto-generated method stub
        return financingBaseInfoRepository.findAll();
    }
}
