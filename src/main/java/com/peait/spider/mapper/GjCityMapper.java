package com.peait.spider.mapper;

import com.peait.spider.entity.GjCity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GjCityMapper {
    int deleteByPrimaryKey(String cityId);

    int insert(GjCity record);

    int insertSelective(GjCity record);

    GjCity selectByPrimaryKey(String cityId);

    int updateByPrimaryKeySelective(GjCity record);

    int updateByPrimaryKey(GjCity record);

    List<GjCity> selectByUpdateStatus();
}