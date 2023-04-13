package com.tanhua.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserDto {
    // 账号
    String username;

    // 密码
    String password;

    // 验证码
    String verificationCode;

    // 同获取验证码时uuid
    String uuid;

    @Override
    public String toString() {
        return "SystemUserDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
