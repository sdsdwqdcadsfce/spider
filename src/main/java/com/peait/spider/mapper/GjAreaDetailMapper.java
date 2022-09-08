package com.peait.spider.mapper;

import com.peait.spider.entity.GjAreaDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GjAreaDetailMapper {
    int deleteByPrimaryKey(String id);

    int insert(GjAreaDetail record);

    int insertSelective(GjAreaDetail record);

    GjAreaDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GjAreaDetail record);

    int updateByPrimaryKey(GjAreaDetail record);
}