package com.tanhua.server.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class UserFreezeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void checkUserStatus(Integer state, Long userId) {
        String value = redisTemplate.opsForValue().get(Constants.FREEZE_USER + userId);
        if (!StringUtils.isEmpty(value)) {
            Map map = JSON.parseObject(value, Map.class);
            Integer freezingRange = Integer.valueOf(map.get("freezingRange").toString());
            if (Objects.equals(freezingRange, state)) {
                throw new BusinessException(ErrorResult.builder().errMessage("您的账号被冻结！").build());
            }
        }
    }
}
