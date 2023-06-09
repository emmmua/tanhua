package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.domain.Analysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AnalysisService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private AnalysisMapper analysisMapper;

    /**
     * 定时统计tb_log表中的数据，保存或跟新tb_analysis表
     * 1、查询tb_log表（注册用户数，登录用户数，活跃用户数，次日留存）
     * 2、构造Analysis对象
     * 3、保存或者更新
     */
    public void analysis() throws ParseException {
        // 1、定义查询日期
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");

        // 2、统计数据-注册数量
        Integer regCount = logMapper.queryByTypeAndLogTime("0102", todayStr);

        // 3、统计数据-登录数量
        Integer loginCount = logMapper.queryByTypeAndLogTime("0101", todayStr);

        // 4、统计数据-活跃数量
        Integer activeCount = logMapper.queryByLogTime(todayStr);

        // 5、统计数据-次日留存
        Integer numRetention1d = logMapper.queryNumRetention1d(todayStr, yesterdayStr);

        // 6、构造Analysis对象


        // 7、根据日期查询数据
        LambdaQueryWrapper<Analysis> lqw = new LambdaQueryWrapper<>();
        lqw.eq( Analysis::getRecordDate, new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
        Analysis analysis = analysisMapper.selectOne(lqw);

        // 8、如果存在，更新; 不存在，保存。
        if (analysis != null) {
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(numRetention1d);
            analysisMapper.updateById(analysis);
        } else {
            analysis = new Analysis();
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
            analysis.setCreated(new Date());
            analysisMapper.insert(analysis);
        }
    }


}
