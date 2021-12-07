package com.peait.spider.mapper;

import com.peait.spider.entity.HkPositionBuy;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface HkPositionBuyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HkPositionBuy record);

    int insertSelective(HkPositionBuy record);

    HkPositionBuy selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HkPositionBuy record);

    int updateByPrimaryKey(HkPositionBuy record);

    List<HashMap> selectData();
}