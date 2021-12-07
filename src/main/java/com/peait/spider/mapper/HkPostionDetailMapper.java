package com.peait.spider.mapper;

import com.peait.spider.entity.HkPositionDetailBO;
import com.peait.spider.entity.HkPositionList;
import com.peait.spider.entity.HkPostionDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface HkPostionDetailMapper {
    int insert(HkPostionDetail record);

    int insertSelective(HkPostionDetail record);

    List<HkPositionList> hkPositionListMapper(@Param("date") String date);

    List<String> selectDateByCode(@Param("code") String dataCode);

    List<HkPositionDetailBO> selectDataByTime();
}