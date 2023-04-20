package com.cost.handler;

import com.alibaba.fastjson.JSON;
import com.cost.domain.common.FeeCodeConditional;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JsonStringToListTypeHandler implements TypeHandler<List<FeeCodeConditional>> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, List<FeeCodeConditional> feeCodeConditionals, JdbcType jdbcType) throws SQLException {
        if (feeCodeConditionals != null) {
            String jsonString = JSON.toJSONString(feeCodeConditionals);
            preparedStatement.setString(i, jsonString);
        } else {
            preparedStatement.setNull(i, JdbcType.VARCHAR.TYPE_CODE);
        }
    }

    @Override
    public List<FeeCodeConditional> getResult(ResultSet resultSet, String columnName) throws SQLException {
        String jsonString = resultSet.getString(columnName);
        return JSON.parseArray(jsonString, FeeCodeConditional.class);
    }

    @Override
    public List<FeeCodeConditional> getResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String jsonString = resultSet.getString(columnIndex);
        return JSON.parseArray(jsonString, FeeCodeConditional.class);
    }

    @Override
    public List<FeeCodeConditional> getResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String jsonString = callableStatement.getString(columnIndex);
        return JSON.parseArray(jsonString, FeeCodeConditional.class);
    }
}