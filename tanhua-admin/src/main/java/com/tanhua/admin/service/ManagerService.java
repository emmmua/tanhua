package com.tanhua.admin.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MovementApi movementApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public PageResult findAllUsers(Integer page, Integer pagesize) {
        IPage iPage = userInfoApi.findAll(page, pagesize);
        return new PageResult(page, pagesize, iPage.getTotal(), iPage.getRecords());
    }


    //根据id查询用户详情
    public ResponseEntity findById(Long userId) {
        UserInfo info = userInfoApi.findById(userId);
        if(Boolean.TRUE.equals(redisTemplate.hasKey(Constants.FREEZE_USER + info.getId()))) {
            info.setUserStatus("2");
        }
        return ResponseEntity.ok(info);
    }


    //根据用户id分页查询此用户发布的所有视频列表
    public PageResult findAllVideos(Integer page, Integer pagesize, Long userId) {
        return videoApi.findByUserId(page, pagesize, userId);
    }


    //查询指定用户发布的所有动态
    public PageResult findAllMovements(Integer page, Integer pagesize, Long userId, Integer state) {
        //1、调用API查询 ：（PageResult<Publish>）
        PageResult result = movementApi.findByUserId(userId,page,pagesize);
        //2、一个Publsh构造一个Movements
        List<Movement> items = ( List<Movement>)result.getItems();
        List<MovementsVo> list = new ArrayList<>();
        for (Movement item : items) {
            UserInfo userInfo = userInfoApi.findById(item.getUserId());
            MovementsVo vo = MovementsVo.init(userInfo, item);
            list.add(vo);
        }
        //3、构造返回值
        result.setItems(list);
        return result;
    }

    //用户冻结
    public Map userFreeze(Map params) {
        int freezingTime = Integer.parseInt(params.get("freezingTime").toString());

        long userId = Long.parseLong(params.get("userId").toString());

        int days = 0;
        if (freezingTime == 1) {
            days = 3;
        }
        if (freezingTime == 2) {
            days = 7;
        }
        if (freezingTime == 3) {
            days = -1;
        }
        String value = JSON.toJSONString(params);
        redisTemplate.opsForValue().set(Constants.FREEZE_USER + userId, value, days, TimeUnit.MINUTES);
        Map map = new HashMap();
        map.put("message", "冻结成功");
        return map;
    }

    //用户解冻
    public Map userUnfreeze(Map params) {
        long userId = Long.parseLong(params.get("userId").toString());
        redisTemplate.delete(Constants.FREEZE_USER + userId);
        Map map = new HashMap();
        map.put("message","解冻成功");
        return map;
    }
}
