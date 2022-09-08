package com.peait.spider.mapper;

import com.peait.spider.entity.GjVillagetr;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GjVillagetrMapper {
    int deleteByPrimaryKey(String villagetrId);

    int insert(GjVillagetr record);

    int insertSelective(GjVillagetr record);

    GjVillagetr selectByPrimaryKey(String villagetrId);

    int updateByPrimaryKeySelective(GjVillagetr record);

    int updateByPrimaryKey(GjVillagetr record);
}