package com.liukx;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * BeanPropertyRowMapper的增强版本,加入自己的逻辑
 *
 *           String sql = "select * from tembin_loan.amazon_account order by org_id";
 *
 *         EnhanceBeanPropertyRowMapper.Enhancer amazonAccountEnhancer = new EnhanceBeanPropertyRowMapper.Enhancer() {
 *             @Override
 *             public <T> T complete(ResultSet rs, T t) throws SQLException {
 *                 String oldDataOrgId = rs.getString("org_id");
 *                 AmazonAccount a = (AmazonAccount) t;
 *                 if(StringUtils.isEmpty(a.getSubjectId())){
 *                     CreditCustomer creditCustomer = creditService.findCreditCustomer(oldDataOrgId);
 *                     if(StringUtils.isEmpty(creditCustomer.getSubjectId())){
 *                         throw new ServiceException("转换亚马逊店铺失败,subjectId为空");
 *                     }
 *                     a.setSubjectId(oldDataOrgId);
 *                 }
 *                 return (T)a;
 *             }
 *         };
 *         List<AmazonAccount> amazonAccountList = jdbcTemplate.query(sql, new EnhanceBeanPropertyRowMapper(AmazonAccount.class, amazonAccountEnhancer));
 *         amazonAccountRepository.saveAll(amazonAccountList); //数据保存,可以考虑一条条保存,try catch,因为数据量本身大不,而且考虑可能因为数据本身的问题,有些可能会保存不成功
 *
 * Created by liukx on 2020/6/11 0011.
 */
public class EnhanceBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {

    private Enhancer enhancer;

    public EnhanceBeanPropertyRowMapper() { }

    public EnhanceBeanPropertyRowMapper(Class<T> mappedClass, Enhancer enhancer) {
        super(mappedClass);
        this.enhancer = enhancer;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        T t = super.mapRow(rs, rowNumber);

        if(enhancer != null){
            return enhancer.complete(rs,t);
        }

        return t;
    }

    /**
     * @description 增强器,放在内部类,更简洁,功能性更加明确
     * @author liukx
     * @date 2020/6/11 0011
     */
    public interface Enhancer{

        <T>T complete(ResultSet rs,T t) throws SQLException;

    }

}
