package com.bcb.domain.dao;

import com.bcb.bean.dto.UserInfochild;
import com.bcb.bean.dto.UserInfoparent;
import com.bcb.bean.dto.UserInvestRateDto;
import com.bcb.domain.entity.UserAccountInfo;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class TradeDao extends BaseDao{

    public List<UserInvestRateDto> queryAgentTradeList(Integer tradeId) {
        final StringBuilder sb = new StringBuilder("");
        sb.append(" select ucode.Code as code,uinfo.DisplayName as userName,");
        sb.append(" gainAgentLevel(uinfo.UserId) as agentLevel,CONCAT(bill.CoinAmount,'') as bcbAgentAmount ");
        sb.append(" from user_agents_bill bill ");
        sb.append(" left join user_account_info uinfo on bill.UserId=uinfo.UserId ");
        sb.append(" left join user_invit_code ucode on ucode.UserId=uinfo.UserId ");
        sb.append(" where bill.RelationId=?");
        sb.append(" order by gainAgentLevel(uinfo.UserId) asc ");
        return  this.jdbcTemplate.query(
                sb.toString(),
                new Object[] {tradeId},
                new BeanPropertyRowMapper<>(
                        UserInvestRateDto.class));
    }

    public Map<String, Object> queryUserTreeDatas(Integer page, Integer rows,String condition) {
        Map<String,Object> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from (");
        sb.append("select a.UserId as userId,b.Code as codeId, a.DisplayName as displayName,a.step as step,CONCAT(IFNULL(ROUND((a.FinancingScale)/1,2),0),\"%\") as financingScale, a.createTime as registTime,a.MobileNumber as mobileNumber\n" +
                "from(\n" +

                "\tselect DATE_FORMAT(CreateTime,'%Y-%m-%d %H:%i:%s') as createTime,MobileNumber,DisplayName,UserId,gainAgentLevel(UserId) as step,FinancingScale from user_account_info\n" +
                ")a \n" +
                "JOIN(\n" +
                "\tselect UserId,Code from user_invit_code\n" +
                ")b on a.UserId = b.UserId");
        sb.append(")aaaaa where 1=1 ");
        if(!ObjectUtils.isEmpty(condition)){
            sb.append(" and (userId like '%"+condition+"%' or mobileNumber like '%" + condition + "%')");
        }
        sb.append(" order by registTime limit ?,?");
        List<Map<String,Object>> list = this.jdbcTemplate.queryForList(
                sb.toString(),
                new Object[] {page,rows});
        map.put("items",list);
        final StringBuilder countSql = new StringBuilder("");
        countSql.append("select count(*) from( " +
                "select a.UserId as userId,b.Code as codeId, a.DisplayName as displayName,a.step as step,a.FinancingScale as financingScale, a.createTime as registTime,a.MobileNumber as mobileNumber\n" +
                "from(\n" +
                "\tselect DATE_FORMAT(CreateTime,'%Y-%m-%d %H:%i:%s') as createTime,MobileNumber,DisplayName,UserId,gainAgentLevel(UserId) as step,FinancingScale from user_account_info\n" +
                ")a \n" +
                "JOIN(\n" +
                "\tselect UserId,Code from user_invit_code\n" +
                ")b on a.UserId = b.UserId  " +
                ")aaaaa");
        countSql.append(" where 1=1 ");
        if(!ObjectUtils.isEmpty(condition)){
            countSql.append(" and (userId like '%"+condition+"%' or mobileNumber like '%" + condition + "%')");
        }
        Integer count = this.jdbcTemplate.queryForObject(countSql.toString(),new Object[]{},Integer.class);
        map.put("num",count);
        return map;
    }


    public List<UserAccountInfo> queryUserTreeDatasAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("select  uinfo.CountryCode as countryCode,ucode.Code as code,uinfo.UserId,uinfo.ParentId,uinfo.DisplayName, ");
        sb.append("uinfo.Status,DATE_FORMAT(uinfo.CreateTime,'%Y-%m-%d %H:%i:%s') as cTime,");
        sb.append("gainAgentLevel(uinfo.UserId) as agentLevel ,uinfo.Scale ");
        sb.append(" from user_account_info uinfo");
        sb.append(" left join user_invit_code ucode on ucode.UserId = uinfo.UserId ");
        sb.append(" left join user_wallet_info wallet on wallet.UserId = uinfo.UserId ");
        sb.append(" where 1=1 ");
        List<UserAccountInfo> listUserAccountInfo = this.jdbcTemplate.query(
                sb.toString(),
                new BeanPropertyRowMapper<>(
                        UserAccountInfo.class));
        return listUserAccountInfo;
    }


    public List<String> queryUserListWallet(Integer userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select Token from user_wallet_info ");
        sb.append(" where UserId =  "+userId );
        List<String> ListWallet = this.jdbcTemplate.query(
                sb.toString(), new RowMapper<String>(){
                    public String mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        return rs.getString(1);
                    }
                });
        return ListWallet;
    }


    public UserInfoparent queryforUserInfoparent(Integer userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select uinfo.ParentId as userId,\n" +
                "gainAgentLevel(uinfo.ParentId) as displayAgentLevel \n" +
                "from user_account_info uinfo");
        sb.append(" where uinfo.UserId =  "+userId );
        List<UserInfoparent> list = this.jdbcTemplate.query(
                sb.toString(),
                new BeanPropertyRowMapper<>(
                        UserInfoparent.class));
        if(list != null && list.size()>0)return list.get(0);
        return null;
    }


    public List<UserInfochild> queryAlluserInfochildList(List<Integer> listUserId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ucode.Code as code,uinfo.UserId,uinfo.displayName  from user_account_info uinfo " +
                        " left join user_invit_code ucode on ucode.UserId = uinfo.UserId "
                );
        sb.append(" where uinfo.UserId  in  "+"( 0  "+"," );
        for(Integer userId:listUserId) {
            sb.append(userId+"," );
        }
       // if(listUserId!=null && listUserId.size()>0)
            sb = sb.deleteCharAt(sb.length()-1);
        sb.append(" )" );
        List<UserInfochild> listUserInfochild = this.jdbcTemplate.query(
                sb.toString(),
                new BeanPropertyRowMapper<>(
                        UserInfochild.class));
        return  listUserInfochild;
    }

}
