package com.peait.spider.mapper;

import com.peait.spider.entity.HkPositionList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface HkPositionListMapper {
    int insert(HkPositionList record);

    int insertSelective(HkPositionList record);

    HashMap<String, String> selectAddressData();

    HashMap<String, String> selectAddressDataById(@Param("id")String id );

    List<String> selectCode();

    void updateByCode(@Param("code") String code,@Param("nowDate") String nowDate);

    List<HashMap> getData();
}