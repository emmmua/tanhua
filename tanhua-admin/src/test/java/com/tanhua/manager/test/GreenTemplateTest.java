package com.tanhua.manager.test;

import com.tanhua.admin.AdminServerApplication;
import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminServerApplication.class)
public class GreenTemplateTest {
    @Autowired
    private AliyunGreenTemplate template;

    @Test
    public void test() throws Exception {
        Map<String, String> map = template.greenTextScan("今天是个好日子");
        map.forEach((k, v) -> System.out.println(k + "--" + v));
    }
}
