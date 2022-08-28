package com.peait.spider.mapper;

import com.peait.spider.entity.DfStockDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DfStockDetailMapper {
    int deleteByPrimaryKey(String id);

    int insert(DfStockDetail record);

    int insertSelective(DfStockDetail record);

    DfStockDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DfStockDetail record);

    int updateByPrimaryKey(DfStockDetail record);
}