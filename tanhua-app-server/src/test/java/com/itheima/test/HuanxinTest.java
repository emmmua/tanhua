package com.itheima.test;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class HuanxinTest {

    @Autowired
    private HuanXinTemplate template;

    @DubboReference
    private UserApi userApi;

    @Test
    public void testCreateUser() {
        template.createUser("106","123456");
    }

    @Test
    public void register() {
        for (int i = 1; i <= 106; i++) {
            User user = userApi.findById(Long.valueOf(i));
            if(user != null) {
                Boolean create = template.createUser("hx" + user.getId(), Constants.INIT_PASSWORD);
                if (create){
                    user.setHxUser("hx" + user.getId());
                    user.setHxPassword(Constants.INIT_PASSWORD);
                    userApi.update(user);
                }
            }
        }
    }
}
