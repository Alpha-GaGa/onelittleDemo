package com.cost.service.impl;

import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.request.FeeCodeQueryRequest;
import com.cost.mapper.FeeCodeRelMapper;
import com.cost.service.IFeeCodeRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 费用代号映射业务层
 * @Created zhangtianhao
 * @date 2023-04-19 16:01
 * @version
 */
@Service
public class FeeCodeRelServiceImpl implements IFeeCodeRelService {

    @Autowired
    private FeeCodeRelMapper feeCodeRelMapper;

    /**
     * 查询费用代号和系统代号映射数据
     * @param feeCodeQueryRequest
     * @return
     */
    @Override
    public List<SysFeeCodeDTO> selectFeeCode(FeeCodeQueryRequest feeCodeQueryRequest) {
        return feeCodeRelMapper.selectFeeCode(feeCodeQueryRequest);
    }
}
