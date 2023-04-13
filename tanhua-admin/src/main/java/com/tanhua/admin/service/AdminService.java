package com.tanhua.admin.service;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.dto.SystemUserDto;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public Map login(SystemUserDto systemUserDto) {
        // 1、校验验证码是否正确
        String key = Constants.CAP_CODE + systemUserDto.getUuid();
        String code = redisTemplate.opsForValue().get(key);
        if (code == null || !code.equals(systemUserDto.getVerificationCode())) {
            throw new BusinessException("验证码错误");
        }
        redisTemplate.delete(key);

        // 2、根据用户名查询管理员对象 Admin
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(systemUserDto.getUsername() != null, Admin::getUsername, systemUserDto.getUsername());
        Admin admin = adminMapper.selectOne(queryWrapper);

        // 3、判断admin对象是否存在，密码是否一致
        String password = SecureUtil.md5(systemUserDto.getPassword());
        if (Objects.isNull(admin) || !password.equals(admin.getPassword())) {
            throw new BusinessException("验证码错误");
        }

        // 4、生成token
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", admin.getId());
        tokenMap.put("username", admin.getUsername());
        String token = JwtUtils.getToken(tokenMap);


        // 5、构造返回值
        Map res = new HashMap();
        res.put("token", token);
        return res;
    }

    //获取当前用户的用户资料
    public AdminVo profile() {
        Long id = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(id);
        return AdminVo.init(admin);
    }
}
