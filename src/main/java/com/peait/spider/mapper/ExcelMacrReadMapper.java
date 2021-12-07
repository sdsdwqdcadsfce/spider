package com.peait.spider.mapper;

import com.peait.spider.entity.ExcelMacrRead;

public interface ExcelMacrReadMapper {
    int deleteByPrimaryKey(String id);

    int insert(ExcelMacrRead record);

    int insertSelective(ExcelMacrRead record);

    ExcelMacrRead selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ExcelMacrRead record);

    int updateByPrimaryKey(ExcelMacrRead record);
}