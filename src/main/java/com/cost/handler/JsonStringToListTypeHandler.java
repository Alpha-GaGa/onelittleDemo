package com.cost.handler;

import com.alibaba.fastjson.JSON;
import com.cost.domain.common.FeeCodeConditional;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(List.class)
public class JsonStringToListTypeHandler extends BaseTypeHandler<List<FeeCodeConditional>> {

    /**
     * 入库前数据类型转换
     * @param preparedStatement
     * @param i
     * @param feeCodeConditionals
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<FeeCodeConditional> feeCodeConditionals, JdbcType jdbcType) throws SQLException {
            String jsonString = JSON.toJSONString(feeCodeConditionals);
            log.info("conditional属性入库json转换{}", jsonString);
            preparedStatement.setString(i, jsonString);
    }

    /**
     * 查询后处理的数据
     * @param resultSet
     * @param s
     * @return
     * @throws SQLException
     */
    @Override
    public List<FeeCodeConditional> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String jsonString = resultSet.getString(s);
        return JSON.parseArray(jsonString, FeeCodeConditional.class);
    }

    @Override
    public List<FeeCodeConditional> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String jsonString = resultSet.getString(i);
        return JSON.parseArray(jsonString, FeeCodeConditional.class);
    }

    @Override
    public List<FeeCodeConditional> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String jsonString = callableStatement.getString(i);
        return JSON.parseArray(jsonString, FeeCodeConditional.class);
    }
}