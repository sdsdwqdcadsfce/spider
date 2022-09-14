package com.peait.spider.mapper;

import com.peait.spider.entity.GjDistrict;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GjDistrictMapper {
    int deleteByPrimaryKey(String districtId);

    int insert(GjDistrict record);

    int insertSelective(GjDistrict record);

    GjDistrict selectByPrimaryKey(String districtId);

    int updateByPrimaryKeySelective(GjDistrict record);

    int updateByPrimaryKey(GjDistrict record);

    List<GjDistrict> selectByUpdateStatus();
}