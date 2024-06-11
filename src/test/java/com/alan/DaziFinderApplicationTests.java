package com.alan;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alan.pojo.vo.RegdVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DaziFinderApplicationTests {
    /**
     * 测试md5加密
     */
    @Test
    void testMd5() {
        final String SALT = "ysc";
        String originalPsw = "123456";
        String encryptPsw = SecureUtil.md5(SALT+originalPsw);
        System.out.println(encryptPsw);
//        // 测试加密后的密码是否和原密码一致
//        String userPsw = "123456";
//        String EUserPsw = SecureUtil.md5(SALT+userPsw);
//        System.out.println(EUserPsw);
//        System.out.println(encryptPsw.equals(EUserPsw));
    }

    /**
     * 非空测试
     */
    @Test
    void testBlank(){
        boolean blank = StrUtil.hasBlank("/t", "null", "checkPassword");
        System.out.println(blank);
    }
    @Test
    void contextLoads() {
        List<RegdVO> regdVOS=new ArrayList<>();
        regdVOS.add(new RegdVO("2021-1-1",11));
        regdVOS.add(new RegdVO("2021-1-1",11));
        regdVOS.add(new RegdVO("2021-1-1",11));
        regdVOS.forEach(System.out::println);
    }

}
