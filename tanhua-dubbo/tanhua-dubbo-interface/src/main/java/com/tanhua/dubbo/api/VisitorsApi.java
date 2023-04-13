package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;

import java.util.List;

public interface VisitorsApi {

    //保存访客数据
    void save(Visitors visitors);

    //查询首页访客列表
    List<Visitors> queryMyVisitors(Long date, Long userId);
}
