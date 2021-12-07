//package com.peait.spider.controller;
//
//import com.peait.spider.entity.HkPositionDetailBO;
//import com.peait.spider.result.Result;
//import com.peait.spider.service.PositionDetailServer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.List;
//
//@RestController
//public class PositionDetailController {
//    @Autowired
//    private PositionDetailServer positionDetailServer;
//    @GetMapping("/getPerLimit")
//    public Result getData(){
//        List<HkPositionDetailBO> dataLIst = positionDetailServer.getPerLimit();
//    }
//
//    @GetMapping("/getNumLimit")
//    public Result getData(){
//        positionDetailServer.getNumLimit();
//    }
//}
