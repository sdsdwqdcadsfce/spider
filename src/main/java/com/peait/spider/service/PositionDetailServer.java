package com.peait.spider.service;

import com.peait.spider.entity.HkPositionDetailBO;

import java.util.HashMap;
import java.util.List;

public interface PositionDetailServer {
    List<HkPositionDetailBO> getPerLimit();
}
