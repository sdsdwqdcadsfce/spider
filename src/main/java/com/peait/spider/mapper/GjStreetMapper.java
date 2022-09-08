package com.peait.spider.mapper;

import com.peait.spider.entity.GjStreet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GjStreetMapper {
    int deleteByPrimaryKey(String streetId);

    int insert(GjStreet record);

    int insertSelective(GjStreet record);

    GjStreet selectByPrimaryKey(String streetId);

    int updateByPrimaryKeySelective(GjStreet record);

    int updateByPrimaryKey(GjStreet record);
}