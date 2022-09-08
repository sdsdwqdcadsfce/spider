package com.peait.spider.mapper;

import com.peait.spider.entity.GjProvince;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GjProvinceMapper {
    int deleteByPrimaryKey(String provinceId);

    int insert(GjProvince record);

    int insertSelective(GjProvince record);

    GjProvince selectByPrimaryKey(String provinceId);

    int updateByPrimaryKeySelective(GjProvince record);

    int updateByPrimaryKey(GjProvince record);
}