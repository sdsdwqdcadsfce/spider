//package com.peait.spider.service.impl;
//
//import com.peait.spider.entity.HkPositionDetailBO;
//import com.peait.spider.mapper.HkPostionDetailMapper;
//import com.peait.spider.service.PositionDetailServer;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class PositionDetailServerImpl implements PositionDetailServer {
//    @Resource
//    private HkPostionDetailMapper hkPostionDetailMapper;
//    @Override
//    public List<HkPositionDetailBO> getPerLimit() {
//
//        List<HkPositionDetailBO> dataList = hkPostionDetailMapper.selectDataByTime();
//        //按照code分类
//        Map<String, List<HkPositionDetailBO>> codeGroupList = dataList.stream().collect(Collectors.groupingBy(HkPositionDetailBO::getStockCode));
//        //分类后查询组长度小于5过滤掉
//        Set<Map.Entry<String, List<HkPositionDetailBO>>> entries = codeGroupList.entrySet();
//        for (Map.Entry<String, List<HkPositionDetailBO>> map:entries) {
//            List<HkPositionDetailBO> value = map.getValue();
//            if(value.size()==5){
//                //判断1 5 差值
//                //判断 13 差值
//                //判断 12 差值
//            }
//        }
//
//
//    }
//}
